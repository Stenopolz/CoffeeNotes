package database

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import co.touchlab.kermit.Logger
import domain.CoffeeRepository
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.cacheDir
import io.github.vinceglb.filekit.copyTo
import io.github.vinceglb.filekit.databasesDir
import io.github.vinceglb.filekit.delete
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.openFilePicker
import io.github.vinceglb.filekit.dialogs.openFileSaver
import io.github.vinceglb.filekit.div
import io.github.vinceglb.filekit.exists
import io.github.vinceglb.filekit.path
import io.github.vinceglb.filekit.size
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class DatabaseBackupManagerImpl(
    private val coffeeRepository: CoffeeRepository
) : DatabaseBackupManager {
    override suspend fun exportDatabase(): Boolean = withContext(Dispatchers.IO) {
        try {
            // Ensure all transactions are committed
            coffeeRepository.checkpoint()

            // Get the database file

            val dbFile = FileKit.databasesDir / CoffeeDatabase.DATABASE_NAME
            if (!dbFile.exists()) {
                return@withContext false
            }

            // Create a timestamped filename
            val timestamp = Clock.System.now()
                .toLocalDateTime(TimeZone.currentSystemDefault())
                .format(
                    LocalDateTime.Format {
                        year()
                        char('-')
                        monthNumber()
                        char('-')
                        day()
                        char('_')
                        hour()
                        char('-')
                        minute()
                        char('-')
                        second()
                    }
                )
            val exportFileName = "coffee_notes_backup_$timestamp.db"

            // Show save dialog and save the database file
            saveDatabase(dbFile, exportFileName)

        } catch (e: Exception) {
            Logger.e("Export failed", e)
            false
        }
    }

    private suspend fun saveDatabase(sourceFile: PlatformFile, fileName: String): Boolean {
        return try {
            val saveFile =
                FileKit.openFileSaver(suggestedName = fileName, extension = "db") ?: return false

            // Copy the database file to the selected location
            sourceFile.copyTo(saveFile)
            return sourceFile.size() == saveFile.size()
        } catch (e: Exception) {
            Logger.e("Export failed", e)
            false
        }
    }

    override suspend fun importDatabase(): Boolean = withContext(Dispatchers.IO) {
        try {
            // Show file picker and get selected file URI
            val selectedFile = FileKit.openFilePicker(
                type = FileKitType.File("db")
            ) ?: return@withContext false

            // Validate the selected file is a valid SQLite database
            val tempFile = copyFileToTempFile(selectedFile) ?: return@withContext false

            if (!isValidSQLiteDatabase(tempFile.path)) {
                tempFile.delete()
                return@withContext false
            }

            // Close database connections to allow file replacement
            coffeeRepository.close()

            // Replace the main database file
            val success = replaceMainDatabase(tempFile)

            // Clean up temp file
            tempFile.delete()

            // Reinitialize repository after database replacement
            if (success) {
                coffeeRepository.reinitialize()
            }

            success
        } catch (e: Exception) {
            Logger.e("Import failed", e)
            false
        }
    }

    private suspend fun copyFileToTempFile(original: PlatformFile): PlatformFile? {
        return try {
            val tempFile = FileKit.cacheDir / "temp_import_db_${timestampMilliseconds()}.db"

            original.copyTo(tempFile)

            if (tempFile.exists() && tempFile.size() > 0) tempFile else null
        } catch (e: Exception) {
            Logger.e("Copy to temp file failed", e)
            null
        }
    }

    private suspend fun replaceMainDatabase(sourceFile: PlatformFile): Boolean {
        return try {
            val dbFile = FileKit.databasesDir / CoffeeDatabase.DATABASE_NAME

            // Create backup of current database before replacement
            val backupFile =
                FileKit.databasesDir / "coffee_room_backup_${timestampMilliseconds()}.db"

            if (dbFile.exists()) {
                dbFile.copyTo(backupFile)
            }

            // Replace with new database file
            sourceFile.copyTo(dbFile)

            // Verify the replacement was successful
            if (isValidSQLiteDatabase(dbFile.path)) {
                // Clean up backup file after successful replacement
                if (backupFile.exists()) {
                    backupFile.delete()
                }
                true
            } else {
                Logger.e("Restored database is not valid")
                // Restore backup if replacement failed
                if (backupFile.exists()) {
                    backupFile.copyTo(dbFile)
                    backupFile.delete()
                }
                false
            }
        } catch (e: Exception) {
            Logger.e("Database replacement failed", e)
            false
        }
    }

    private fun timestampMilliseconds(): String {
        return Clock.System.now().toEpochMilliseconds().toString()
    }

    private fun isValidSQLiteDatabase(fileUrl: String): Boolean {
        return try {
            // Check if database can be opened and has expected tables
            BundledSQLiteDriver().open(fileUrl).use { databaseConnection ->
                val hasValidTables = databaseConnection
                    .prepare("SELECT name FROM sqlite_master WHERE type='table'")
                    .use { statement ->
                        var foundCoffeeTable = false
                        var foundRecipeTable = false
                        while (statement.step()) {
                            val tableName = statement.getText(0)
                            when (tableName) {
                                CoffeeDatabase.COFFEE_TABLE_NAME -> foundCoffeeTable = true
                                CoffeeDatabase.RECIPE_TABLE_NAME -> foundRecipeTable = true
                            }
                        }
                        foundCoffeeTable && foundRecipeTable
                    }
                hasValidTables
            }
        } catch (e: Exception) {
            Logger.e("Database validation failed", e)
            false
        }
    }
}
