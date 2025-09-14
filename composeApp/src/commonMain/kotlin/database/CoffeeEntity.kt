package database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = CoffeeDatabase.COFFEE_TABLE_NAME)
data class CoffeeEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val origin: String,
    val roaster: String,
)
