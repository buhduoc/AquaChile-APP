package com.duoc.aquachileapp.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAlert
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.duoc.aquachileapp.ui.components.Campo
import com.duoc.aquachileapp.ui.components.CampoDesplegableCentro
import com.duoc.aquachileapp.ui.components.FondoBarra
import com.duoc.aquachileapp.ui.theme.Morado
import com.duoc.aquachileapp.viewmodel.BitacoraViewModel

@Composable
fun BitacoraScreen(
    vm: BitacoraViewModel = viewModel(),
    nombreBuzo: String? = null
) {
    val vDescNum = vm.velDescensoStr.toDoubleOrNull() ?: 0.0
    val vAscNum = vm.velAscensoStr.toDoubleOrNull() ?: 0.0

    val excesoDesc = vDescNum > 24.0
    val excesoAsc = vAscNum > 9.0
    val hayAlertaEnVivo = excesoDesc || excesoAsc

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoBarra)
            .verticalScroll(rememberScrollState())
    ) {

        // Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 22.dp, end = 22.dp, top = 8.dp, bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "Bitácora Post-Inmersión",
                color = Color.White,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Registro de telemetría: tiempo de fondo, profundidad y velocidades de inmersión",
                color = Color.White.copy(alpha = 0.8f),
                style = MaterialTheme.typography.bodyMedium
            )
        }

        // Contenedor Blanco
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 600.dp)
                .background(
                    Color.White,
                    RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
                )
                .padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text(
                text = "Registrar Nueva Telemetría Post-Inmersión",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2937)
            )

            CampoDesplegableCentro(
                valor = vm.centro,
                alSeleccionar = { vm.centro = it }
            )

            Campo(
                valor = vm.buzo.ifBlank { nombreBuzo ?: "" },
                alCambiar = { vm.buzo = it },
                etiqueta = "Buzo A Cargo"
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Campo(
                    valor = vm.tiempoFondoStr,
                    alCambiar = { vm.tiempoFondoStr = it },
                    etiqueta = "Tiempo Fondo (min)",
                    modifier = Modifier.weight(1f),
                    numero = true
                )
                Campo(
                    valor = vm.profundidadStr,
                    alCambiar = { vm.profundidadStr = it },
                    etiqueta = "Profundidad Máx (m)",
                    modifier = Modifier.weight(1f),
                    numero = true
                )
            }

            // Velocidades de Descenso y Ascenso
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Campo(
                    valor = vm.velDescensoStr,
                    alCambiar = { vm.velDescensoStr = it },
                    etiqueta = "Descenso (m/min - máx 24)",
                    modifier = Modifier.weight(1f),
                    numero = true
                )
                Campo(
                    valor = vm.velAscensoStr,
                    alCambiar = { vm.velAscensoStr = it },
                    etiqueta = "Ascenso (m/min - máx 9)",
                    modifier = Modifier.weight(1f),
                    numero = true
                )
            }

            // Panel de Detección de Alerta en Vivo al Jefe de Centro
            if (hayAlertaEnVivo) {
                Surface(
                    color = Color(0xFFFEE2E2),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AddAlert,
                                contentDescription = "Alerta",
                                tint = Color(0xFFDC2626),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "ALERTA AUTOMÁTICA AL JEFE DE CENTRO",
                                color = Color(0xFFDC2626),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }

                        if (excesoDesc) {
                            Text(
                                text = "• Exceso Velocidad de Descenso: $vDescNum m/min (Límite máximo permitido: 24.0 m/min)",
                                color = Color(0xFF991B1B),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        if (excesoAsc) {
                            Text(
                                text = "• ALERTA CRÍTICA: Exceso Velocidad de Ascenso: $vAscNum m/min (Límite máximo seguro: 9.0 m/min - Riesgo Descompresivo)",
                                color = Color(0xFF991B1B),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Campo(
                valor = vm.observaciones,
                alCambiar = { vm.observaciones = it },
                etiqueta = "Observaciones de la Inmersión"
            )

            vm.error?.let { err ->
                Text(
                    text = err,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Button(
                onClick = {
                    vm.registrar(nombreBuzo = nombreBuzo)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (hayAlertaEnVivo) Color(0xFFDC2626) else Morado,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Icon(
                    imageVector = if (hayAlertaEnVivo) Icons.Default.Warning else Icons.Default.Save,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = if (hayAlertaEnVivo) "Guardar y Notificar Alerta a Jefe de Centro" else "Guardar Registros de Inmersión",
                    fontWeight = FontWeight.Bold
                )
            }

            // Historial de Bitácoras
            Text(
                text = "Historial de Bitácoras de Inmersión (${vm.registrosBitacora.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2937),
                modifier = Modifier.padding(top = 8.dp)
            )

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                vm.registrosBitacora.forEach { bit ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (bit.tieneAlerta) Color(0xFFFEF2F2) else Color(0xFFF9FAFB)
                        ),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${bit.centro} · ${bit.buzo}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF111827)
                                )

                                Surface(
                                    color = if (bit.tieneAlerta) Color(0xFFFEE2E2) else Color(0xFFD1FAE5),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = if (bit.tieneAlerta) "Alerta Jefe Centro 🚨" else "Parámetros Normales ✓",
                                        color = if (bit.tieneAlerta) Color(0xFFDC2626) else Color(0xFF047857),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Text(
                                text = "Fecha/Hora: ${bit.fechaHora} | Fondo: ${bit.tiempoFondoMin} min | Prof: ${bit.profundidadMaxM} m",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF4B5563)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Surface(
                                    color = if (bit.excesoDescenso) Color(0xFFFEE2E2) else Color(0xFFEFF6FF),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Text("Descenso", fontSize = 10.sp, color = Color(0xFF6B7280))
                                        Text(
                                            "${bit.velocidadDescensoMMin} m/min",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (bit.excesoDescenso) Color(0xFFDC2626) else Morado
                                        )
                                        if (bit.excesoDescenso) Text(">24 m/min (Exceso)", fontSize = 9.sp, color = Color(0xFFDC2626))
                                    }
                                }

                                Surface(
                                    color = if (bit.excesoAscenso) Color(0xFFFEE2E2) else Color(0xFFEFF6FF),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Text("Ascenso", fontSize = 10.sp, color = Color(0xFF6B7280))
                                        Text(
                                            "${bit.velocidadAscensoMMin} m/min",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (bit.excesoAscenso) Color(0xFFDC2626) else Morado
                                        )
                                        if (bit.excesoAscenso) Text(">9 m/min (Exceso Crítico)", fontSize = 9.sp, color = Color(0xFFDC2626))
                                    }
                                }
                            }

                            if (bit.alertasJefeCentro.isNotEmpty()) {
                                Surface(
                                    color = Color(0xFFFEE2E2),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(10.dp),
                                        verticalArrangement = Arrangement.spacedBy(2.dp)
                                    ) {
                                        bit.alertasJefeCentro.forEach { al ->
                                            Text(al, fontSize = 11.sp, color = Color(0xFF991B1B), fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }

                            if (bit.observaciones.isNotEmpty()) {
                                Text("Observaciones: ${bit.observaciones}", style = MaterialTheme.typography.bodySmall, color = Color(0xFF374151))
                            }
                        }
                    }
                }
            }
        }
    }
}
