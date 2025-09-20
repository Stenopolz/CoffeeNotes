package database

interface DatabaseBackupManager {
    suspend fun exportDatabase(): Result<Unit>
    suspend fun importDatabase(): Result<Unit>
}
