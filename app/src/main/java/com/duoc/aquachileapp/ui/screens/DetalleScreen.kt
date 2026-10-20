package com.duoc.aquachileapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.duoc.aquachileapp.ui.components.EtiquetaResultado
import com.duoc.aquachileapp.ui.components.EtiquetaSalud
import com.duoc.aquachileapp.ui.components.Fotos
import com.duoc.aquachileapp.viewmodel.HistorialViewModel

@Composable
fun DetalleScreen(
    vm: HistorialViewModel,
    id: Int,
    onVolver: () -> Unit
) {
    val context = LocalContext.current
    val preChequeo = vm.buscar(id)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        if (preChequeo == null) {

            Text("No se encontró el pre-chequeo")

        } else {

            EtiquetaResultado(preChequeo.resultado)

            Text(
                text = "${preChequeo.centro} · ${preChequeo.fecha}",
                style = MaterialTheme.typography.titleMedium
            )

            Text("Buzo: ${preChequeo.buzo}")
            Text("Supervisor: ${preChequeo.supervisor}")

            Text(
                text = "Checklist",
                style = MaterialTheme.typography.titleMedium
            )

            preChequeo.items.forEach { item ->
                Text("• ${item.nombre}: ${item.estado.texto}")
            }

            if (preChequeo.observaciones.isNotEmpty()) {
                Text(
                    text = "Observaciones",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(preChequeo.observaciones)
            }

            if (preChequeo.fotos.isNotEmpty()) {
                val (imagenes, archivos) = preChequeo.fotos.partition { esImagen(context, it) }

                if (imagenes.isNotEmpty()) {
                    Text(
                        text = "Fotos (toca una imagen para ampliar)",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Fotos(imagenes)
                }

                if (archivos.isNotEmpty()) {
                    Text(
                        text = "Archivos adjuntos",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    archivos.forEach { uriString ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.InsertDriveFile,
                                contentDescription = "Archivo",
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = nombreArchivo(context, uriString),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }

            Text(
                text = "Salud (datos ficticios)",
                style = MaterialTheme.typography.titleMedium
            )

            Text("Presión ${preChequeo.sistolica}/${preChequeo.diastolica} · Saturación ${preChequeo.saturacion}%")

            preChequeo.alertas.forEach { alerta ->
                Text("⚠ $alerta")
            }

            EtiquetaSalud(preChequeo.salud)
        }

        OutlinedButton(
            onClick = onVolver,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver")
        }
    }
}
