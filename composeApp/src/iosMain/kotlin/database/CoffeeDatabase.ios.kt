package database

import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSLog
import platform.Foundation.NSUserDomainMask

fun getDatabaseBuilder(): RoomDatabase.Builder<CoffeeDatabase> {
    val dbFilePath = documentDirectory() + CoffeeDatabase.DATABASE_NAME
    NSLog("Database builder database path: $dbFilePath")
    return Room.databaseBuilder<CoffeeDatabase>(
        name = dbFilePath,
    )
}

@OptIn(ExperimentalForeignApi::class)
private fun documentDirectory(): String {
    val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null,
    )
    requireNotNull(documentDirectory)
    if (!NSFileManager.defaultManager.fileExistsAtPath(path = documentDirectory.path + "/databases/")) {
        NSFileManager.defaultManager.createDirectoryAtPath(
            path = documentDirectory.path + "/databases/",
            withIntermediateDirectories = false,
            attributes = null,
            error = null,
        )
    }

    return requireNotNull(documentDirectory.path + "/databases/")
}
