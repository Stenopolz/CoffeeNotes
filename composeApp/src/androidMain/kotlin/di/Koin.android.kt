package di

import androidx.room.RoomDatabase
import database.CoffeeDatabase
import database.DatabaseBackupManager
import database.DatabaseBackupManagerImpl
import database.getDatabaseBuilder
import org.koin.dsl.module
import platformhelper.ActivityStarter

actual fun platformModule() = module {
    factory<RoomDatabase.Builder<CoffeeDatabase>> {
        getDatabaseBuilder(context = get())
    }

    single<DatabaseBackupManager> {
        DatabaseBackupManagerImpl(
            context = get(),
            coffeeRepository = get(),
            activityStarter = get()
        )
    }

    single {
        ActivityStarter()
    }
}
