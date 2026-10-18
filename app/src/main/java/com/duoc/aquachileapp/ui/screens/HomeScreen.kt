package com.duoc.aquachileapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.duoc.aquachileapp.model.Resultado
import com.duoc.aquachileapp.ui.components.TarjetaPreChequeo
import com.duoc.aquachileapp.ui.components.colorDe
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
            .padding(16.dp),

        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = "Hola, $nombreUsuario",
            style = MaterialTheme.typography.headlineSmall
        )

        Text(
            text = "Sistema de pre-chequeo de seguridad para operaciones de buceo."
        )

        if (esSupervisor && pendientes > 0) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Cuentas pendientes de aprobación: $pendientes")

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

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Contador(
                resultado = Resultado.CUMPLE,
                cantidad = registros.count { it.resultado == Resultado.CUMPLE },
                modifier = Modifier.weight(1f)
            )
            Contador(
                resultado = Resultado.OBSERVADO,
                cantidad = registros.count { it.resultado == Resultado.OBSERVADO },
                modifier = Modifier.weight(1f)
            )
            Contador(
                resultado = Resultado.REQUIERE_REVISION,
                cantidad = registros.count { it.resultado == Resultado.REQUIERE_REVISION },
                modifier = Modifier.weight(1f)
            )
        }

        Button(
            onClick = {
                navController.navigate("prechequeo")
            },
            modifier = Modifier.fillMaxWidth()
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
                style = MaterialTheme.typography.titleMedium
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
            TarjetaPreChequeo(preChequeo)
        }
    }
}

@Composable
private fun Contador(
    resultado: Resultado,
    cantidad: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = colorDe(resultado)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "$cantidad",
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = resultado.texto,
                color = Color.White,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}