package database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipes")
data class RecipeEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val coffeeId: Int,
    val temperature: Int,
    val totalTimeSeconds: Int,
    val grindSize: Int,
    val waterAmountGrams: String,
    val weightGrams: String,
    val notes: String,
    val rating: Int
)
