package coffeedetailsscreen

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
) : ScreenModel {
    private val recipeList = MutableStateFlow<List<Recipe>>(emptyList())

    fun onStart() {
        screenModelScope.launch {
            recipeList.value = repository.getRecipes(coffee.id)
        }
    }

    fun getRecipes(): StateFlow<List<Recipe>> = recipeList
}
