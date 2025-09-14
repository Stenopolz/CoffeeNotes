package com.stenopolz.coffeenotes

import App
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.android.scope.AndroidScopeComponent
import org.koin.androidx.scope.activityScope
import org.koin.core.scope.Scope
import platformhelper.ActivityStarter
import platformhelper.FileUriRequest

class MainActivity : ComponentActivity(), AndroidScopeComponent {

    override val scope: Scope by activityScope()
    val activityStarter: ActivityStarter by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            activityStarter.fileUriRequests.collect { request ->
                when (request) {
                    is FileUriRequest.Save -> handleSaveRequest(request)
                    is FileUriRequest.Load -> handleLoadRequest(request)
                }
            }
        }

        setContent {
            App()
        }
    }

    override fun onStart() {
        super.onStart()
    }

    private fun handleSaveRequest(request: FileUriRequest.Save) {
        val launcher = activityResultRegistry.register(
            "database_save_${System.currentTimeMillis()}",
            ActivityResultContracts.CreateDocument(request.mimeType)
        ) { uri ->
            request.onResult(uri)
        }

        launcher.launch(request.fileName)
    }

    private fun handleLoadRequest(request: FileUriRequest.Load) {
        val launcher = activityResultRegistry.register(
            "database_import_${System.currentTimeMillis()}",
            ActivityResultContracts.OpenDocument()
        ) { uri ->
            request.onResult(uri)
        }

        launcher.launch(arrayOf(request.mimeType))
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
