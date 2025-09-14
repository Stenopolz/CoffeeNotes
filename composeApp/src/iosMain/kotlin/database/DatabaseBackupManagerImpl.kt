package database

class DatabaseBackupManagerImpl(): DatabaseBackupManager {
   override suspend fun exportDatabase(): Boolean {
        // iOS implementation not needed for this feature
        return false
    }

    override suspend fun importDatabase(): Boolean {
        // iOS implementation not needed for this feature
        return false
    }
}
