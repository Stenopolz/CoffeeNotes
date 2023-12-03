package addcoffeescreen

import cafe.adriel.voyager.core.model.ScreenModel
import data.Coffee
import domain.CoffeeRepository

class AddCoffeeScreenModel(
    private val repository: CoffeeRepository,
    private val navigateBack: () -> Unit,
) : ScreenModel {

    fun createCoffee(
        title: String,
        origin: String,
        roaster: String,
    ) {
        repository.addCoffee(
            Coffee(
                id = "",
                title = title,
                origin = origin,
                roaster = roaster
            )
        )

        navigateBack()
    }
}