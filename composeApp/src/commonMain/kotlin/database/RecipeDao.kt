package database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface RecipeDao {
    @Insert
    suspend fun insertRecipe(recipe: RecipeEntity): Long

    @Query("SELECT * FROM recipes WHERE id = :id")
    suspend fun getRecipeById(id: Int): RecipeEntity?

    @Query("SELECT * FROM recipes WHERE coffeeId = :coffeeId ORDER BY rating DESC")
    suspend fun getRecipesByCoffeeId(coffeeId: Int): List<RecipeEntity>

    @Query("DELETE FROM recipes WHERE coffeeId = :coffeeId")
    suspend fun deleteRecipesByCoffeeId(coffeeId: Int)

    @Delete
    suspend fun deleteRecipe(recipe: RecipeEntity)

    @Update
    suspend fun updateRecipe(recipe: RecipeEntity)
}
