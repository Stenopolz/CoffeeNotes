package ui.editcoffeescreen

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import data.Coffee
import domain.CoffeeRepository
import kotlinx.coroutines.launch

class EditCoffeeScreenModel(
    private val coffee: Coffee,
    private val repository: CoffeeRepository,
    private val navigateBack: () -> Unit,
) : ScreenModel {

    fun updateCoffee(
        title: String,
        origin: String,
        roaster: String,
    ) {
        screenModelScope.launch {
            repository.updateCoffee(
                coffee = coffee.copy(
                    title = title,
                    origin = origin,
                    roaster = roaster
                )
            )

            navigateBack()
        }
    }
}
