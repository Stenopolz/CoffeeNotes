package di

import androidx.room.RoomDatabase
import database.CoffeeDatabase
import database.DatabaseBackupManager
import database.DatabaseBackupManagerImpl
import database.getDatabaseBuilder
import org.koin.dsl.module

actual fun platformModule() = module {
    single<RoomDatabase.Builder<CoffeeDatabase>> {
        getDatabaseBuilder()
    }
    factory<DatabaseBackupManager> { DatabaseBackupManagerImpl() }
}
