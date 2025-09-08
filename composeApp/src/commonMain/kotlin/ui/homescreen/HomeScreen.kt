package ui.homescreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
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
                            style = MaterialTheme.typography.headlineSmall
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
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                }
            }
        ) { paddingValues ->
            val searchFieldState = screenModel.searchFieldState


            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = paddingValues.calculateTopPadding(),
                        bottom = paddingValues.calculateBottomPadding(),
                        start = 16.dp,
                        end = 16.dp
                    ),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                stickyHeader {
                    SearchBarRow(searchFieldState)
                }
                itemsIndexed(coffeeList) { index, coffee ->
                    CoffeeRow(coffee, isLastItem = index == coffeeList.lastIndex) {
                        navigateToDetails(coffee)
                    }
                }
            }
        }
    }

    @Composable
    private fun SearchBarRow(searchFieldState: TextFieldState) {
        SearchBar(
            modifier = Modifier
                .padding(bottom = 16.dp)
                .fillMaxWidth(),
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
    }

    @Composable
    private fun CoffeeRow(
        coffee: Coffee,
        isLastItem: Boolean,
        onClick: () -> Unit,
    ) {
        Column(
            modifier = Modifier
                .clickable(onClick = onClick),
        ) {
            Text(
                text = coffee.title,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                modifier = Modifier.padding(
                    vertical = 8.dp
                ),
                text = coffee.origin,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = coffee.roaster,
                style = MaterialTheme.typography.titleMedium
            )
            if (!isLastItem) {
                HorizontalDivider(
                    modifier = Modifier.padding(
                        top = 8.dp
                    )
                )
            }
        }
    }
}
