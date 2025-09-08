package ui.editrecipescreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CopyAll
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import data.Recipe
import kotlinx.coroutines.launch
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
class EditRecipeScreen(
    private val recipe: Recipe
) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = getScreenModel<EditRecipeScreenModel> {
            parametersOf(
                recipe,
                { navigator.pop() },
                { recipe: Recipe -> navigator.replace(EditRecipeScreen(recipe)) }
            )
        }
        val showConfirmationDialog by screenModel.getShowDeleteConfirmationDialog().collectAsState()
        var showMenu by remember(recipe.id) { mutableStateOf(false) }
        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()

        var temperature by remember { mutableStateOf(recipe.temperature.toString()) }
        var totalTime by remember { mutableStateOf(recipe.totalTimeSeconds.toString()) }
        var grindSize by remember { mutableStateOf(recipe.grindSize.toString()) }
        var waterAmount by remember { mutableStateOf(recipe.waterAmountGrams) }
        var coffeeWeight by remember { mutableStateOf(recipe.weightGrams) }
        var notes by remember { mutableStateOf(recipe.notes) }
        var rating by remember { mutableStateOf(recipe.rating.toString()) }

        Scaffold(
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Edit Recipe",
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
                        IconButton(
                            modifier = Modifier,
                            onClick = {
                                showMenu = true
                            }
                        ) {
                            Icon(Icons.Filled.MoreVert, contentDescription = "More")
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Text(text = "Delete", modifier = Modifier.padding(start = 8.dp))
                                },
                                leadingIcon = {
                                    Icon(Icons.Filled.Delete, contentDescription = "Delete")
                                },
                                onClick = screenModel::onDeleteClick
                            )
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "Duplicate",
                                        modifier = Modifier.padding(start = 8.dp)
                                    )
                                },
                                leadingIcon = {
                                    Icon(Icons.Filled.CopyAll, contentDescription = "Duplicate")
                                },
                                onClick = {
                                    screenModel.onDuplicateClick()
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Editing duplicated recipe")
                                    }
                                }
                            )
                        }
                    }
                )
            },
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
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

        if (showConfirmationDialog) {
            DeleteConfirmationDialog(
                onConfirm = screenModel::onConfirmDeleteClick,
                onDismiss = screenModel::onDismissDeleteClick
            )
        }
    }

    @Composable
    private fun DeleteConfirmationDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(text = "Delete Recipe")
            },
            text = {
                Text("Are you sure you want to delete this recipe?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        onConfirm()
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                Button(
                    onClick = onDismiss
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}
