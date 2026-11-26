package com.duoc.aquachileapp.ui.screens

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File
import java.io.IOException

internal fun copiarAdjuntoLocal(context: Context, uri: Uri): String? {
    var archivo: File? = null
    return try {
        val mime = context.contentResolver.getType(uri)
        val extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mime ?: "application/octet-stream") ?: "bin"
        val carpeta = File(context.filesDir, "fotos").apply { mkdirs() }
        val destino = File.createTempFile("adjunto_", ".$extension", carpeta)
        archivo = destino
        val entrada = context.contentResolver.openInputStream(uri) ?: throw IOException("Archivo no disponible")
        entrada.use { input -> destino.outputStream().use { output -> input.copyTo(output) } }
        if (destino.length() == 0L) throw IOException("Archivo vacío")
        FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", destino).toString()
    } catch (e: Exception) {
        archivo?.delete()
        Toast.makeText(context, "No se pudo guardar el adjunto. Selecciónalo nuevamente.", Toast.LENGTH_LONG).show()
        null
    }
}
