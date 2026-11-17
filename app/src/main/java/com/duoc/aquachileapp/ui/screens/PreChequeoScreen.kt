package com.duoc.aquachileapp.ui.screens

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.duoc.aquachileapp.model.Estado
import com.duoc.aquachileapp.model.PREGUNTAS_SALUD
import com.duoc.aquachileapp.model.PreChequeo
import com.duoc.aquachileapp.model.Usuario
import com.duoc.aquachileapp.ui.components.Campo
import com.duoc.aquachileapp.ui.components.CampoDesplegableCentro
import com.duoc.aquachileapp.ui.components.EtiquetaSalud
import com.duoc.aquachileapp.ui.components.FilaChecklist
import com.duoc.aquachileapp.ui.components.FondoBarra
import com.duoc.aquachileapp.ui.components.Fotos
import com.duoc.aquachileapp.ui.components.TarjetaEvidenciaEquipo
import com.duoc.aquachileapp.ui.theme.Morado
import com.duoc.aquachileapp.viewmodel.NuevoViewModel
import java.io.File

private fun crearUriFoto(context: Context): Uri {
    val carpeta = File(context.cacheDir, "fotos").apply { mkdirs() }
    val archivo = File.createTempFile("pre_", ".jpg", carpeta)
    return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", archivo)
}

fun esImagen(context: Context, uriString: String): Boolean {
    val uri = Uri.parse(uriString)
    val mimeType = context.contentResolver.getType(uri)
    if (mimeType != null) {
        return mimeType.startsWith("image/")
    }
    val extension = uriString.substringAfterLast('.', "").lowercase()
    return extension in listOf("jpg", "jpeg", "png", "webp", "gif", "bmp") ||
            uriString.contains("fotos") || uriString.contains("pre_")
}

fun nombreArchivo(context: Context, uriString: String): String {
    val uri = Uri.parse(uriString)
    context.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { c ->
        if (c.moveToFirst()) {
            val i = c.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (i >= 0) return c.getString(i)
        }
    }
    return uri.lastPathSegment ?: "Archivo adjunto"
}

@Composable
fun PreChequeoScreen(
    vm: NuevoViewModel,
    usuario: Usuario?,
    onGuardar: (PreChequeo) -> Unit
) {
    val context = LocalContext.current
    var uriFotoTomada by rememberSaveable { mutableStateOf<Uri?>(null) }
    var itemParaFotoIndex by rememberSaveable { mutableStateOf<Int?>(null) }
    var equipoTargetId by rememberSaveable { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        vm.prepararDatos(usuario)
    }

    val camaraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { exito ->
        if (exito) {
            uriFotoTomada?.let { uri ->
                val str = uri.toString()
                if (equipoTargetId != null) {
                    vm.adjuntarFotoEquipo(equipoTargetId!!, str)
                } else if (itemParaFotoIndex != null) {
                    vm.agregarFotoItem(itemParaFotoIndex!!, str)
                } else {
                    if (str !in vm.fotos) vm.fotos.add(str)
                }
            }
        }
    }

    val selectorFotos = rememberLauncherForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia()
    ) { uris ->
        uris.forEach { uri ->
            val str = uri.toString()
            if (equipoTargetId != null) {
                vm.adjuntarFotoEquipo(equipoTargetId!!, str)
            } else if (itemParaFotoIndex != null) {
                vm.agregarFotoItem(itemParaFotoIndex!!, str)
            } else {
                if (str !in vm.fotos) vm.fotos.add(str)
            }
        }
    }

    val selectorArchivos = rememberLauncherForActivityResult(
        ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        uris.forEach { uri ->
            val str = uri.toString()
            if (str !in vm.fotos) {
                vm.fotos.add(str)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoBarra)
            .verticalScroll(rememberScrollState())
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 22.dp, end = 22.dp, top = 8.dp, bottom = 36.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Nuevo pre-chequeo",
                color = Color.White,
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = "Datos de la faena",
                color = Color.White.copy(alpha = 0.8f),
                style = MaterialTheme.typography.titleMedium
            )

            CampoDesplegableCentro(
                valor = vm.centro,
                alSeleccionar = { vm.centro = it },
                sobreMorado = true
            )
            Campo(vm.buzo, { vm.buzo = it }, "Buzo", sobreMorado = true)
            Campo(vm.supervisor, { vm.supervisor = it }, "Supervisor", sobreMorado = true)
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 600.dp)
                .background(
                    Color.White,
                    RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
                )
                .padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // ---- Acreditaciones de Perfil del Buzo ----
            val valRes = vm.validacion

            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Acreditaciones y Perfil de Buceo (${vm.buzo.ifBlank { "Buzo" }})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F2937)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("• Edad / IMC:", style = MaterialTheme.typography.bodySmall, color = Color(0xFF4B5563))
                        Text("${vm.edad} años | IMC ${valRes.imc} (${valRes.imcCategoria})", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = Color(0xFF111827))
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("• Examen Preocupacional:", style = MaterialTheme.typography.bodySmall, color = Color(0xFF4B5563))
                        Text(if (vm.examenPreocupacionalVigente) "Vigente" else "Vencido / No Presentado", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = if (vm.examenPreocupacionalVigente) Color(0xFF047857) else Color(0xFFDC2626))
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("• Test de Esfuerzo (>45 años):", style = MaterialTheme.typography.bodySmall, color = Color(0xFF4B5563))
                        Text(if (vm.testEsfuerzoVigente) "Vigente" else "Pendiente / Vencido", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = if (vm.testEsfuerzoVigente) Color(0xFF047857) else Color(0xFFDC2626))
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("• Escuela de Buceo Calbuco:", style = MaterialTheme.typography.bodySmall, color = Color(0xFF4B5563))
                        Text("${vm.puntajeEscuelaCalbuco} / 100 pts", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = Color(0xFF111827))
                    }
                }
            }

            // ---- Tarjeta Semáforo de Validación en vivo ----
            Card(
                colors = CardDefaults.cardColors(containerColor = valRes.semaforo.color.copy(alpha = 0.12f)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            color = valRes.semaforo.color,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(
                                imageVector = if (valRes.estaBloqueado) Icons.Default.Lock else Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.padding(8.dp)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Semáforo: ${valRes.semaforo.titulo}",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = valRes.semaforo.color
                            )
                            Text(
                                text = "Validación cruzada de Perfil de Buzo, Salud y Equipos",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF374151)
                            )
                        }
                    }

                    if (valRes.estaBloqueado) {
                        Surface(
                            color = Color(0xFFFEE2E2),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "⛔ GUARDA Y CIERRE BLOQUEADOS POR SEGURIDAD",
                                    color = Color(0xFFDC2626),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                                valRes.motivosBloqueo.forEach { motivo ->
                                    Text(
                                        text = "• $motivo",
                                        color = Color(0xFF991B1B),
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }

                    if (valRes.advertencias.isNotEmpty()) {
                        Surface(
                            color = Color(0xFFFEF3C7),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                valRes.advertencias.forEach { adv ->
                                    Text(
                                        text = "⚠️ $adv",
                                        color = Color(0xFFB45309),
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // ---- Evidencia Fotográfica Obligatoria y Línea Base (Antes del Zarpe) ----
            Text(
                text = "Evidencia Fotográfica Obligatoria (Antes del Zarpe)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Captura de los 4 equipos de seguridad requeridos con metadatos (ID Faena, Equipo, Fecha/Hora, Responsable) y comparación con Línea Base.",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF6B7280)
            )

            vm.evidenciasEquipos.forEach { evidencia ->
                TarjetaEvidenciaEquipo(
                    evidencia = evidencia,
                    onTomarFoto = {
                        equipoTargetId = evidencia.idEquipo
                        itemParaFotoIndex = null
                        val uri = crearUriFoto(context)
                        uriFotoTomada = uri
                        camaraLauncher.launch(uri)
                    },
                    onGaleria = {
                        equipoTargetId = evidencia.idEquipo
                        itemParaFotoIndex = null
                        selectorFotos.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    onEliminar = {
                        vm.eliminarFotoEquipo(evidencia.idEquipo)
                    }
                )
            }

            // ---- Checklist ----
            Text(
                text = "Checklist de seguridad",
                style = MaterialTheme.typography.titleMedium
            )

            vm.items.forEachIndexed { indice, item ->
                FilaChecklist(
                    item = item,
                    alElegir = { estado -> vm.cambiarEstado(indice, estado) },
                    alCambiarObservacion = { obs -> vm.cambiarObservacion(indice, obs) },
                    onTomarFoto = {
                        equipoTargetId = null
                        itemParaFotoIndex = indice
                        val uri = crearUriFoto(context)
                        uriFotoTomada = uri
                        camaraLauncher.launch(uri)
                    },
                    onGaleria = {
                        equipoTargetId = null
                        itemParaFotoIndex = indice
                        selectorFotos.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    onEliminarFoto = { indiceFoto ->
                        vm.eliminarFotoItem(indice, indiceFoto)
                    }
                )
            }

            val evaluados = vm.items.count { it.estado != Estado.PENDIENTE }
            val totalItems = vm.items.size
            if (totalItems > 0) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "$evaluados de $totalItems ítems evaluados",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF374151)
                    )
                    LinearProgressIndicator(
                        progress = { evaluados.toFloat() / totalItems.toFloat() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = Color(0xFF0F4C81),
                        trackColor = Color(0xFFE5E7EB)
                    )
                }
            }

            Campo(vm.observaciones, { vm.observaciones = it }, "Observaciones generales")

            Text(
                text = "Evidencias generales y Archivos",
                style = MaterialTheme.typography.titleMedium
            )

            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        equipoTargetId = null
                        itemParaFotoIndex = null
                        val uri = crearUriFoto(context)
                        uriFotoTomada = uri
                        camaraLauncher.launch(uri)
                    }
                ) {
                    Text("Tomar foto")
                }

                OutlinedButton(
                    onClick = {
                        equipoTargetId = null
                        itemParaFotoIndex = null
                        selectorFotos.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
                ) {
                    Text("Galería")
                }

                OutlinedButton(
                    onClick = {
                        equipoTargetId = null
                        itemParaFotoIndex = null
                        selectorArchivos.launch("*/*")
                    }
                ) {
                    Text("Adjuntar archivo")
                }
            }

            val (imagenes, archivos) = vm.fotos.partition { esImagen(context, it) }

            if (imagenes.isNotEmpty()) {
                Text(
                    text = "Fotos generales (${imagenes.size})",
                    style = MaterialTheme.typography.bodyMedium
                )
                Fotos(
                    fotos = imagenes,
                    onEliminar = { indice ->
                        val fotoAEliminar = imagenes[indice]
                        vm.fotos.remove(fotoAEliminar)
                    }
                )
            }

            if (archivos.isNotEmpty()) {
                Text(
                    text = "Archivos adjuntos (${archivos.size})",
                    style = MaterialTheme.typography.bodyMedium
                )
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    archivos.forEach { uriString ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.weight(1f)
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
                            IconButton(onClick = { vm.fotos.remove(uriString) }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Quitar archivo"
                                )
                            }
                        }
                    }
                }
            }

            Text(
                text = "Salud del buzo (datos ficticios)",
                style = MaterialTheme.typography.titleMedium
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Campo(vm.sistolica, { vm.sistolica = it }, "Sistólica", Modifier.weight(1f), numero = true)
                Campo(vm.diastolica, { vm.diastolica = it }, "Diastólica", Modifier.weight(1f), numero = true)
            }

            Campo(vm.saturacion, { vm.saturacion = it }, "Saturación O₂ (%)", numero = true)

            PREGUNTAS_SALUD.forEach { pregunta ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = pregunta in vm.alertas,
                        onCheckedChange = { vm.marcarAlerta(pregunta) }
                    )
                    Text(pregunta)
                }
            }

            vm.salud?.let { nivel ->
                EtiquetaSalud(nivel)
            }

            vm.error?.let { mensaje ->
                Text(
                    text = mensaje,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Button(
                onClick = {
                    val preChequeo = vm.crear()
                    if (preChequeo != null) {
                        onGuardar(preChequeo)
                    }
                },
                enabled = !valRes.estaBloqueado,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (valRes.estaBloqueado) Color(0xFFDC2626) else Morado,
                    disabledContainerColor = Color(0xFFFCA5A5),
                    disabledContentColor = Color.White
                ),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (valRes.estaBloqueado) {
                        Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Guardado Bloqueado por Seguridad", fontWeight = FontWeight.Bold)
                    } else {
                        Text("Guardar pre-chequeo", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
