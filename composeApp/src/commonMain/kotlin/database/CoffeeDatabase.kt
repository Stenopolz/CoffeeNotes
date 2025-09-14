package database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

@Database(entities = [CoffeeEntity::class, RecipeEntity::class], version = 1)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class CoffeeDatabase : RoomDatabase() {
    abstract fun getCoffeeDao(): CoffeeDao
    abstract fun getRecipeDao(): RecipeDao

    companion object {
        fun getCoffeeDatabase(
            builder: Builder<CoffeeDatabase>
        ): CoffeeDatabase {
            return builder
                .setDriver(BundledSQLiteDriver())
                .setQueryCoroutineContext(Dispatchers.IO)
                .build()
        }

        const val DATABASE_NAME = "coffee_room.db"
        const val COFFEE_TABLE_NAME = "coffee"
        const val RECIPE_TABLE_NAME = "recipes"
    }
}

// The Room compiler generates the `actual` implementations.
@Suppress("KotlinNoActualForExpect", "EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<CoffeeDatabase> {
    override fun initialize(): CoffeeDatabase
}
