package ui.homescreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.input.clearText
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
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
import cafe.adriel.voyager.navigator.currentOrThrow
import data.Coffee
import ui.addcoffeescreen.AddCoffeeScreen
import ui.coffeedetailsscreen.CoffeeDetailsScreen

@OptIn(ExperimentalMaterial3Api::class)
object HomeScreen : Screen {
    @Composable
    override fun Content() {
        val screenModel = getScreenModel<HomeScreenModel>()
        val coffeeList by screenModel.getCoffee().collectAsState()
        val navigator = LocalNavigator.currentOrThrow

        LifecycleEffect(
            onStarted = { screenModel.onStart() }
        )

        MainContent(
            navigateToDetails = { navigator push CoffeeDetailsScreen(it) },
            navigateToAddNewCoffee = { navigator push AddCoffeeScreen() },
            screenModel = screenModel,
            coffeeList = coffeeList
        )
    }

    @Composable
    private fun MainContent(
        navigateToDetails: (Coffee) -> Unit,
        navigateToAddNewCoffee: () -> Unit,
        screenModel: HomeScreenModel,
        coffeeList: List<Coffee>
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Coffee Notes",
                            style = MaterialTheme.typography.h5
                        )
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
                        onClick = navigateToAddNewCoffee
                    ) {
                        Text(
                            text = "Add new coffee",
                            style = MaterialTheme.typography.h6
                        )
                    }
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .background(
                        color = MaterialTheme.colors.surface
                    )
            ) {
                val searchFieldState = screenModel.searchFieldState
                SearchBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 16.dp,
                            vertical = 16.dp
                        ),
                    shape = SearchBarDefaults.inputFieldShape,
                    inputField = {
                        SearchBarDefaults.InputField(
                            query = searchFieldState.text.toString(),
                            onQueryChange = {
                                searchFieldState.edit { replace(0, length, it) }
                            },
                            onSearch = { /* No-op */ },
                            expanded = false,
                            onExpandedChange = { },
                            placeholder = { Text("Looking for a specific coffee?") },
                            trailingIcon = {
                                if (searchFieldState.text.isNotEmpty()) {
                                    IconButton(
                                        onClick = {
                                            searchFieldState.clearText()
                                        }
                                    ) {
                                        Icon(Icons.Filled.Clear, contentDescription = "Clear")
                                    }
                                }
                            }
                        )
                    },
                    expanded = false,
                    onExpandedChange = { /* No-op */ }
                ) { /* No-op */ }

                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(coffeeList) { index, coffee ->
                        CoffeeRow(coffee) {
                            navigateToDetails(coffee)
                        }
                        if (index < coffeeList.lastIndex) {
                            Divider()
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun CoffeeRow(
        coffee: Coffee,
        onClick: () -> Unit,
    ) {
        Column(
            modifier = Modifier
                .padding(
                    horizontal = 24.dp,
                    vertical = 16.dp
                )
                .clickable(onClick = onClick),
        ) {
            Text(
                text = coffee.title,
                style = MaterialTheme.typography.h6
            )
            Text(
                modifier = Modifier.padding(
                    vertical = 8.dp
                ),
                text = coffee.origin,
                style = MaterialTheme.typography.subtitle1
            )
            Text(
                text = coffee.roaster,
                style = MaterialTheme.typography.subtitle1
            )
        }
    }
}
