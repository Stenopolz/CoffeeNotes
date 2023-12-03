package homescreen

import addcoffeescreen.AddCoffeeScreen
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.lifecycle.LifecycleEffect
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import domain.CoffeeRepositoryImpl

object HomeScreen : Screen {
    @Composable
    override fun Content() {
        val screenModel = rememberScreenModel { HomeScreenModel(CoffeeRepositoryImpl) }
        val coffeeList by screenModel.getCoffee().collectAsState()
        val navigator = LocalNavigator.currentOrThrow

        LifecycleEffect(
            onStarted = { screenModel.onStart() }
        )

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
                        onClick = {
                            navigator push AddCoffeeScreen()
                        }
                    ) {
                        Text(
                            text = "Add new coffee",
                            style = MaterialTheme.typography.h6
                        )
                    }
                }
            }
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize().background(
                    color = MaterialTheme.colors.surface
                ),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(coffeeList) { coffee ->
                    Column(
                        modifier = Modifier.padding(
                            horizontal = 24.dp,
                            vertical = 16.dp
                        )
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
        }
    }
}