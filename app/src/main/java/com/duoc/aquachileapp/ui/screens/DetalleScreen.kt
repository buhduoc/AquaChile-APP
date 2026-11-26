package com.duoc.aquachileapp.ui.screens

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import android.content.ActivityNotFoundException
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Lock
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.duoc.aquachileapp.model.Estado
import com.duoc.aquachileapp.model.EvidenciaEquipo
import com.duoc.aquachileapp.ui.components.EtiquetaResultado
import com.duoc.aquachileapp.ui.components.EtiquetaSalud
import com.duoc.aquachileapp.ui.components.FondoBarra
import com.duoc.aquachileapp.ui.components.Fotos
import com.duoc.aquachileapp.ui.components.TarjetaEvidenciaEquipo
import com.duoc.aquachileapp.ui.theme.FondoApp
import com.duoc.aquachileapp.ui.theme.Morado
import com.duoc.aquachileapp.ui.theme.TextoOscuro
import com.duoc.aquachileapp.ui.theme.TextoSuave
import com.duoc.aquachileapp.viewmodel.HistorialViewModel
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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

private fun crearUriFotoDetalle(
    context: Context
): Uri {

    val carpeta =
        File(
            context.filesDir,
            "fotos"
        ).apply {
            mkdirs()
        }

    val archivo =
        File.createTempFile(
            "evidencia_",
            ".jpg",
            carpeta
        )

    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        archivo
    )
}



@Composable
fun DetalleScreen(
    vm: HistorialViewModel,
    id: Int,
    esSupervisor: Boolean = false,
    onEditar: (Int) -> Unit,
    onVolver: () -> Unit
) {
    val context = LocalContext.current
    val preChequeo = vm.buscar(id)
    var mostrarDialogoEliminar by remember { mutableStateOf(false) }
    var mostrarModalValidacion by remember { mutableStateOf(false) }



    // =====================================================
    // ESTADOS PARA EDITAR EVIDENCIAS
    // =====================================================

    /*
     * Guarda temporalmente la URI donde
     * la cámara va a guardar la nueva foto.
     */
    var uriFotoTomada by rememberSaveable {
        mutableStateOf<Uri?>(null)
    }


    /*
     * Guarda qué evidencia estamos modificando.
     *
     * Ejemplo:
     * Maleta de Aire
     * Filtros del Compresor
     */
    var evidenciaTarget by remember {
        mutableStateOf<EvidenciaEquipo?>(null)
    }


    // =====================================================
    // LAUNCHER 1 - CÁMARA
    // =====================================================

    val camaraEvidenciaLauncher =
        rememberLauncherForActivityResult(
            contract =
                ActivityResultContracts.TakePicture()
        ) { exito ->

            if (exito) {

                val evidencia =
                    evidenciaTarget

                val uri =
                    uriFotoTomada


                if (
                    evidencia != null &&
                    uri != null
                ) {

                    /*
                     * Creamos una copia de la evidencia,
                     * pero con la nueva fotografía.
                     */
                    val actualizada =
                        evidencia.copy(

                            uriFotoActual =
                                uri.toString(),

                            fechaHora =
                                LocalDateTime.now()
                                    .format(
                                        DateTimeFormatter.ofPattern(
                                            "dd-MM-yyyy HH:mm"
                                        )
                                    ),

                            responsable =
                                preChequeo?.supervisor
                                    ?: evidencia.responsable
                        )


                    /*
                     * AQUÍ SE HACE EL PUT
                     *
                     * PUT /api/evidencias/{id}
                     */
                    vm.actualizarEvidencia(
                        actualizada
                    )


                    // Limpiar estados temporales
                    evidenciaTarget = null
                    uriFotoTomada = null
                }
            }
        }


    // =====================================================
    // LAUNCHER 2 - GALERÍA
    // =====================================================

    val galeriaEvidenciaLauncher =
        rememberLauncherForActivityResult(
            contract =
                ActivityResultContracts.OpenDocument()
        ) { uri ->

            if (uri != null) {

                val evidencia =
                    evidenciaTarget


                val copiaLocal = copiarAdjuntoLocal(context, uri)
                if (evidencia != null && copiaLocal != null) {

                    /*
                     * Mismo proceso que con cámara,
                     * pero usando la URI elegida
                     * desde la galería.
                     */
                    val actualizada =
                        evidencia.copy(

                            uriFotoActual =
                                copiaLocal,

                            fechaHora =
                                LocalDateTime.now()
                                    .format(
                                        DateTimeFormatter.ofPattern(
                                            "dd-MM-yyyy HH:mm"
                                        )
                                    ),

                            responsable =
                                preChequeo?.supervisor
                                    ?: evidencia.responsable
                        )


                    /*
                     * AQUÍ TAMBIÉN SE HACE EL PUT
                     */
                    vm.actualizarEvidencia(
                        actualizada
                    )


                    evidenciaTarget = null
                }
            }
        }



    // GET evidencias desde Spring Boot
    LaunchedEffect(id) {

        vm.cargarEvidencias(id)
    }
    val evidenciasMostrar =

        if (vm.evidenciasConsultadas) {
            // Datos reales desde MySQL
            vm.evidenciasServidor

        } else {
            // Mientras carga o si no hay conexión,
            // usa los datos locales
            preChequeo?.evidenciasEquipos
                ?: emptyList()
        }

    // PANTALLA


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

            Text("Evaluación preventiva: no constituye autorización definitiva de zarpe.", modifier = Modifier.padding(12.dp), color = Color(0xFF475569))
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

            // ---- Evidencia Fotográfica de Equipos y Línea Base ----
            if (preChequeo.evidenciasEquipos.isNotEmpty()) {
                SeccionBlanca("Evidencia Fotográfica de Equipos (Línea Base Zarpe)") {

                    Text(
                        text =
                            if (vm.evidenciasConsultadas) {
                                "${evidenciasMostrar.size} evidencia(s) registradas en servidor"
                            } else {
                                "Mostrando evidencias locales"
                            },
                        fontSize = 12.sp,
                        color = TextoSuave
                    )


                    evidenciasMostrar.forEach { evidencia ->

                        TarjetaEvidenciaEquipo(
                            evidencia =
                                evidencia,

                            // CÁMARA → PUT

                            onTomarFoto = {

                                /*
                                 * Primero indicamos qué
                                 * evidencia queremos cambiar.
                                 */
                                evidenciaTarget =
                                    evidencia


                                /*
                                 * Creamos dónde se guardará
                                 * la nueva foto.
                                 */
                                val uri =
                                    crearUriFotoDetalle(
                                        context
                                    )


                                uriFotoTomada =
                                    uri


                                /*
                                 * Abrimos la cámara.
                                 *
                                 * Al terminar se ejecuta
                                 * camaraEvidenciaLauncher,
                                 * que hace el PUT.
                                 */
                                camaraEvidenciaLauncher
                                    .launch(
                                        uri
                                    )
                            },

                            // GALERÍA → PUT


                            onGaleria = {

                                /*
                                 * Guardamos cuál evidencia
                                 * queremos actualizar.
                                 */
                                evidenciaTarget =
                                    evidencia


                                /*
                                 * Abrimos selector de imágenes.
                                 *
                                 * Cuando selecciones una,
                                 * galeriaEvidenciaLauncher
                                 * hará el PUT.
                                 */
                                try {
                                    galeriaEvidenciaLauncher.launch(arrayOf("image/*"))
                                } catch (_: ActivityNotFoundException) {
                                    Toast.makeText(context, "No hay selector de archivos disponible", Toast.LENGTH_LONG).show()
                                }
                            },

                            // DELETE

                            onEliminar = {

                                /*
                                 * DELETE
                                 *
                                 * /api/evidencias/{id}
                                 */
                                vm.eliminarEvidencia(
                                    evidencia
                                )
                            },


                            /*
                             * Solo el Supervisor
                             * puede modificar/eliminar.
                             */
                            editable =
                                esSupervisor
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
                onClick = {
                    onEditar(
                        preChequeo.id
                    )
                },
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Morado
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {

                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Editar",
                    modifier = Modifier.size(20.dp)
                )

                Spacer(
                    Modifier.width(6.dp)
                )

                Text(
                    "Editar este Pre-chequeo",
                    fontWeight = FontWeight.Bold
                )

            }
            OutlinedButton(
                onClick = {
                    mostrarDialogoEliminar = true
                },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {

                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    modifier = Modifier.size(20.dp)
                )

                Text(
                    "Eliminar este Pre-chequeo",
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (preChequeo != null) {
                OutlinedButton(
                    onClick = { mostrarModalValidacion = true },
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Morado),
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.HealthAndSafety,
                        contentDescription = "Validación",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text("Validación", fontWeight = FontWeight.Bold)
                }
            }

            Button(
                onClick = onVolver,
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
            ) {
                Text("Volver", fontWeight = FontWeight.Bold)
            }
        }
    }

    if (mostrarModalValidacion && preChequeo != null) {
        val valRes = preChequeo.validacion

        AlertDialog(
            onDismissRequest = { mostrarModalValidacion = false },
            title = {
                Text("Validación de Seguridad", fontWeight = FontWeight.Bold)
            },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val estadoSync = vm.estadoPorId(id)
                    Text("Evaluación preventiva local", fontWeight = FontWeight.Bold, color = TextoOscuro)
                    Text(
                        text = when (estadoSync) {
                            "SINCRONIZADO" -> "Registro sincronizado"
                            "ERROR" -> "Pendiente de sincronizar (error)"
                            "SINCRONIZANDO" -> "Sincronización en curso"
                            "PENDIENTE" -> "Pendiente de sincronizar"
                            else -> "Sincronización no confirmada"
                        },
                        fontSize = 12.sp,
                        color = TextoSuave
                    )
                    Surface(
                        color = Color(0xFFFEE2E2),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF991B1B))
                                Spacer(Modifier.width(8.dp))
                                Text("Aprobación definitiva bloqueada", fontWeight = FontWeight.Bold, color = Color(0xFF991B1B), fontSize = 13.sp)
                            }
                            Text("Este resultado no constituye una autorización de zarpe ni de buceo. Datos de salud ficticios: solo demostración.", fontSize = 12.sp, color = Color(0xFF991B1B))
                        }
                    }
                    Surface(
                        color = valRes.semaforo.color.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = if (valRes.estaBloqueado) Icons.Default.Lock else Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = valRes.semaforo.color
                            )
                            Text(
                                text = valRes.semaforo.titulo,
                                color = valRes.semaforo.color,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }

                    Text("• IMC: ${valRes.imc} kg/m² (${valRes.imcCategoria})", fontSize = 13.sp, color = TextoOscuro)
                    Text("• Edad: ${preChequeo.edad} años | Test Esfuerzo: ${if (preChequeo.testEsfuerzoVigente) "Vigente" else "No Vigente / Pendiente"}", fontSize = 13.sp, color = TextoOscuro)
                    Text("• Examen Preocupacional: ${if (preChequeo.examenPreocupacionalVigente) "Vigente" else "Vencido"}", fontSize = 13.sp, color = TextoOscuro)
                    Text("• Escuela de Buceo Calbuco: ${preChequeo.puntajeEscuelaCalbuco}/100 pts", fontSize = 13.sp, color = TextoOscuro)

                    if (valRes.motivosBloqueo.isNotEmpty()) {
                        Text("Motivos de Bloqueo:", fontWeight = FontWeight.Bold, color = Color(0xFFDC2626), fontSize = 13.sp)
                        valRes.motivosBloqueo.forEach { motivo ->
                            Text("⛔ $motivo", color = Color(0xFF991B1B), fontSize = 12.sp)
                        }
                    }

                    if (valRes.advertencias.isNotEmpty()) {
                        Text("Advertencias:", fontWeight = FontWeight.Bold, color = Color(0xFFD97706), fontSize = 13.sp)
                        valRes.advertencias.forEach { adv ->
                            Text("⚠️ $adv", color = Color(0xFFB45309), fontSize = 12.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { mostrarModalValidacion = false }) {
                    Text("Cerrar")
                }
            }
        )
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
