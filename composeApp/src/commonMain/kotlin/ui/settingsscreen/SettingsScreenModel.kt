package ui.settingsscreen

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import database.DatabaseBackupManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsScreenModel(
    private val navigateBack: () -> Unit,
    private val showMessage: (String) -> Unit,
    private val databaseBackupManager: DatabaseBackupManager,
): ScreenModel {
    private val loading = MutableStateFlow(false)

    val isLoading = loading.asStateFlow()

    fun onBackPressed() {
        if (!loading.value) {
            navigateBack()
        }
    }

    fun exportDatabase() {
        screenModelScope.launch {
            loading.value = true
            val result = databaseBackupManager.exportDatabase()
            loading.value = false
            result.fold(
                onSuccess = {
                    showMessage("Database exported successfully")
                },
                onFailure = { error ->
                    showMessage("Error: ${error.message}")
                }
            )
        }
    }

    fun importDatabase() {
        screenModelScope.launch {
            loading.value = true
            val result = databaseBackupManager.importDatabase()
            loading.value = false
            result.fold(
                onSuccess = {
                    showMessage("Database restored successfully")
                },
                onFailure = { error ->
                    showMessage("Error: ${error.message}")
                }
            )
        }
    }
}
