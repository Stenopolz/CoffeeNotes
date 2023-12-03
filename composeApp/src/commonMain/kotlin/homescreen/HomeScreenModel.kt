package homescreen

import cafe.adriel.voyager.core.model.ScreenModel
import data.Coffee
import domain.CoffeeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HomeScreenModel(
    private val repository: CoffeeRepository,
) : ScreenModel {
    private val coffeeList = MutableStateFlow<List<Coffee>>(emptyList())

    fun onStart() {
        coffeeList.value = repository.getCoffeeList()
    }

    fun getCoffee(): StateFlow<List<Coffee>> = coffeeList
}