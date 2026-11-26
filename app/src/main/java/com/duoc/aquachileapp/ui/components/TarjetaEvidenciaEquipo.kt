package com.duoc.aquachileapp.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Compare
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.duoc.aquachileapp.model.EvidenciaEquipo
import com.duoc.aquachileapp.ui.theme.Morado

@Composable
fun TarjetaEvidenciaEquipo(
    evidencia: EvidenciaEquipo,
    onTomarFoto: () -> Unit,
    onGaleria: () -> Unit,
    onEliminar: () -> Unit,
    editable: Boolean = true
) {
    var expandido by remember { mutableStateOf(false) }
    var mostrarComparacion by remember { mutableStateOf(false) }
    var fotoParaZoomRes by remember { mutableStateOf<Int?>(null) }
    var fotoParaZoomUrl by remember { mutableStateOf<String?>(null) }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header desplegable
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expandido = !expandido },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    if (evidencia.uriFotoActual != null) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Completado",
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Pending,
                            contentDescription = "Pendiente",
                            tint = Color(0xFFD97706),
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Text(
                        text = evidencia.nombreEquipo,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F2937)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        color = if (evidencia.uriFotoActual != null) Color(0xFFD1FAE5) else Color(0xFFFEF3C7),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (evidencia.uriFotoActual != null) "Completado ✓" else "Pendiente Zarpe",
                            color = if (evidencia.uriFotoActual != null) Color(0xFF047857) else Color(0xFFB45309),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    IconButton(onClick = { expandido = !expandido }) {
                        Icon(
                            imageVector = if (expandido) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = if (expandido) "Colapsar" else "Expandir",
                            tint = Color(0xFF4B5563)
                        )
                    }
                }
            }

            // Contenido Desplegable
            if (expandido) {
                // Muestras: Foto Actual vs Línea Base
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Foto Capturada
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text("Foto Capturada", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)

                        Box(
                            modifier = Modifier
                                .size(110.dp)
                                .background(Color(0xFFF3F4F6), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (evidencia.uriFotoActual != null) {
                                AsyncImage(
                                    model = com.duoc.aquachileapp.data.Remote.RetrofitClient.foto(evidencia.uriFotoActual),
                                    contentDescription = "Foto capturada",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable { fotoParaZoomUrl = evidencia.uriFotoActual }
                                )

                                if (editable) {
                                    IconButton(
                                        onClick = onEliminar,
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .size(26.dp)
                                            .padding(2.dp)
                                            .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Quitar",
                                            tint = Color.White,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            } else {
                                Text("Sin Foto", style = MaterialTheme.typography.bodySmall, color = Color(0xFF9CA3AF))
                            }
                        }
                    }

                    // Foto Línea Base
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text("Línea Base (Ref.)", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)

                        Box(
                            modifier = Modifier
                                .size(110.dp)
                                .background(Color(0xFFEFF6FF), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = evidencia.resFotoReferencia),
                                contentDescription = "Línea Base ${evidencia.nombreEquipo}",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { fotoParaZoomRes = evidencia.resFotoReferencia }
                            )
                        }
                    }
                }

                // Metadata asociada
                if (evidencia.uriFotoActual != null) {
                    Surface(
                        color = Color(0xFFF9FAFB),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text("• ID Faena / Centro: ${evidencia.idFaena ?: "Faena Zarpe"}", fontSize = 11.sp, color = Color(0xFF4B5563))
                            Text("• Equipo: ${evidencia.nombreEquipo}", fontSize = 11.sp, color = Color(0xFF4B5563))
                            Text("• Fecha/Hora: ${evidencia.fechaHora ?: "Reciente"}", fontSize = 11.sp, color = Color(0xFF4B5563))
                            Text("• Responsable: ${evidencia.responsable ?: "Buzo a cargo"}", fontSize = 11.sp, color = Color(0xFF4B5563))
                        }
                    }
                }

                // Acciones: Cámara / Galería / Comparar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (editable) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            TextButton(
                                onClick = onTomarFoto,
                                colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF0F4C81))
                            ) {
                                Icon(Icons.Default.PhotoCamera, contentDescription = "Cámara", modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Cámara", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            TextButton(
                                onClick = onGaleria,
                                colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF0F4C81))
                            ) {
                                Icon(Icons.Default.PhotoLibrary, contentDescription = "Galería", modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Galería", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    OutlinedButton(
                        onClick = { mostrarComparacion = true },
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Compare, contentDescription = "Comparar", modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Comparar Línea Base", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    if (mostrarComparacion) {
        DialogoComparacionLineaBase(
            evidencia = evidencia,
            onDismiss = { mostrarComparacion = false }
        )
    }

    fotoParaZoomRes?.let { resId ->
        DialogoZoomImagen(
            fotoModel = resId,
            onDismiss = { fotoParaZoomRes = null }
        )
    }

    fotoParaZoomUrl?.let { urlStr ->
        DialogoZoomImagen(
            fotoModel = urlStr,
            onDismiss = { fotoParaZoomUrl = null }
        )
    }
}

@Composable
fun DialogoComparacionLineaBase(
    evidencia: EvidenciaEquipo,
    onDismiss: () -> Unit
) {
    var fotoParaZoomRes by remember { mutableStateOf<Int?>(null) }
    var fotoParaZoomUrl by remember { mutableStateOf<String?>(null) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.9f))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(20.dp))
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Comparación con Línea Base",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2937)
                )

                Text(
                    text = "Equipo: ${evidencia.nombreEquipo}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF4B5563)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Columna 1: Línea Base
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            color = Color(0xFFEEF2FF),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                "LÍNEA BASE / REFERENCIA",
                                color = Morado,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        Image(
                            painter = painterResource(id = evidencia.resFotoReferencia),
                            contentDescription = "Línea Base Referencia",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(140.dp)
                                .background(Color(0xFFF3F4F6), RoundedCornerShape(12.dp))
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { fotoParaZoomRes = evidencia.resFotoReferencia }
                        )
                    }

                    // Columna 2: Foto Actual
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            color = if (evidencia.uriFotoActual != null) Color(0xFFD1FAE5) else Color(0xFFFEE2E2),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                if (evidencia.uriFotoActual != null) "FOTO CAPTURADA ACTUAL" else "SIN CAPTURA",
                                color = if (evidencia.uriFotoActual != null) Color(0xFF047857) else Color(0xFFDC2626),
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        if (evidencia.uriFotoActual != null) {
                            AsyncImage(
                                model = com.duoc.aquachileapp.data.Remote.RetrofitClient.foto(evidencia.uriFotoActual),
                                contentDescription = "Foto capturada actual",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(140.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { fotoParaZoomUrl = evidencia.uriFotoActual }
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(140.dp)
                                    .background(Color(0xFFF3F4F6), RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Sin Foto", fontSize = 12.sp, color = Color(0xFF9CA3AF))
                            }
                        }
                    }
                }

                // Ficha Metadata
                Surface(
                    color = Color(0xFFF9FAFB),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text("• ID Faena: ${evidencia.idFaena ?: "Faena Zarpe"}", fontSize = 11.sp, fontWeight = FontWeight.Medium)
                        Text("• Equipo: ${evidencia.nombreEquipo}", fontSize = 11.sp, fontWeight = FontWeight.Medium)
                        Text("• Fecha/Hora: ${evidencia.fechaHora ?: "No capturado"}", fontSize = 11.sp, fontWeight = FontWeight.Medium)
                        Text("• Responsable: ${evidencia.responsable ?: "Buzo a cargo"}", fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    }
                }

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = Morado),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cerrar Comparación", fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    fotoParaZoomRes?.let { resId ->
        DialogoZoomImagen(
            fotoModel = resId,
            onDismiss = { fotoParaZoomRes = null }
        )
    }

    fotoParaZoomUrl?.let { urlStr ->
        DialogoZoomImagen(
            fotoModel = urlStr,
            onDismiss = { fotoParaZoomUrl = null }
        )
    }
}
