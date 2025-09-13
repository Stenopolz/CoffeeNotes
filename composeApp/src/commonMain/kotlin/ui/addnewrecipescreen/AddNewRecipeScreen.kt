package ui.addnewrecipescreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
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
import data.Coffee
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
class AddNewRecipeScreen(
    private val coffee: Coffee
) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = getScreenModel<AddNewRecipeScreenModel> {
            parametersOf(coffee, { navigator.pop() })
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
                            style = MaterialTheme.typography.headlineSmall
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
                                screenModel.createRecipe(
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
        ) { paddingValues ->
            Column(
                modifier = Modifier.fillMaxSize()
                    .padding(
                        top = paddingValues.calculateTopPadding(),
                        start = 16.dp,
                        end = 16.dp,
                        bottom = paddingValues.calculateBottomPadding()
                    ),
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
