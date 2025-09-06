package ui.editrecipescreen

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import data.Recipe
import domain.CoffeeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EditRecipeScreenModel(
    private val recipe: Recipe,
    private val repository: CoffeeRepository,
    private val navigateBack: () -> Unit,
) : ScreenModel {
    private val showDeleteConfirmationDialog = MutableStateFlow(false)

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

    fun onDeleteClick() {
        showDeleteConfirmationDialog.value = true
    }

    fun onConfirmDeleteClick() {
        screenModelScope.launch {
            repository.removeRecipe(recipe)
            navigateBack()
        }
    }

    fun onDismissDeleteClick() {
        showDeleteConfirmationDialog.value = false
    }

    fun getShowDeleteConfirmationDialog(): StateFlow<Boolean> = showDeleteConfirmationDialog
}
