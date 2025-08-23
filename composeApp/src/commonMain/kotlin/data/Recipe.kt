package data

data class Recipe(
    val id: Int = 0,
    val coffeeId: Int,
    val temperature: Int,
    val totalTimeSeconds: Int,
    val grindSize: Int,
    val waterAmountGrams: String,
    val weightGrams: String,
    val notes: String,
    val rating: Int,
)
