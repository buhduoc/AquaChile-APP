package com.duoc.aquachileapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.duoc.aquachileapp.model.Resultado
import com.duoc.aquachileapp.ui.components.PastillaTab
import com.duoc.aquachileapp.ui.components.TarjetaPreChequeo
import com.duoc.aquachileapp.ui.theme.Morado
import com.duoc.aquachileapp.ui.theme.TextoOscuro
import com.duoc.aquachileapp.ui.theme.TextoSuave
import com.duoc.aquachileapp.viewmodel.CuentasViewModel
import com.duoc.aquachileapp.viewmodel.HistorialViewModel

@Composable
fun HomeScreen(
    navController: NavHostController,
    nombreUsuario: String,
    esSupervisor: Boolean,
    historial: HistorialViewModel,
    cuentas: CuentasViewModel
) {

    val registros = historial.registros
    val pendientes = cuentas.pendientes.size

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 8.dp),

        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Column {
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(fontWeight = FontWeight.Normal)) { append("Hola, ") }
                    withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) { append("$nombreUsuario!") }
                },
                fontSize = 22.sp,
                color = TextoOscuro
            )
            Text(
                text = "Sistema de pre-chequeo de seguridad para operaciones de buceo.",
                fontSize = 12.sp,
                color = TextoSuave
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            PastillaTab("Resumen", seleccionada = true, onClick = {})
            PastillaTab("Historial", seleccionada = false) { navController.navigate("historial") }
            PastillaTab("Nuevo", seleccionada = false) { navController.navigate("prechequeo") }
        }

        if (esSupervisor && pendientes > 0) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Cuentas pendientes de aprobación: $pendientes", fontSize = 13.sp)

                    TextButton(
                        onClick = {
                            navController.navigate("aprobaciones")
                        }
                    ) {
                        Text("Revisar")
                    }
                }
            }
        }

        // Tarjetas moradas con barra de progreso (como "Back End Development" de la imagen)
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Resultado.entries.forEach { resultado ->
                Contador(
                    resultado = resultado,
                    cantidad = registros.count { it.resultado == resultado },
                    total = registros.size
                )
            }
        }

        Button(
            onClick = {
                navController.navigate("prechequeo")
            },
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Nuevo Pre-chequeo")
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Últimos pre-chequeos",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )

            TextButton(
                onClick = {
                    navController.navigate("historial")
                }
            ) {
                Text("Ver todos")
            }
        }

        registros.take(3).forEach { preChequeo ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        navController.navigate("detalle/${preChequeo.id}")
                    }
            ) {
                TarjetaPreChequeo(preChequeo)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun Contador(
    resultado: Resultado,
    cantidad: Int,
    total: Int,
    modifier: Modifier = Modifier
) {
    val porcentaje = if (total == 0) 0 else cantidad * 100 / total

    Card(
        modifier = modifier
            .width(220.dp)
            .height(125.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Morado)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "$cantidad de $total",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 10.sp,
                modifier = Modifier.align(Alignment.End)
            )
            Text(
                text = resultado.texto,
                color = Color.White,
                fontSize = 17.sp,
                fontWeight = FontWeight.Medium
            )
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Progreso", color = Color.White, fontSize = 10.sp)
                    Text("$porcentaje%", color = Color.White, fontSize = 10.sp)
                }
                LinearProgressIndicator(
                    progress = { porcentaje / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp),
                    color = Color.White,
                    trackColor = Color.White.copy(alpha = 0.3f)
                )
            }
        }
    }
}
