package ui.editcoffeescreen

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
import data.Coffee
import org.koin.core.parameter.parametersOf

class EditCoffeeScreen(
    private val coffee: Coffee,
) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        val screenModel = getScreenModel<EditCoffeeScreenModel> {
            parametersOf(coffee, { navigator.pop() })
        }

        var title by remember { mutableStateOf(coffee.title) }
        var origin by remember { mutableStateOf(coffee.origin) }
        var roaster by remember { mutableStateOf(coffee.roaster) }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Edit coffee",
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
                                screenModel.updateCoffee(title, origin, roaster)
                            }
                        ) {
                            Icon(Icons.Filled.Save, contentDescription = "Save")
                        }
                    }
                )
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
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Coffee Name") }
                )
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = origin,
                    onValueChange = { origin = it },
                    label = { Text("Country of origin") }
                )
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = roaster,
                    onValueChange = { roaster = it },
                    label = { Text("Roaster Name") }
                )
            }
        }
    }
}
