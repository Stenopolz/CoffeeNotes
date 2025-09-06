package ui.editrecipescreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import data.Recipe
import org.koin.core.parameter.parametersOf

class EditRecipeScreen(
    private val recipe: Recipe
) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = getScreenModel<EditRecipeScreenModel> {
            parametersOf(recipe, { navigator.pop() })
        }

        var temperature by remember { mutableStateOf(recipe.temperature.toString()) }
        var totalTime by remember { mutableStateOf(recipe.totalTimeSeconds.toString()) }
        var grindSize by remember { mutableStateOf(recipe.grindSize.toString()) }
        var waterAmount by remember { mutableStateOf(recipe.waterAmountGrams) }
        var coffeeWeight by remember { mutableStateOf(recipe.weightGrams) }
        var notes by remember { mutableStateOf(recipe.notes) }
        var rating by remember { mutableStateOf(recipe.rating.toString()) }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Edit Recipe",
                            style = MaterialTheme.typography.h5
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                navigator.pop()
                            }
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        IconButton(
                            modifier = Modifier,
                            onClick = {
                                screenModel.saveRecipe(
                                    temperature = temperature.toInt(),
                                    totalTime = totalTime.toInt(),
                                    grindSize = grindSize.toInt(),
                                    waterAmount = waterAmount,
                                    weight = coffeeWeight,
                                    notes = notes,
                                    rating = rating.toInt()
                                )
                            }
                        ) {
                            Icon(Icons.Filled.Save, contentDescription = "Save")
                        }
                    }
                )
            },
        ) {
            Column(
                modifier = Modifier.fillMaxSize().background(
                    color = MaterialTheme.colors.surface
                ).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = temperature,
                    onValueChange = { temperature = it },
                    label = { Text("Temperature (°C)") }
                )
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = totalTime,
                    onValueChange = { totalTime = it },
                    label = { Text("Total Brew Time (s)") }
                )
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = grindSize,
                    onValueChange = { grindSize = it },
                    label = { Text("Grind Size") }
                )
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = waterAmount,
                    onValueChange = { waterAmount = it },
                    label = { Text("Water Amount (g)") }
                )
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = coffeeWeight,
                    onValueChange = { coffeeWeight = it },
                    label = { Text("Coffee Weight (g)") }
                )
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes") }
                )
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = rating,
                    onValueChange = { rating = it },
                    label = { Text("Rating (1-10)") }
                )
            }
        }
    }
}
