package ui.homescreen

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.snapshotFlow
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import data.Coffee
import database.DatabaseBackupManager
import domain.CoffeeRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class HomeScreenModel(
    private val repository: CoffeeRepository,
    private val databaseBackupManager: DatabaseBackupManager,
) : ScreenModel {
    val searchFieldState = TextFieldState()

    private val coffeeList = MutableStateFlow<List<Coffee>>(emptyList())
    private val _isExporting = MutableStateFlow(false)
    private val _isImporting = MutableStateFlow(false)
    val isExporting: StateFlow<Boolean> = _isExporting
    val isImporting: StateFlow<Boolean> = _isImporting

    @OptIn(FlowPreview::class)
    fun onStart() {
        screenModelScope.launch {
            snapshotFlow { searchFieldState.text }
                .debounce(200.milliseconds)
                .collect {
                    searchCoffee(it.toString())
                }
        }
    }

    fun getCoffee(): StateFlow<List<Coffee>> = coffeeList

    fun searchCoffee(query: String) {
        screenModelScope.launch {
            if (query.isNotBlank()) {
                coffeeList.value = repository.searchCoffee(query)
            } else {
                coffeeList.value = repository.getCoffeeList()
            }
        }
    }

    fun exportDatabase() {
        screenModelScope.launch {
            _isExporting.value = true
            databaseBackupManager.exportDatabase()
            _isExporting.value = false
        }
    }

    fun importDatabase() {
        screenModelScope.launch {
            _isImporting.value = true
            databaseBackupManager.importDatabase()
            coffeeList.value = repository.getCoffeeList()
            _isImporting.value = false
        }
    }
}
