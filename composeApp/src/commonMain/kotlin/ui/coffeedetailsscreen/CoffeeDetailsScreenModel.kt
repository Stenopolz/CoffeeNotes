package ui.coffeedetailsscreen

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import data.Coffee
import data.Recipe
import domain.CoffeeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CoffeeDetailsScreenModel(
    private val coffee: Coffee,
    private val repository: CoffeeRepository,
    private val navigateBack: () -> Unit,
) : ScreenModel {
    private val recipeList = MutableStateFlow<List<Recipe>>(emptyList())
    private val displayedCoffee = MutableStateFlow<Coffee?>(null)
    private val showDeleteConfirmationDialog = MutableStateFlow(false)

    fun onStart() {
        screenModelScope.launch {
            displayedCoffee.value = repository.getCoffee(coffee.id)
            recipeList.value = repository.getRecipes(coffee.id)
        }
    }

    fun getRecipes(): StateFlow<List<Recipe>> = recipeList

    fun getCoffee(): StateFlow<Coffee?> = displayedCoffee

    fun onDeleteClick() {
        showDeleteConfirmationDialog.value = true
    }

    fun onConfirmDeleteClick() {
        screenModelScope.launch {
            repository.removeCoffee(coffee)
            navigateBack()
        }
    }

    fun onDismissDeleteClick() {
        showDeleteConfirmationDialog.value = false
    }

    fun getShowDeleteConfirmationDialog(): StateFlow<Boolean> = showDeleteConfirmationDialog
}
