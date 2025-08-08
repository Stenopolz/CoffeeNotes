package addnewrecipescreen

import cafe.adriel.voyager.core.model.ScreenModel
import data.Coffee
import data.Recipe
import domain.CoffeeRepository

class AddNewRecipeScreenModel(
    private val coffee: Coffee,
    private val repository: CoffeeRepository,
) : ScreenModel {

    fun createRecipe(
        temperature: Int,
        totalTime: Int,
        grindSize: Int,
        waterAmount: Int,
        weight: Int,
        notes: String,
        rating: Int,
    ) {
        repository.addRecipe(
            coffeeId = coffee.id,
            recipe = Recipe(
                id = "",
                temperature = temperature,
                totalTime = totalTime,
                grindSize = grindSize,
                waterAmountMilligrams = waterAmount,
                weightMilligrams = weight,
                notes = notes,
                rating = rating,
            )
        )
    }
}
