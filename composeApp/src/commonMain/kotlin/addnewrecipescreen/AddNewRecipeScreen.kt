package addnewrecipescreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import data.Coffee
import domain.CoffeeRepositoryImpl

class AddNewRecipeScreen(
    private val coffee: Coffee
) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel {
            AddNewRecipeScreenModel(
                coffee = coffee,
                repository = CoffeeRepositoryImpl,
            )
        }

        var temperature by remember { mutableStateOf("") }
        var totalTime by remember { mutableStateOf("") }
        var grindSize by remember { mutableStateOf("") }
        var waterAmount by remember { mutableStateOf("") }
        var coffeeWeight by remember { mutableStateOf("") }
        var notes by remember { mutableStateOf("") }
        var rating by remember { mutableStateOf("") }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "New Recipe",
                            style = MaterialTheme.typography.h5
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                navigator.pop()
                            }
                        ) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            },
            bottomBar = {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        modifier = Modifier.padding(vertical = 8.dp),
                        onClick = {
                            screenModel.createRecipe(
                                temperature = temperature.toInt(),
                                totalTime = totalTime.toInt(),
                                grindSize = grindSize.toInt(),
                                waterAmount = waterAmount.toInt(),
                                weight = coffeeWeight.toInt(),
                                notes = notes,
                                rating = rating.toInt()
                            )
                        }
                    ) {
                        Text(
                            text = "Save",
                            style = MaterialTheme.typography.h6
                        )
                    }
                }
            }
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
                    label = { Text("Water Amount (mg)") }
                )
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = coffeeWeight,
                    onValueChange = { coffeeWeight = it },
                    label = { Text("Coffee Weight (mg)") }
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
                    label = { Text("Rating (1-5)") }
                )
            }
        }
    }
}
