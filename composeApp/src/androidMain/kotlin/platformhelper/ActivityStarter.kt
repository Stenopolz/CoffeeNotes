package platformhelper

import android.net.Uri
import android.util.Log
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow

class ActivityStarter {
    val fileUriRequests = MutableSharedFlow<FileUriRequest>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    fun requestFileSavingUri(request: FileUriRequest.Save) {
        val result = fileUriRequests.tryEmit(request)
        Log.d("ActivityStarter", "requestFileSavingUri: emit result = $result")
    }

    fun requestFileLoadingUri(request: FileUriRequest.Load) {
        val result = fileUriRequests.tryEmit(request)
        Log.d("ActivityStarter", "requestFileLoadingUri: emit result = $result")
    }
}

sealed class FileUriRequest(
) {
    data class Save(
        val fileName: String,
        val mimeType: String,
        val onResult: (Uri?) -> Unit
    ) : FileUriRequest()

    class Load(
        val mimeType: String,
        val onResult: (Uri?) -> Unit
    ) : FileUriRequest()
}
