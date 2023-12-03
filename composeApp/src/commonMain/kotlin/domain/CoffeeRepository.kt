package domain

import data.Coffee


interface CoffeeRepository {
    fun getCoffeeList(): List<Coffee>
    fun addCoffee(coffee: Coffee)
    fun removeCoffee(coffee: Coffee)
}

object CoffeeRepositoryImpl : CoffeeRepository {
    private val coffeeList = mutableListOf<Coffee>(
            Coffee(
                id = "0",
                title = "Kenia Jopa Slona",
                origin = "Kenia",
                roaster = "Slon"
            ),
            Coffee(
                id = "1",
                title = "Kenia Morda Slona",
                origin = "Kenia",
                roaster = "Slon"
            ),
            Coffee(
                id = "2",
                title = "Kenia Uho Slona",
                origin = "Kenia",
                roaster = "Slon"
            ),
            Coffee(
                id = "3",
                title = "Kenia Hobot Slona",
                origin = "Kenia",
                roaster = "Slon"
            )
    )

    override fun getCoffeeList(): List<Coffee> {
        return coffeeList
    }

    override fun addCoffee(coffee: Coffee) {
        coffeeList.add(coffee.copy(id = "$coffeeList.size"))
    }

    override fun removeCoffee(coffee: Coffee) {
        coffeeList.remove(coffee)
    }
}