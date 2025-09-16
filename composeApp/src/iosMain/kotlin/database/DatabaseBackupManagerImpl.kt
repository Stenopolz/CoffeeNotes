package database

import domain.CoffeeRepository
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCObjectVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSError
import platform.Foundation.NSFileManager
import platform.Foundation.NSOperationQueue
import platform.Foundation.NSTimeZone
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask
import platform.Foundation.defaultTimeZone
import platform.Foundation.temporaryDirectory
import platform.UIKit.UIApplication
import platform.UIKit.UIDocumentPickerDelegateProtocol
import platform.UIKit.UIDocumentPickerViewController
import platform.darwin.NSObject
import kotlin.coroutines.resume

class DatabaseBackupManagerImpl(
    private val coffeeRepository: CoffeeRepository,
) : DatabaseBackupManager, KoinComponent {

    @OptIn(ExperimentalForeignApi::class)
    override suspend fun exportDatabase(): Boolean = withContext(Dispatchers.IO) {
        try {
            // Ensure all transactions are committed
            coffeeRepository.checkpoint()

            // Get the database file path
            val dbFilePath = documentDirectory() + "/" + CoffeeDatabase.DATABASE_NAME
            val dbFile = NSURL.fileURLWithPath(dbFilePath)

            // Check if database file exists
            if (!NSFileManager.defaultManager.fileExistsAtPath(dbFilePath)) {
                return@withContext false
            }

            // Create a timestamped filename
            val dateFormatter = NSDateFormatter().apply {
                dateFormat = "yyyy-MM-dd_HH-mm-ss"
                timeZone = NSTimeZone.defaultTimeZone
            }
            val timestamp = dateFormatter.stringFromDate(NSDate())
            val exportFileName = "coffee_notes_backup_$timestamp.db"

            // Show save dialog and save the database file
            showSaveDialog(dbFile, exportFileName)

        } catch (e: Exception) {
            println("Export failed: ${e.message}")
            false
        }
    }

    @OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
    private suspend fun showSaveDialog(sourceFile: NSURL, fileName: String): Boolean =
        suspendCancellableCoroutine { continuation ->
            val mainQueue = NSOperationQueue.mainQueue
            mainQueue.addOperationWithBlock {
                try {
                    // Create a temporary copy with the desired filename
                    val tempDir = NSFileManager.defaultManager.temporaryDirectory
                    val tempFileURL = tempDir.URLByAppendingPathComponent(fileName)

                    if (tempFileURL == null) {
                        println("Failed to create temp file URL")
                        continuation.resume(false)
                        return@addOperationWithBlock
                    }

                    // Copy source file to temp location with new name
                    val fileManager = NSFileManager.defaultManager

                    // Remove existing temp file if it exists
                    val tempPath = tempFileURL.path
                    if (tempPath != null && fileManager.fileExistsAtPath(tempPath)) {
                        fileManager.removeItemAtURL(tempFileURL, error = null)
                    }

                    memScoped {
                        val error = alloc<ObjCObjectVar<NSError?>>()
                        val copied = fileManager.copyItemAtURL(
                            srcURL = sourceFile,
                            toURL = tempFileURL,
                            error = error.ptr
                        )

                        if (!copied || error.value != null) {
                            println("Failed to create temp copy: ${error.value?.localizedDescription}")
                            continuation.resume(false)
                            return@addOperationWithBlock
                        }
                    }

                    // Create document picker for export
                    val documentPicker = UIDocumentPickerViewController(
                        forExportingURLs = listOf(tempFileURL)
                    )

                    // Create delegate
                    val delegate = DocumentPickerDelegate { success ->
                        // Clean up temp file
                        fileManager.removeItemAtURL(tempFileURL, error = null)
                        continuation.resume(success)
                    }

                    documentPicker.setDelegate(delegate)

                    // Present the document picker
                    val rootViewController =
                        UIApplication.sharedApplication.keyWindow?.rootViewController
                    rootViewController?.presentViewController(
                        documentPicker,
                        animated = true,
                        completion = null
                    )

                } catch (e: Exception) {
                    println("Error showing save dialog: ${e.message}")
                    continuation.resume(false)
                }
            }
        }

    @OptIn(ExperimentalForeignApi::class)
    private fun documentDirectory(): String {
        val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = null,
        )
        return requireNotNull(documentDirectory?.path)
    }

    override suspend fun importDatabase(): Boolean {
        // iOS implementation not needed for this feature
        return false
    }
}

@OptIn(ExperimentalForeignApi::class)
private class DocumentPickerDelegate(
    private val completion: (Boolean) -> Unit
) : NSObject(), UIDocumentPickerDelegateProtocol {

    override fun documentPicker(
        controller: UIDocumentPickerViewController,
        didPickDocumentsAtURLs: List<*>
    ) {
        completion(true)
    }

    override fun documentPickerWasCancelled(controller: UIDocumentPickerViewController) {
        completion(false)
    }
}
