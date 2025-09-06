package ui.addcoffeescreen

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import data.Coffee
import domain.CoffeeRepository
import kotlinx.coroutines.launch

class AddCoffeeScreenModel(
    private val repository: CoffeeRepository,
    private val navigateBack: () -> Unit,
) : ScreenModel {

    fun createCoffee(
        title: String,
        origin: String,
        roaster: String,
    ) {
        screenModelScope.launch {
            repository.addCoffee(
                Coffee(
                    id = 0,
                    title = title,
                    origin = origin,
                    roaster = roaster
                )
            )

            navigateBack()
        }
    }
}
