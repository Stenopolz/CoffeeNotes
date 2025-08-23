package di

import androidx.room.RoomDatabase
import database.CoffeeDatabase
import database.getDatabaseBuilder
import org.koin.dsl.module

actual fun platformModule() = module {
    single<RoomDatabase.Builder<CoffeeDatabase>> {
        getDatabaseBuilder()
    }
}
