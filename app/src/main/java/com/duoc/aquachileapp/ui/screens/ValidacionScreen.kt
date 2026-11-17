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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.duoc.aquachileapp.model.PREGUNTAS_SALUD
import com.duoc.aquachileapp.model.SemaforoRiesgo
import com.duoc.aquachileapp.ui.components.Campo
import com.duoc.aquachileapp.ui.components.FilaChecklist
import com.duoc.aquachileapp.ui.components.FondoBarra
import com.duoc.aquachileapp.ui.theme.Morado
import com.duoc.aquachileapp.viewmodel.ValidacionViewModel

@Composable
fun ValidacionScreen(
    vm: ValidacionViewModel = viewModel()
) {
    val resultado = vm.resultadoValidacion

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
                text = "Validación de Seguridad",
                color = Color.White,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Cálculo de Semáforo de Riesgo y Control de Bloqueos Preventivos",
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

            // Tarjeta Principal del Semáforo
            Card(
                colors = CardDefaults.cardColors(containerColor = resultado.semaforo.color.copy(alpha = 0.12f)),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            color = resultado.semaforo.color,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = if (resultado.estaBloqueado) Icons.Default.Lock else if (resultado.semaforo == SemaforoRiesgo.AMARILLO) Icons.Default.Warning else Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.padding(10.dp)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = resultado.semaforo.titulo,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = resultado.semaforo.color
                            )

                            Text(
                                text = "IMC Calibrado: ${resultado.imc} kg/m² (${resultado.imcCategoria})",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF374151)
                            )
                        }
                    }

                    if (resultado.estaBloqueado) {
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
                                        imageVector = Icons.Default.Block,
                                        contentDescription = "Bloqueado",
                                        tint = Color(0xFFDC2626),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        text = "CIERRE Y APROBACIÓN BLOQUEADOS",
                                        color = Color(0xFFDC2626),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }

                                Text(
                                    text = "Motivos registrados de bloqueo:",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF991B1B)
                                )

                                resultado.motivosBloqueo.forEach { motivo ->
                                    Text(
                                        text = "• $motivo",
                                        color = Color(0xFFB91C1C),
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }

                    if (resultado.advertencias.isNotEmpty()) {
                        Surface(
                            color = Color(0xFFFEF3C7),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "Advertencias y observaciones:",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFFD97706)
                                )

                                resultado.advertencias.forEach { adv ->
                                    Text(
                                        text = "• $adv",
                                        color = Color(0xFFB45309),
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Datos Biométricos
            Text(
                text = "1. Datos Biométricos del Buzo",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2937)
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Campo(vm.edad, { vm.edad = it }, "Edad (años)", Modifier.weight(1f), numero = true)
                Campo(vm.pesoKg, { vm.pesoKg = it }, "Peso (kg)", Modifier.weight(1f), numero = true)
                Campo(vm.alturaCm, { vm.alturaCm = it }, "Altura (cm)", Modifier.weight(1f), numero = true)
            }

            // Acreditaciones y Certificados
            Text(
                text = "2. Acreditaciones y Escuela de Buceo Calbuco",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2937)
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Examen Preocupacional Vigente", fontWeight = FontWeight.Medium)
                        Switch(
                            checked = vm.examenPreocupacionalVigente,
                            onCheckedChange = { vm.examenPreocupacionalVigente = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Morado)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Test de Esfuerzo Vigente (>45 años exigido)", fontWeight = FontWeight.Medium)
                        Switch(
                            checked = vm.testEsfuerzoVigente,
                            onCheckedChange = { vm.testEsfuerzoVigente = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Morado)
                        )
                    }

                    Campo(
                        valor = vm.puntajeEscuelaCalbuco,
                        alCambiar = { vm.puntajeEscuelaCalbuco = it },
                        etiqueta = "Puntaje Escuela de Buceo Calbuco (0 - 100)",
                        numero = true
                    )
                }
            }

            // Signos Vitales y Encuesta Preventiva
            Text(
                text = "3. Signos Vitales y Encuesta Preventiva de Salud",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2937)
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Campo(vm.sistolica, { vm.sistolica = it }, "Sistólica (mmHg)", Modifier.weight(1f), numero = true)
                Campo(vm.diastolica, { vm.diastolica = it }, "Diastólica (mmHg)", Modifier.weight(1f), numero = true)
                Campo(vm.saturacion, { vm.saturacion = it }, "Saturación O₂ (%)", Modifier.weight(1f), numero = true)
            }

            PREGUNTAS_SALUD.forEach { pregunta ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = pregunta in vm.alertasSalud,
                        onCheckedChange = { vm.alternarAlerta(pregunta) }
                    )
                    Text(pregunta, style = MaterialTheme.typography.bodyMedium)
                }
            }

            // Checklist de Equipos
            Text(
                text = "4. Evaluación de Ítems Críticos de Equipos",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2937)
            )

            vm.itemsChecklist.forEachIndexed { indice, item ->
                FilaChecklist(
                    item = item,
                    alElegir = { estado -> vm.cambiarEstadoItem(indice, estado) }
                )
            }

            // Botón de Aprobación Final
            Button(
                onClick = {},
                enabled = !resultado.estaBloqueado,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (resultado.estaBloqueado) Color(0xFFDC2626) else Morado,
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
                    Icon(
                        imageVector = if (resultado.estaBloqueado) Icons.Default.Lock else Icons.Default.HealthAndSafety,
                        contentDescription = null
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = if (resultado.estaBloqueado) "Aprobación Bloqueada por Seguridad" else "Aprobar Validación de Seguridad",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
