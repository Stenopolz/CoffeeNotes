package ui.editrecipescreen

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import data.Recipe
import domain.CoffeeRepository
import kotlinx.coroutines.launch

class EditRecipeScreenModel(
    private val recipe: Recipe,
    private val repository: CoffeeRepository,
    private val navigateBack: () -> Unit,
): ScreenModel {

    fun saveRecipe(
        temperature: Int,
        totalTime: Int,
        grindSize: Int,
        waterAmount: String,
        weight: String,
        notes: String,
        rating: Int,
    ) {
        screenModelScope.launch {
            repository.updateRecipe(
                recipe = recipe.copy(
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
