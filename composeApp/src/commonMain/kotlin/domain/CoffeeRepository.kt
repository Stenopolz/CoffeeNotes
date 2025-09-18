package domain

import androidx.room.RoomRawQuery
import co.touchlab.kermit.Logger
import data.Coffee
import data.Recipe
import database.CoffeeDao
import database.CoffeeDatabase
import database.CoffeeEntity
import database.RecipeDao
import database.RecipeEntity

interface CoffeeRepository {
    suspend fun getCoffeeList(): List<Coffee>
    suspend fun getCoffee(coffeeId: Int): Coffee
    suspend fun searchCoffee(query: String): List<Coffee>
    suspend fun addCoffee(coffee: Coffee)
    suspend fun updateCoffee(coffee: Coffee)
    suspend fun removeCoffee(coffee: Coffee)
    suspend fun getRecipes(coffeeId: Int): List<Recipe>
    suspend fun addRecipe(recipe: Recipe): Recipe
    suspend fun updateRecipe(recipe: Recipe)
    suspend fun removeRecipe(recipe: Recipe)
    suspend fun checkpoint()
    suspend fun close()
    suspend fun reinitialize()
}

class CoffeeRepositoryImpl(
    private val getDatabase: () -> CoffeeDatabase
) : CoffeeRepository {

    private var database = getDatabase()
    private var coffeeDao: CoffeeDao = database.getCoffeeDao()
    private var recipeDao: RecipeDao = database.getRecipeDao()

    override suspend fun getCoffeeList(): List<Coffee> {
        return coffeeDao.getAllCoffee().map { it.toAppData() }
    }

    override suspend fun searchCoffee(query: String): List<Coffee> {
        return coffeeDao.searchCoffee(query).map { it.toAppData() }
    }

    override suspend fun getCoffee(coffeeId: Int): Coffee {
        return coffeeDao.getCoffeeById(coffeeId)?.toAppData()
            ?: throw IllegalArgumentException("Coffee with id $coffeeId not found")
    }

    override suspend fun addCoffee(coffee: Coffee) {
        coffeeDao.insertCoffee(coffee.toEntity())
    }

    override suspend fun updateCoffee(coffee: Coffee) {
        coffeeDao.updateCoffee(coffee.toEntity())
    }

    override suspend fun removeCoffee(coffee: Coffee) {
        recipeDao.deleteRecipesByCoffeeId(coffee.id)
        coffeeDao.deleteCoffee(coffee.toEntity())
    }

    override suspend fun getRecipes(coffeeId: Int): List<Recipe> {
        return recipeDao.getRecipesByCoffeeId(coffeeId).map { it.toAppData() }
    }

    override suspend fun addRecipe(recipe: Recipe): Recipe {
        val recipeId = recipeDao.insertRecipe(recipe.toEntity())
        val recipe = recipeDao.getRecipeById(recipeId.toInt())!! // Should never be null
        return recipe.toAppData()
    }

    override suspend fun updateRecipe(recipe: Recipe) {
        recipeDao.updateRecipe(recipe.toEntity())
    }

    override suspend fun removeRecipe(recipe: Recipe) {
        recipeDao.deleteRecipe(recipe.toEntity())
    }

    override suspend fun checkpoint() {
        coffeeDao.checkpoint(RoomRawQuery("PRAGMA wal_checkpoint(TRUNCATE)"))
    }

    override suspend fun close() {
        database.close()
    }

    override suspend fun reinitialize() {
        Logger.e("Reinitializing database")
        database = getDatabase()
        coffeeDao = database.getCoffeeDao()
        recipeDao = database.getRecipeDao()
    }

    fun CoffeeEntity.toAppData(): Coffee {
        Logger.e("Mapping CoffeeEntity to Coffee: $this")
        return Coffee(
            id = this.id,
            title = this.title,
            origin = this.origin,
            roaster = this.roaster
        )
    }

    fun Coffee.toEntity(): CoffeeEntity {
        return CoffeeEntity(
            id = this.id,
            title = this.title,
            origin = this.origin,
            roaster = this.roaster
        )
    }

    fun RecipeEntity.toAppData(): Recipe {
        return Recipe(
            id = this.id,
            coffeeId = this.coffeeId,
            temperature = this.temperature,
            totalTimeSeconds = this.totalTimeSeconds,
            grindSize = this.grindSize,
            waterAmountGrams = this.waterAmountGrams,
            weightGrams = this.weightGrams,
            notes = this.notes,
            rating = this.rating
        )
    }

    fun Recipe.toEntity(): RecipeEntity {
        return RecipeEntity(
            id = this.id,
            coffeeId = this.coffeeId,
            temperature = this.temperature,
            totalTimeSeconds = this.totalTimeSeconds,
            grindSize = this.grindSize,
            waterAmountGrams = this.waterAmountGrams,
            weightGrams = this.weightGrams,
            notes = this.notes,
            rating = this.rating
        )
    }
}
