package com.duoc.aquachileapp.ui.screens

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import android.content.ActivityNotFoundException
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Brush
import com.duoc.aquachileapp.ui.components.recordarSinInternet
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
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import com.duoc.aquachileapp.data.local.PreferenciasCentro
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
    val carpeta = File(context.filesDir, "fotos").apply { mkdirs() }
    val archivo = File.createTempFile("pre_", ".jpg", carpeta)
    return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", archivo)
}

fun esImagen(context: Context, uriString: String): Boolean {
    val mimeType = runCatching { context.contentResolver.getType(Uri.parse(uriString)) }.getOrNull()
    if (mimeType != null) return mimeType.startsWith("image/")
    return uriString.substringAfterLast('.', "").lowercase() in listOf("jpg", "jpeg", "png", "webp", "gif", "bmp") || uriString.contains("fotos")
}

fun nombreArchivo(context: Context, uriString: String): String {
    val uri = Uri.parse(uriString)
    return try {
        context.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { c ->
            val i = c.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (c.moveToFirst() && i >= 0) c.getString(i) else null
        } ?: uri.lastPathSegment ?: "Archivo adjunto"
    } catch (e: SecurityException) {
        "Archivo sin permiso de acceso"
    } catch (e: Exception) {
        "Archivo no disponible"
    }
}

@Composable
fun PreChequeoScreen(
    vm: NuevoViewModel,
    usuario: Usuario?,
    onGuardar: (PreChequeo) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val sinInternet = recordarSinInternet()
    val evaluados = vm.items.count { it.estado != Estado.PENDIENTE }
    val totalItems = vm.items.size
    var uriFotoTomada by rememberSaveable { mutableStateOf<Uri?>(null) }
    var itemParaFotoIndex by rememberSaveable { mutableStateOf<Int?>(null) }
    var equipoTargetId by rememberSaveable { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        // Solo cargar datos del usuario
        // cuando estamos creando uno nuevo.
        if (vm.idEnEdicion == null) {

            vm.prepararDatos(usuario)
            val centroGuardado = PreferenciasCentro.leer(context)
            if (vm.idEnEdicion == null && vm.centro == "Centro Puerto Montt" && centroGuardado != null) {
                vm.centro = centroGuardado
            }
        }
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
        ActivityResultContracts.OpenMultipleDocuments()
    ) { uris ->
        uris.forEach { uri ->
            val str = copiarAdjuntoLocal(context, uri) ?: return@forEach
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
            val str = copiarAdjuntoLocal(context, uri) ?: return@forEach
            if (str !in vm.fotos) {
                vm.fotos.add(str)
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        if (totalItems > 0) {
            Column(
                modifier = Modifier.fillMaxWidth().background(Color.White)
                    .padding(horizontal = 22.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text("$evaluados de $totalItems ítems evaluados", fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF334155), fontSize = 12.sp)
                LinearProgressIndicator(
                    progress = { evaluados.toFloat() / totalItems.toFloat() },
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                    color = Color(0xFF64748B), trackColor = Color(0xFFE5E7EB)
                )
            }
        }
        Column(
            modifier = Modifier.fillMaxSize()
                .background(if (sinInternet) Brush.verticalGradient(listOf(Color(0xFF64748B), Color.White)) else FondoBarra)
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
                alSeleccionar = { centro ->
                    vm.centro = centro
                    scope.launch { PreferenciasCentro.guardar(context, centro) }
                },
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
            // ---- Datos editables del Perfil del Buzo ----
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF9FAFB)
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {

                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    Text(
                        text = "Datos del perfil del buzo",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F2937)
                    )


                    // Edad y peso
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {

                        Campo(
                            valor = vm.edad,
                            alCambiar = { vm.edad = it },
                            etiqueta = "Edad",
                            modifier = Modifier.weight(1f),
                            numero = true
                        )

                        Campo(
                            valor = vm.pesoKg,
                            alCambiar = { vm.pesoKg = it },
                            etiqueta = "Peso (kg)",
                            modifier = Modifier.weight(1f),
                            numero = true
                        )
                    }


                    // Altura y puntaje
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {

                        Campo(
                            valor = vm.alturaCm,
                            alCambiar = { vm.alturaCm = it },
                            etiqueta = "Altura (cm)",
                            modifier = Modifier.weight(1f),
                            numero = true
                        )

                        Campo(
                            valor = vm.puntajeEscuelaCalbuco,
                            alCambiar = {
                                vm.puntajeEscuelaCalbuco = it
                            },
                            etiqueta = "Puntaje Calbuco",
                            modifier = Modifier.weight(1f),
                            numero = true
                        )
                    }


                    // Examen preocupacional
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Checkbox(
                            checked =
                                vm.examenPreocupacionalVigente,

                            onCheckedChange = {
                                vm.examenPreocupacionalVigente = it
                            }
                        )

                        Text(
                            text = "Examen preocupacional vigente",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }


                    // Test de esfuerzo
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Checkbox(
                            checked =
                                vm.testEsfuerzoVigente,

                            onCheckedChange = {
                                vm.testEsfuerzoVigente = it
                            }
                        )

                        Text(
                            text = "Test de esfuerzo vigente",
                            style = MaterialTheme.typography.bodyMedium
                        )
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
                        try {
                            selectorFotos.launch(arrayOf("image/*"))
                        } catch (_: ActivityNotFoundException) {
                            Toast.makeText(context, "No hay selector de archivos disponible", Toast.LENGTH_LONG).show()
                        }
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
                        try {
                            selectorFotos.launch(arrayOf("image/*"))
                        } catch (_: ActivityNotFoundException) {
                            Toast.makeText(context, "No hay selector de archivos disponible", Toast.LENGTH_LONG).show()
                        }
                    },
                    onEliminarFoto = { indiceFoto ->
                        vm.eliminarFotoItem(indice, indiceFoto)
                    }
                )
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
                        try {
                            selectorFotos.launch(arrayOf("image/*"))
                        } catch (_: ActivityNotFoundException) {
                            Toast.makeText(context, "No hay selector de archivos disponible", Toast.LENGTH_LONG).show()
                        }
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
                    containerColor = if (valRes.estaBloqueado) Color(0xFFDC2626) else if (sinInternet) Color(0xFF64748B) else Morado,
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
                        Text(
                            text =
                                if (vm.idEnEdicion != null)
                                    "Guardar cambios"
                                else
                                    "Guardar pre-chequeo",

                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
    }
}
