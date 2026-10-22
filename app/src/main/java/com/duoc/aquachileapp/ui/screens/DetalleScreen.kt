package com.duoc.aquachileapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.duoc.aquachileapp.model.Estado
import com.duoc.aquachileapp.ui.components.EtiquetaResultado
import com.duoc.aquachileapp.ui.components.EtiquetaSalud
import com.duoc.aquachileapp.ui.components.FondoBarra
import com.duoc.aquachileapp.ui.components.Fotos
import com.duoc.aquachileapp.ui.theme.FondoApp
import com.duoc.aquachileapp.ui.theme.Morado
import com.duoc.aquachileapp.ui.theme.TextoOscuro
import com.duoc.aquachileapp.ui.theme.TextoSuave
import com.duoc.aquachileapp.viewmodel.HistorialViewModel

private fun iconoEstado(estado: Estado): ImageVector = when (estado) {
    Estado.CUMPLE -> Icons.Filled.CheckCircle
    Estado.NO_CUMPLE -> Icons.Filled.Cancel
    Estado.OBSERVADO -> Icons.Filled.Warning
    else -> Icons.Filled.Remove
}

private fun colorEstado(estado: Estado): Color = when (estado) {
    Estado.CUMPLE -> Color(0xFF2E9E5B)
    Estado.NO_CUMPLE -> Color(0xFFE03E3E)
    Estado.OBSERVADO -> Color(0xFFE5A100)
    else -> TextoSuave
}

@Composable
private fun SeccionBlanca(titulo: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = titulo,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                color = TextoOscuro
            )
            content()
        }
    }
}

@Composable
private fun DatoSalud(etiqueta: String, valor: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(FondoApp, RoundedCornerShape(14.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Text(etiqueta, fontSize = 11.sp, color = TextoSuave)
        Text(valor, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Morado)
    }
}

@Composable
fun DetalleScreen(
    vm: HistorialViewModel,
    id: Int,
    esSupervisor: Boolean = false,
    onVolver: () -> Unit
) {
    val context = LocalContext.current
    val preChequeo = vm.buscar(id)
    var mostrarDialogoEliminar by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {

        if (preChequeo == null) {

            Text("No se encontró el pre-chequeo")

        } else {

            // ---- Cabecera con degradado ----
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(FondoBarra)
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = preChequeo.centro,
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(preChequeo.fecha, color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                    Text("Buzo: ${preChequeo.buzo}", color = Color.White, fontSize = 13.sp)
                    Text(
                        "Supervisor: ${preChequeo.supervisor}",
                        color = Color.White,
                        fontSize = 13.sp
                    )
                    Row(
                        modifier = Modifier.padding(top = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        EtiquetaResultado(preChequeo.resultado)
                        EtiquetaSalud(preChequeo.salud)
                    }
                }
            }

            // ---- Checklist ----
            SeccionBlanca("Checklist") {
                preChequeo.items.forEachIndexed { indice, item ->
                    if (indice > 0) HorizontalDivider(color = Color(0xFFECEEF8))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = iconoEstado(item.estado),
                            contentDescription = null,
                            tint = colorEstado(item.estado),
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = item.nombre,
                            fontSize = 13.sp,
                            color = TextoOscuro,
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 10.dp)
                        )
                        Text(
                            text = item.estado.texto,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = colorEstado(item.estado)
                        )
                    }
                }
            }

            // ---- Observaciones ----
            if (preChequeo.observaciones.isNotEmpty()) {
                SeccionBlanca("Observaciones") {
                    Text(preChequeo.observaciones, fontSize = 13.sp, color = TextoOscuro)
                }
            }

            // ---- Fotos y archivos ----
            if (preChequeo.fotos.isNotEmpty()) {
                val (imagenes, archivos) = preChequeo.fotos.partition { esImagen(context, it) }

                if (imagenes.isNotEmpty()) {
                    SeccionBlanca("Fotos (toca una imagen para ampliar)") {
                        Fotos(imagenes)
                    }
                }

                if (archivos.isNotEmpty()) {
                    SeccionBlanca("Archivos adjuntos") {
                        archivos.forEach { uriString ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.InsertDriveFile,
                                    contentDescription = "Archivo",
                                    tint = Morado
                                )
                                Text(
                                    text = nombreArchivo(context, uriString),
                                    fontSize = 13.sp,
                                    color = TextoOscuro
                                )
                            }
                        }
                    }
                }
            }

            // ---- Salud ----
            SeccionBlanca("Salud (datos ficticios)") {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    DatoSalud(
                        "Presión",
                        "${preChequeo.sistolica}/${preChequeo.diastolica}",
                        Modifier.weight(1f)
                    )
                    DatoSalud(
                        "Saturación",
                        "${preChequeo.saturacion}%",
                        Modifier.weight(1f)
                    )
                }
                preChequeo.alertas.forEach { alerta ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Warning,
                            contentDescription = null,
                            tint = Color(0xFFE5A100),
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = alerta,
                            fontSize = 13.sp,
                            color = TextoOscuro,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        }

        if (esSupervisor && preChequeo != null) {
            OutlinedButton(
                onClick = { mostrarDialogoEliminar = true },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626)),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text("Eliminar este Pre-chequeo", fontWeight = FontWeight.Bold)
            }
        }

        Button(
            onClick = onVolver,
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Volver")
        }
    }

    if (mostrarDialogoEliminar) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoEliminar = false },
            title = {
                Text("¿Eliminar pre-chequeo?", fontWeight = FontWeight.Bold)
            },
            text = {
                Text("Se eliminará permanentemente este pre-chequeo. Esta acción no se puede deshacer.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        vm.eliminar(id)
                        mostrarDialogoEliminar = false
                        onVolver()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                ) {
                    Text("Eliminar", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoEliminar = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
