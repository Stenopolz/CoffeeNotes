package database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

fun getDatabaseBuilder(context: Context): RoomDatabase.Builder<CoffeeDatabase> {
    val appContext = context.applicationContext
    val dbFile = appContext.getDatabasePath(CoffeeDatabase.DATABASE_NAME)
    return Room.databaseBuilder<CoffeeDatabase>(
        context = appContext,
        name = dbFile.absolutePath
    )
}
