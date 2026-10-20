package com.duoc.aquachileapp.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.duoc.aquachileapp.model.Estado
import com.duoc.aquachileapp.model.ItemChecklist
import com.duoc.aquachileapp.viewmodel.AquaCheckViewmodel

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.duoc.aquachileapp.model.Estado
import com.duoc.aquachileapp.model.ItemChecklist
import com.duoc.aquachileapp.viewmodel.AquaCheckViewmodel
import java.io.File

private fun crearUriFoto(context: Context): Uri {
    val carpeta = File(context.cacheDir, "fotos").apply { mkdirs() }
    val archivo = File.createTempFile("pre_", ".jpg", carpeta)
    return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", archivo)
}

private fun nombreDe(context: Context, uri: String): String {
    val u = Uri.parse(uri)
    context.contentResolver.query(u, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { c ->
        if (c.moveToFirst()) {
            val i = c.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (i >= 0) return c.getString(i)
        }
    }
    return u.lastPathSegment ?: "Archivo"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChecklistScreen(
    onGuardar: (List<ItemChecklist>, String, List<String>) -> Unit,
    viewModel: AquaCheckViewmodel = viewModel()
) {
    val context = LocalContext.current
    var uriFoto by rememberSaveable { mutableStateOf<Uri?>(null) }

    val camara = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { tomada ->
        if (tomada) uriFoto?.let { viewModel.agregarAdjunto(it.toString()) }
    }

    val archivos = rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
        uris.forEach { viewModel.agregarAdjunto(it.toString()) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Inspección de Equipos en Terreno",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(viewModel.checklist) { index, item ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = if (item.critico) "${item.nombre} (crítico)" else item.nombre,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Row(
                            modifier = Modifier.horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Estado.entries.forEach { estado ->
                                FilterChip(
                                    selected = item.estado == estado,
                                    onClick = { viewModel.cambiarEstado(index, estado) },
                                    label = { Text(estado.texto) }
                                )
                            }
                        }
                    }
                }
            }

            item {
                OutlinedTextField(
                    value = viewModel.observaciones,
                    onValueChange = { viewModel.observaciones = it },
                    label = {
                        Text(
                            if (viewModel.requiereObservacion) "Observaciones (obligatorias)"
                            else "Observaciones"
                        )
                    },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Evidencia",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = {
                                val uri = crearUriFoto(context)
                                uriFoto = uri
                                camara.launch(uri)
                            }
                        ) {
                            Text("Tomar foto")
                        }
                        OutlinedButton(onClick = { archivos.launch("*/*") }) {
                            Text("Adjuntar archivo")
                        }
                    }
                    if (viewModel.adjuntos.isEmpty()) {
                        Text(
                            text = "Sin archivos adjuntos",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            itemsIndexed(viewModel.adjuntos) { indice, uri ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = nombreDe(context, uri),
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(onClick = { viewModel.quitarAdjunto(indice) }) {
                        Text("Quitar")
                    }
                }
            }
        }

        if (!viewModel.completo) {
            Text(
                text = "Responde todos los ítems para guardar",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Button(
            onClick = {
                onGuardar(
                    viewModel.checklist.toList(),
                    viewModel.observaciones.trim(),
                    viewModel.adjuntos.toList()
                )
            },
            enabled = viewModel.puedeGuardar,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            Text("Guardar pre-chequeo")
        }
    }
}