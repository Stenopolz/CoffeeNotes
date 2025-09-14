package database

interface DatabaseBackupManager {
    suspend fun exportDatabase(): Boolean
    suspend fun importDatabase(): Boolean
}
