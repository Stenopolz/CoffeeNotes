package ui.coffeedetailsscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.lifecycle.LifecycleEffect
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import data.Coffee
import data.Recipe
import org.koin.core.parameter.parametersOf
import ui.addnewrecipescreen.AddNewRecipeScreen
import ui.editcoffeescreen.EditCoffeeScreen
import ui.editrecipescreen.EditRecipeScreen

class CoffeeDetailsScreen(
    private val coffee: Coffee
) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = getScreenModel<CoffeeDetailsScreenModel> {
            parametersOf(coffee, { navigator.pop() })
        }
        val displayedCoffee by screenModel.getCoffee().collectAsState()
        val recipeList by screenModel.getRecipes().collectAsState()
        val showConfirmationDialog by screenModel.getShowDeleteConfirmationDialog().collectAsState()

        LifecycleEffect(
            onStarted = { screenModel.onStart() }
        )

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                navigator.pop()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                screenModel.onDeleteClick()
                            }
                        ) {
                            Icon(Icons.Filled.Delete, contentDescription = "Delete")
                        }
                        IconButton(
                            onClick = {
                                navigator push EditCoffeeScreen(coffee)
                            }
                        ) {
                            Icon(Icons.Filled.Edit, contentDescription = "Edit")
                        }
                    }
                )
            },
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier.fillMaxSize()
                    .padding(paddingValues)
                    .background(
                        color = MaterialTheme.colors.surface
                    ),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(
                    horizontal = 24.dp
                )
            ) {
                item {
                    CoffeeHeaderRow(displayedCoffee)
                }
                item {
                    RecipesHeaderRow(navigator)
                }
                item {
                    Divider(color = MaterialTheme.colors.primary)
                }
                itemsIndexed(recipeList) { index, recipe ->
                    RecipeRow(
                        recipe = recipe,
                        onClick = { navigator push EditRecipeScreen(recipe) }
                    )
                    if (index < recipeList.lastIndex) {
                        Divider()
                    }
                }
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
    private fun CoffeeHeaderRow(displayedCoffee: Coffee?) {
        displayedCoffee?.let { displayedCoffee ->
            Text(
                modifier = Modifier.padding(
                    top = 16.dp,
                ),
                text = "Name: ${displayedCoffee.title}",
                style = MaterialTheme.typography.h6
            )
            Text(
                modifier = Modifier.padding(
                    top = 16.dp,
                ),
                text = "Origin: ${displayedCoffee.origin}",
                style = MaterialTheme.typography.h6
            )
            Text(
                modifier = Modifier.padding(
                    vertical = 16.dp,
                ),
                text = "Roaster: ${displayedCoffee.roaster}",
                style = MaterialTheme.typography.h6
            )
        } ?: run {
            Text(
                modifier = Modifier.padding(
                    vertical = 16.dp,
                ),
                text = "Loading...",
                style = MaterialTheme.typography.h6
            )
        }
    }

    @Composable
    private fun RecipesHeaderRow(navigator: Navigator) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                modifier = Modifier.padding(
                    vertical = 20.dp,
                ),
                text = "Recipes:",
                style = MaterialTheme.typography.h5
            )

            IconButton(
                onClick = {
                    navigator push AddNewRecipeScreen(coffee)
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    tint = MaterialTheme.colors.primary,
                    contentDescription = "Add Recipe",
                )
            }
        }
    }

    @Composable
    private fun RecipeRow(
        recipe: Recipe,
        onClick: () -> Unit,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.padding(
                    vertical = 16.dp
                )
            ) {
                Text(
                    text = "Temperature: ${recipe.temperature}",
                    style = MaterialTheme.typography.subtitle1
                )
                Text(
                    modifier = Modifier.padding(
                        top = 8.dp
                    ),
                    text = "Water Amount: ${recipe.waterAmountGrams} g",
                    style = MaterialTheme.typography.subtitle1
                )
                Text(
                    modifier = Modifier.padding(
                        top = 8.dp
                    ),
                    text = "Coffee Amount: ${recipe.weightGrams} g",
                    style = MaterialTheme.typography.subtitle1
                )
                Text(
                    modifier = Modifier.padding(
                        top = 8.dp
                    ),
                    text = "Grind Setting: ${recipe.grindSize}",
                    style = MaterialTheme.typography.subtitle1
                )
            }
            Text(
                text = "Rating: ${recipe.rating}",
                style = MaterialTheme.typography.subtitle1
            )
        }
    }

    @Composable
    private fun DeleteConfirmationDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(text = "Delete Coffee")
            },
            text = {
                Text("Are you sure you want to delete this coffee and all of its recipes?")
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
