package data

data class Recipe(
    val id: String,
    val temperature: Int,
    val totalTime: Int,
    val grindSize: Int,
    val waterAmountMilligrams: Int,
    val weightMilligrams: Int,
    val notes: String,
    val rating: Int,
)
