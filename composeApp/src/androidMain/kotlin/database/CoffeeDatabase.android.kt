package database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

fun getDatabaseBuilder(context: Context): RoomDatabase.Builder<CoffeeDatabase> {
    val appContext = context.applicationContext
    val dbFile = appContext.getDatabasePath("coffee_room.db")
    return Room.databaseBuilder<CoffeeDatabase>(
        context = appContext,
        name = dbFile.absolutePath
    )
}
