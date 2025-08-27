package homescreen

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.snapshotFlow
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import data.Coffee
import domain.CoffeeRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class HomeScreenModel(
    private val repository: CoffeeRepository,
) : ScreenModel {
    val searchFieldState = TextFieldState()

    private val coffeeList = MutableStateFlow<List<Coffee>>(emptyList())

    @OptIn(FlowPreview::class)
    fun onStart() {
        screenModelScope.launch {
            snapshotFlow { searchFieldState.text }
                .debounce(200.milliseconds)
                .collect {
                    searchCoffee(it.toString())
                }
        }
    }

    fun getCoffee(): StateFlow<List<Coffee>> = coffeeList

    fun searchCoffee(query: String) {
        screenModelScope.launch {
            if (query.isNotBlank()) {
                coffeeList.value = repository.searchCoffee(query)
            } else {
                coffeeList.value = repository.getCoffeeList()
            }
        }
    }
}
