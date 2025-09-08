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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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

@OptIn(ExperimentalMaterial3Api::class)
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
                    .padding(paddingValues),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(
                    horizontal = 16.dp
                )
            ) {
                stickyHeader {
                    CoffeeHeaderRow(displayedCoffee)
                }
                item {
                    RecipesHeaderRow(navigator)
                }
                item {
                    HorizontalDivider(color = MaterialTheme.colorScheme.primary)
                }
                itemsIndexed(recipeList) { index, recipe ->
                    RecipeRow(
                        recipe = recipe,
                        onClick = { navigator push EditRecipeScreen(recipe) }
                    )
                    if (index < recipeList.lastIndex) {
                        HorizontalDivider()
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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
        ) {
            displayedCoffee?.let { displayedCoffee ->
                Text(
                    modifier = Modifier.padding(
                        top = 16.dp,
                    ),
                    text = "Name: ${displayedCoffee.title}",
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    modifier = Modifier.padding(
                        top = 16.dp,
                    ),
                    text = "Origin: ${displayedCoffee.origin}",
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    modifier = Modifier.padding(
                        vertical = 16.dp,
                    ),
                    text = "Roaster: ${displayedCoffee.roaster}",
                    style = MaterialTheme.typography.titleLarge
                )
            } ?: run {
                Text(
                    modifier = Modifier.padding(
                        vertical = 16.dp,
                    ),
                    text = "Loading...",
                    style = MaterialTheme.typography.titleLarge
                )
            }
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
                style = MaterialTheme.typography.headlineSmall
            )

            IconButton(
                onClick = {
                    navigator push AddNewRecipeScreen(coffee)
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    tint = MaterialTheme.colorScheme.primary,
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
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    modifier = Modifier.padding(
                        top = 8.dp
                    ),
                    text = "Water Amount: ${recipe.waterAmountGrams} g",
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    modifier = Modifier.padding(
                        top = 8.dp
                    ),
                    text = "Coffee Amount: ${recipe.weightGrams} g",
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    modifier = Modifier.padding(
                        top = 8.dp
                    ),
                    text = "Grind Setting: ${recipe.grindSize}",
                    style = MaterialTheme.typography.titleSmall
                )
            }
            Text(
                text = "Rating: ${recipe.rating}",
                style = MaterialTheme.typography.titleSmall
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
