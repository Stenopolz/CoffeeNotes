package addnewrecipescreen

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import data.Coffee
import data.Recipe
import domain.CoffeeRepository
import kotlinx.coroutines.launch

class AddNewRecipeScreenModel(
    private val coffee: Coffee,
    private val repository: CoffeeRepository,
    private val navigateBack: () -> Unit,
) : ScreenModel {

    fun createRecipe(
        temperature: Int,
        totalTime: Int,
        grindSize: Int,
        waterAmount: String,
        weight: String,
        notes: String,
        rating: Int,
    ) {
        screenModelScope.launch {
            repository.addRecipe(
                recipe = Recipe(
                    coffeeId = coffee.id,
                    temperature = temperature,
                    totalTimeSeconds = totalTime,
                    grindSize = grindSize,
                    waterAmountGrams = waterAmount,
                    weightGrams = weight,
                    notes = notes,
                    rating = rating,
                )
            )

            navigateBack()
        }
    }
}
