package com.duoc.aquachileapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.duoc.aquachileapp.model.Resultado
import com.duoc.aquachileapp.ui.components.TarjetaPreChequeo
import com.duoc.aquachileapp.ui.theme.Morado
import com.duoc.aquachileapp.ui.components.recordarSinInternet
import com.duoc.aquachileapp.ui.theme.TextoSuave
import com.duoc.aquachileapp.viewmodel.CuentasViewModel
import com.duoc.aquachileapp.viewmodel.HistorialViewModel
import com.duoc.aquachileapp.viewmodel.WeatherViewModel

data class InfoClima(
    val ciudad: String,
    val temperatura: String,
    val condicion: String,
    val maxMin: String,
    val gradientColors: List<Color>
)

@Composable
fun HomeScreen(
    navController: NavHostController,
    nombreUsuario: String,
    esSupervisor: Boolean,
    historial: HistorialViewModel,
    cuentas: CuentasViewModel
) {

    val sinInternet = recordarSinInternet()
    val registros = historial.registros
    val pendientes = cuentas.pendientes.size

    val weatherVm: WeatherViewModel = viewModel()
    var ciudadIndex by rememberSaveable { mutableStateOf(0) }
    var mostrarDialogoClima by rememberSaveable { mutableStateOf(false) }

    val ciudadesClima = listOf(

        InfoClima(
            ciudad = "Centro Puerto Montt",
            temperatura = "",
            condicion = "",
            maxMin = "",
            gradientColors = listOf(
                Color(0xFF45698F),
                Color(0xFF162A3A)
            )
        ),

        InfoClima(
            ciudad = "Centro Osorno",
            temperatura = "",
            condicion = "",
            maxMin = "",
            gradientColors = listOf(
                Color(0xFF4F8DD6),
                Color(0xFF214B72)
            )
        ),

        InfoClima(
            ciudad = "Centro Calbuco",
            temperatura = "",
            condicion = "",
            maxMin = "",
            gradientColors = listOf(
                Color(0xFF23525D),
                Color(0xFF142D35)
            )
        )
    )

    val ciudadSeleccionada =
        ciudadesClima.getOrElse(ciudadIndex) {
            ciudadesClima.first()
        }


    LaunchedEffect(
        ciudadSeleccionada.ciudad
    ) {
        weatherVm.cargarClima(
            ciudadSeleccionada.ciudad
        )
    }



    val datosApi =
        weatherVm.clima

    val maxima =
        datosApi
            ?.daily
            ?.maxima
            ?.firstOrNull()

    val minima =
        datosApi
            ?.daily
            ?.minima
            ?.firstOrNull()


    val climaActual =
        ciudadSeleccionada.copy(

            temperatura =
                datosApi
                    ?.current
                    ?.temperatura
                    ?.let {
                        "${it.toInt()}°"
                    }
                    ?: "--°",

            condicion =
                weatherVm.descripcion(
                    datosApi
                        ?.current
                        ?.weatherCode
                ),

            maxMin =
                "H: ${
                    maxima?.toInt() ?: "--"
                }°  L: ${
                    minima?.toInt() ?: "--"
                }°"
        )

    if (mostrarDialogoClima) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoClima = false },
            title = { Text("Seleccionar Centro") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    ciudadesClima.forEachIndexed { index, info ->
                        OutlinedButton(
                            onClick = {
                                ciudadIndex = index
                                mostrarDialogoClima = false
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = info.ciudad,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { mostrarDialogoClima = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 12.dp),

        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Spacer(modifier = Modifier.height(4.dp))

        TarjetaClima(
            info = climaActual,
            onClick = {
                mostrarDialogoClima = true
            }
        )

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

        // Tarjetas moradas con barra de progreso
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
            colors = ButtonDefaults.buttonColors(containerColor = if (sinInternet) Color(0xFF64748B) else Morado),
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
                TarjetaPreChequeo(preChequeo, historial.estadoPorId(preChequeo.id))
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun TarjetaClima(
    info: InfoClima,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(95.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.linearGradient(if (recordarSinInternet()) listOf(Color(0xFF475569), Color(0xFF64748B)) else info.gradientColors))
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = info.ciudad,
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = info.condicion,
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Toque para seleccionar clima",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 10.sp
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = info.temperatura,
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Light
                    )
                    Text(
                        text = info.maxMin,
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 11.sp
                    )
                }
            }
        }
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
        colors = CardDefaults.cardColors(containerColor = if (recordarSinInternet()) Color(0xFF64748B) else Morado)
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
