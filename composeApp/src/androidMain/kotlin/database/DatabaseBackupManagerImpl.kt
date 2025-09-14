package database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import android.util.Log
import domain.CoffeeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import platformhelper.ActivityStarter
import platformhelper.FileUriRequest
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.coroutines.resume

class DatabaseBackupManagerImpl(
    private val context: Context,
    private val coffeeRepository: CoffeeRepository,
    private val activityStarter: ActivityStarter,
) : DatabaseBackupManager {

    override suspend fun exportDatabase(): Boolean = withContext(Dispatchers.IO) {
        try {
            // Ensure all transactions are committed
            coffeeRepository.checkpoint()

            // Get the database file
            val dbFile = context.getDatabasePath(CoffeeDatabase.DATABASE_NAME)
            if (!dbFile.exists()) {
                return@withContext false
            }

            // Create a timestamped filename
            val dateFormat = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault())
            val timestamp = dateFormat.format(Date())
            val exportFileName = "coffee_notes_backup_$timestamp.db"

            // Show save dialog and save the database file
            saveDatabase(dbFile, exportFileName)

        } catch (e: Exception) {
            Log.e(TAG, "Export failed", e)
            false
        }
    }

    private suspend fun saveDatabase(sourceFile: File, fileName: String): Boolean {
        return try {
            val saveUri = showSaveDialog(fileName) ?: return false

            // Copy the database file to the selected location
            context.contentResolver.openOutputStream(saveUri)?.use { outputStream ->
                FileInputStream(sourceFile).use { inputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            true
        } catch (e: Exception) {
            Log.e(TAG, "Export failed", e)
            false
        }
    }

    private suspend fun showSaveDialog(fileName: String): Uri? =
        suspendCancellableCoroutine { continuation ->
            activityStarter.requestFileSavingUri(
                FileUriRequest.Save(
                    fileName = fileName,
                    mimeType = MIME_TYPE,
                ) {
                    continuation.resume(it)
                }
            )
        }

    override suspend fun importDatabase(): Boolean = withContext(Dispatchers.IO) {
        try {
            // Show file picker and get selected file URI
            val selectedUri = showFilePicker() ?: return@withContext false

            // Validate the selected file is a valid SQLite database
            val tempFile = copyUriToTempFile(selectedUri) ?: return@withContext false

            if (!isValidSQLiteDatabase(tempFile)) {
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
            Log.e(TAG, "Import failed", e)
            false
        }
    }

    private suspend fun showFilePicker(): Uri? = suspendCancellableCoroutine { continuation ->
        activityStarter.requestFileLoadingUri(
            FileUriRequest.Load(mimeType = MIME_TYPE,) {
                continuation.resume(it)
            }
        )
    }

    private fun copyUriToTempFile(uri: Uri): File? {
        return try {
            val tempFile = File(
                context.cacheDir,
                "temp_import_db_${System.currentTimeMillis()}.db"
            )

            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                FileOutputStream(tempFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }

            if (tempFile.exists() && tempFile.length() > 0) tempFile else null
        } catch (e: Exception) {
            Log.e(TAG, "Import failed", e)
            null
        }
    }

    private fun isValidSQLiteDatabase(file: File): Boolean {
        return try {
            // Check if file starts with SQLite header
            FileInputStream(file).use { inputStream ->
                val header = ByteArray(16)
                val bytesRead = inputStream.read(header)
                if (bytesRead < 16) return false

                val sqliteHeader = "SQLite format 3\u0000".toByteArray()
                header.contentEquals(sqliteHeader)
            } && run {
                // Check if the database can be opened and has expected tables
                try {
                    SQLiteDatabase.openDatabase(
                        file.absolutePath,
                        null,
                        SQLiteDatabase.OPEN_READONLY
                    ).use { database ->
                        val cursor = database.rawQuery(
                            "SELECT name FROM sqlite_master WHERE type='table'",
                            null
                        )
                        val hasValidTables = cursor.use {
                            var foundCoffeeTable = false
                            var foundRecipeTable = false
                            while (it.moveToNext()) {
                                val tableName = it.getString(0)
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
                    Log.e(TAG, "Import failed", e)
                    false
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Import failed", e)
            false
        }
    }

    private fun replaceMainDatabase(sourceFile: File): Boolean {
        return try {
            val dbFile = context.getDatabasePath(CoffeeDatabase.DATABASE_NAME)

            // Create backup of current database before replacement
            val backupFile = File(
                dbFile.parent,
                "coffee_room_backup_${System.currentTimeMillis()}.db"
            )
            if (dbFile.exists()) {
                FileInputStream(dbFile).use { input ->
                    FileOutputStream(backupFile).use { output ->
                        input.copyTo(output)
                    }
                }
            }

            // Replace with new database file
            FileInputStream(sourceFile).use { input ->
                FileOutputStream(dbFile).use { output ->
                    input.copyTo(output)
                }
            }

            // Verify the replacement was successful
            if (isValidSQLiteDatabase(dbFile)) {
                // Clean up backup file after successful replacement
                backupFile.delete()
                true
            } else {
                // Restore backup if replacement failed
                if (backupFile.exists()) {
                    FileInputStream(backupFile).use { input ->
                        FileOutputStream(dbFile).use { output ->
                            input.copyTo(output)
                        }
                    }
                    backupFile.delete()
                }
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Import failed", e)
            false
        }
    }
    
    companion object {
        private const val TAG = "DatabaseBackupManager"
        private const val MIME_TYPE = "application/octet-stream"
    }
}
