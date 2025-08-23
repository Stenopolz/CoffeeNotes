package domain

import data.Coffee
import data.Recipe
import database.CoffeeDao
import database.CoffeeEntity
import database.RecipeDao
import database.RecipeEntity

interface CoffeeRepository {
    suspend fun getCoffeeList(): List<Coffee>
    suspend fun addCoffee(coffee: Coffee)
    suspend fun removeCoffee(coffee: Coffee)
    suspend fun getRecipes(coffeeId: Int): List<Recipe>
    suspend fun addRecipe(recipe: Recipe)
    suspend fun removeRecipe(recipe: Recipe)
}

class CoffeeRepositoryImpl(
    private val coffeeDao: CoffeeDao,
    private val recipeDao: RecipeDao,
) : CoffeeRepository {
    override suspend fun getCoffeeList(): List<Coffee> {
        return coffeeDao.getAllCoffee().map { it.toAppData()}
    }

    override suspend fun addCoffee(coffee: Coffee) {
        coffeeDao.insertCoffee(coffee.toEntity())
    }

    override suspend fun removeCoffee(coffee: Coffee) {
        coffeeDao.deleteCoffee(coffee.toEntity())
    }

    override suspend fun getRecipes(coffeeId: Int): List<Recipe> {
        return recipeDao.getRecipesByCoffeeId(coffeeId).map { it.toAppData() }
    }

    override suspend fun addRecipe(recipe: Recipe) {
        recipeDao.insertRecipe(recipe.toEntity())
    }

    override suspend fun removeRecipe(recipe: Recipe) {
        recipeDao.deleteRecipe(recipe.toEntity())
    }
    
    fun CoffeeEntity.toAppData(): Coffee {
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
