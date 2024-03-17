package domain

import data.Coffee
import data.Recipe


interface CoffeeRepository {
    fun getCoffeeList(): List<Coffee>
    fun addCoffee(coffee: Coffee)
    fun removeCoffee(coffee: Coffee)
    fun getRecipes(coffeeId: String): List<Recipe>
    fun addRecipe(coffeeId: String, recipe: Recipe)
    fun removeRecipe(coffeeId: String, recipeId: String)
}

object CoffeeRepositoryImpl : CoffeeRepository {
    private val coffeeList = mutableListOf<Coffee>(
            Coffee(
                id = "0",
                title = "Kenia Jopa Slona",
                origin = "Kenia",
                roaster = "Slon"
            ),
        Coffee(
            id = "1",
            title = "Kenia Morda Slona",
            origin = "Kenia",
            roaster = "Slon"
        ),
        Coffee(
            id = "2",
            title = "Kenia Uho Slona",
            origin = "Kenia",
            roaster = "Slon"
        ),
        Coffee(
            id = "3",
            title = "Kenia Hobot Slona",
            origin = "Kenia",
            roaster = "Slon"
        )
    )

    private val recipes = mutableMapOf<String, List<Recipe>>(
        "0" to listOf(
            Recipe(
                id = "0",
                temperature = 98,
                totalTime = 180,
                grindSize = 0,
                waterAmountMilligrams = 200000,
                weightMilligrams = 15000,
                notes = "Very good",
                rating = 50,
            ),
            Recipe(
                id = "1",
                temperature = 98,
                totalTime = 190,
                grindSize = 8,
                waterAmountMilligrams = 200000,
                weightMilligrams = 15000,
                notes = "Bitter",
                rating = 50,
            ),
        ),
        "1" to listOf(
            Recipe(
                id = "2",
                temperature = 98,
                totalTime = 180,
                grindSize = 0,
                waterAmountMilligrams = 200000,
                weightMilligrams = 15000,
                notes = "Very good",
                rating = 50,
            ),
        ),
        "2" to listOf(
            Recipe(
                id = "3",
                temperature = 98,
                totalTime = 180,
                grindSize = 0,
                waterAmountMilligrams = 200000,
                weightMilligrams = 15000,
                notes = "Very good",
                rating = 50,
            ),
        )
    )

    override fun getCoffeeList(): List<Coffee> {
        return coffeeList
    }

    override fun addCoffee(coffee: Coffee) {
        coffeeList.add(coffee.copy(id = "$coffeeList.size"))
    }

    override fun removeCoffee(coffee: Coffee) {
        coffeeList.remove(coffee)
    }

    override fun getRecipes(coffeeId: String): List<Recipe> {
        return recipes[coffeeId] ?: emptyList()
    }

    override fun addRecipe(coffeeId: String, recipe: Recipe) {
        val currentRecipes = recipes[coffeeId] ?: emptyList()
        recipes[coffeeId] = currentRecipes + recipe
    }

    override fun removeRecipe(coffeeId: String, recipeId: String) {
        val currentRecipes = recipes[coffeeId] ?: emptyList()
        recipes[coffeeId] = currentRecipes.filter { it.id != recipeId }
    }
}
