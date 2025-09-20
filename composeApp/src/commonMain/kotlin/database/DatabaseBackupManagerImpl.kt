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
import kotlinx.io.IOException
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class DatabaseBackupManagerImpl(
    private val coffeeRepository: CoffeeRepository
) : DatabaseBackupManager {
    override suspend fun exportDatabase(): Result<Unit> = runCatching {
        withContext(Dispatchers.IO) {
            // Ensure all transactions are committed
            coffeeRepository.checkpoint()

            // Get the database file
            val dbFile = FileKit.databasesDir / CoffeeDatabase.DATABASE_NAME
            if (!dbFile.exists()) {
                throw IllegalStateException("Database file does not exist")
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
        }
    }

    private suspend fun saveDatabase(sourceFile: PlatformFile, fileName: String) {
        val saveFile = FileKit.openFileSaver(suggestedName = fileName, extension = "db")
            ?: throw IllegalStateException("File saving cancelled")

        // Copy the database file to the selected location
        sourceFile.copyTo(saveFile)

        if (sourceFile.size() != saveFile.size()) {
            throw IOException("File copy failed, size mismatch")
        }
    }

    override suspend fun importDatabase(): Result<Unit> = runCatching {
        withContext(Dispatchers.IO) {
            // Show file picker and get selected file URI
            val selectedFile = FileKit.openFilePicker(
                type = FileKitType.File("db")
            ) ?: throw IllegalStateException("File selection cancelled")

            // Validate the selected file is a valid SQLite database
            val tempFile = copyFileToTempFile(selectedFile)

            if (!isValidSQLiteDatabase(tempFile.path)) {
                tempFile.delete()
                throw IllegalArgumentException("Selected file is not a valid database")
            }

            // Close database connections to allow file replacement
            coffeeRepository.close()

            // Replace the main database file
            replaceMainDatabase(tempFile)

            // Clean up temp file
            tempFile.delete()

            // Reinitialize repository after database file manipulations
            coffeeRepository.reinitialize()
        }
    }

    private suspend fun copyFileToTempFile(original: PlatformFile): PlatformFile {
        val tempFile = FileKit.cacheDir / "temp_import_db_${timestampMilliseconds()}.db"

        original.copyTo(tempFile)

        return if (tempFile.exists() && tempFile.size() > 0) tempFile
        else throw IOException("Failed to copy to temp file")
    }

    private suspend fun replaceMainDatabase(sourceFile: PlatformFile) {
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
        } else {
            Logger.e("Restored database is not valid")
            // Restore backup if replacement failed
            if (backupFile.exists()) {
                backupFile.copyTo(dbFile)
                backupFile.delete()
            }
            throw IOException("Database replacement failed, original restored")
        }
    }

    private fun timestampMilliseconds(): String {
        return Clock.System.now().toEpochMilliseconds().toString()
    }

    private fun isValidSQLiteDatabase(fileUrl: String): Boolean {
        // Check if database can be opened and has expected tables
        return BundledSQLiteDriver().open(fileUrl).use { databaseConnection ->
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
    }
}
