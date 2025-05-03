package app.aiaw.capacitor.export

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResult
import com.getcapacitor.JSObject
import com.getcapacitor.Plugin
import com.getcapacitor.PluginCall
import com.getcapacitor.PluginMethod
import com.getcapacitor.annotation.ActivityCallback
import com.getcapacitor.annotation.CapacitorPlugin
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

@CapacitorPlugin(name = "ExportFile")
class ExportFilePlugin : Plugin() {

    companion object {
        private const val CREATE_FILE_REQUEST_CODE = "createFileRequest"
    }

    @PluginMethod
    fun exportFile(call: PluginCall) {
        val uriString = call.getString("uri")
        val filename = call.getString("filename")

        if (uriString == null) {
            call.reject("Missing 'uri' option")
            return
        }

        val sourceUri: Uri
        try {
            sourceUri = Uri.parse(uriString)
        } catch (e: Exception) {
            call.reject("Invalid 'uri' option: ${e.localizedMessage}")
            return
        }

        val mimeType = context.contentResolver.getType(sourceUri) ?: "*/*"
        val suggestedName = filename ?: sourceUri.lastPathSegment ?: "downloaded_file"

        try {
            val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = mimeType
                putExtra(Intent.EXTRA_TITLE, suggestedName)
            }
            // Save the call to use it in the callback
            saveCall(call)
            startActivityForResult(call, intent, "handleCreateFileResult")
        } catch (e: Exception) {
            call.reject("Error creating file intent: ${e.localizedMessage}")
        }
    }

    @ActivityCallback
    private fun handleCreateFileResult(call: PluginCall, result: ActivityResult) {
        if (result.resultCode == Activity.RESULT_OK) {
            val destinationUri = result.data?.data
            if (destinationUri != null) {
                try {
                    val sourceUri = Uri.parse(call.getString("uri"))
                    copyFile(sourceUri, destinationUri)
                    val ret = JSObject()
                    ret.put("uri", destinationUri.toString())
                    call.resolve(ret)
                } catch (e: Exception) {
                    call.reject("Error copying file: ${e.localizedMessage}")
                }
            } else {
                call.reject("Failed to get destination URI")
            }
        } else {
            call.reject("User canceled file export")
        }
    }

    @Throws(IOException::class, FileNotFoundException::class)
    private fun copyFile(sourceUri: Uri, destinationUri: Uri) {
        var inputStream: InputStream? = null
        var outputStream: OutputStream? = null
        try {
            inputStream = context.contentResolver.openInputStream(sourceUri)
            outputStream = context.contentResolver.openOutputStream(destinationUri)

            if (inputStream == null) {
                throw FileNotFoundException("Source file not found or could not be opened: $sourceUri")
            }
            if (outputStream == null) {
                throw IOException("Destination file could not be opened: $destinationUri")
            }

            val buffer = ByteArray(1024)
            var length: Int
            while (inputStream.read(buffer).also { length = it } > 0) {
                outputStream.write(buffer, 0, length)
            }
            outputStream.flush()
        } finally {
            inputStream?.close()
            outputStream?.close()
        }
    }
}
