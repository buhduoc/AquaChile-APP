package com.duoc.aquachileapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PersonOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.duoc.aquachileapp.model.Cuenta
import com.duoc.aquachileapp.model.PreChequeo
import com.duoc.aquachileapp.ui.components.EtiquetaResultado
import com.duoc.aquachileapp.ui.components.FondoBarra
import com.duoc.aquachileapp.ui.theme.Morado
import com.duoc.aquachileapp.viewmodel.CuentasViewModel
import com.duoc.aquachileapp.viewmodel.HistorialViewModel

@Composable
fun AprobacionesScreen(
    cuentas: CuentasViewModel,
    historial: HistorialViewModel
) {
    var tabSeleccionada by remember { mutableIntStateOf(0) }
    var cuentaAEliminar by remember { mutableStateOf<Cuenta?>(null) }
    var preChequeoAEliminar by remember { mutableStateOf<PreChequeo?>(null) }

    val pendientes = cuentas.pendientes
    val todasLasCuentas = cuentas.cuentas
    val registros = historial.registros

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoBarra)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 22.dp, end = 22.dp, top = 8.dp, bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "Administración de Supervisor",
                color = Color.White,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Control exclusivo de permisos, cuentas y registros",
                color = Color.White.copy(alpha = 0.8f),
                style = MaterialTheme.typography.bodyMedium
            )
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            TabRow(
                selectedTabIndex = tabSeleccionada,
                containerColor = Color.White,
                contentColor = Morado,
                indicator = { tabPositions ->
                    if (tabSeleccionada < tabPositions.size) {
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[tabSeleccionada]),
                            color = Morado
                        )
                    }
                }
            ) {
                Tab(
                    selected = tabSeleccionada == 0,
                    onClick = { tabSeleccionada = 0 },
                    text = {
                        Text(
                            text = "Solicitudes (${pendientes.size})",
                            fontWeight = if (tabSeleccionada == 0) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
                Tab(
                    selected = tabSeleccionada == 1,
                    onClick = { tabSeleccionada = 1 },
                    text = {
                        Text(
                            text = "Cuentas (${todasLasCuentas.size})",
                            fontWeight = if (tabSeleccionada == 1) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
                Tab(
                    selected = tabSeleccionada == 2,
                    onClick = { tabSeleccionada = 2 },
                    text = {
                        Text(
                            text = "Pre-chequeos (${registros.size})",
                            fontWeight = if (tabSeleccionada == 2) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }

            when (tabSeleccionada) {
                0 -> {
                    // Tab 0: Aprobaciones de cuentas pendientes
                    if (pendientes.isEmpty()) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF3F4F6)),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Sin pendientes",
                                    tint = Color(0xFF10B981),
                                    modifier = Modifier.height(48.dp)
                                )
                                Text(
                                    text = "No hay solicitudes pendientes",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1F2937)
                                )
                                Text(
                                    text = "Todas las cuentas están aprobadas.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF6B7280)
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(pendientes) { cuenta ->
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    shape = RoundedCornerShape(16.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(
                                            modifier = Modifier.weight(1f),
                                            verticalArrangement = Arrangement.spacedBy(2.dp)
                                        ) {
                                            Text(
                                                text = cuenta.nombre,
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF111827)
                                            )
                                            Text(
                                                text = "Usuario: ${cuenta.usuario}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color(0xFF4B5563)
                                            )
                                            Surface(
                                                color = Color(0xFFFEF3C7),
                                                shape = RoundedCornerShape(6.dp),
                                                modifier = Modifier.padding(top = 2.dp)
                                            ) {
                                                Text(
                                                    text = "Rol: ${cuenta.rol}",
                                                    color = Color(0xFFD97706),
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                                )
                                            }
                                        }

                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Button(
                                                onClick = { cuentas.aprobar(cuenta) },
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = Morado,
                                                    contentColor = Color.White
                                                ),
                                                shape = RoundedCornerShape(12.dp)
                                            ) {
                                                Text("Aprobar", fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                1 -> {
                    // Tab 1: Gestión y eliminación de cuentas
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(todasLasCuentas) { cuenta ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                shape = RoundedCornerShape(16.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(
                                        modifier = Modifier.weight(1f),
                                        verticalArrangement = Arrangement.spacedBy(2.dp)
                                    ) {
                                        Text(
                                            text = cuenta.nombre,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF111827)
                                        )
                                        Text(
                                            text = "Usuario: ${cuenta.usuario}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color(0xFF4B5563)
                                        )
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                                            modifier = Modifier.padding(top = 4.dp)
                                        ) {
                                            Surface(
                                                color = if (cuenta.rol.contains("Supervisor", ignoreCase = true)) Color(0xFFEEF2FF) else Color(0xFFE0F2FE),
                                                shape = RoundedCornerShape(6.dp)
                                            ) {
                                                Text(
                                                    text = cuenta.rol,
                                                    color = if (cuenta.rol.contains("Supervisor", ignoreCase = true)) Morado else Color(0xFF0369A1),
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                                )
                                            }

                                            Surface(
                                                color = if (cuenta.aprobada) Color(0xFFD1FAE5) else Color(0xFFFEF3C7),
                                                shape = RoundedCornerShape(6.dp)
                                            ) {
                                                Text(
                                                    text = if (cuenta.aprobada) "Aprobada" else "Pendiente",
                                                    color = if (cuenta.aprobada) Color(0xFF047857) else Color(0xFFB45309),
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                    }

                                    OutlinedButton(
                                        onClick = { cuentaAEliminar = cuenta },
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            contentColor = Color(0xFFDC2626)
                                        ),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Borrar",
                                            modifier = Modifier.height(18.dp)
                                        )
                                        Text(" Borrar", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                2 -> {
                    // Tab 2: Gestión y eliminación de pre-chequeos
                    if (registros.isEmpty()) {
                        Text(
                            text = "No hay pre-chequeos registrados",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF6B7280)
                        )
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(registros) { preChequeo ->
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    shape = RoundedCornerShape(16.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "${preChequeo.centro} · ${preChequeo.buzo}",
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold
                                            )
                                            EtiquetaResultado(preChequeo.resultado)
                                        }

                                        Text(
                                            text = "Fecha: ${preChequeo.fecha} | Supervisor: ${preChequeo.supervisor}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color(0xFF6B7280)
                                        )

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.End
                                        ) {
                                            OutlinedButton(
                                                onClick = { preChequeoAEliminar = preChequeo },
                                                colors = ButtonDefaults.outlinedButtonColors(
                                                    contentColor = Color(0xFFDC2626)
                                                ),
                                                shape = RoundedCornerShape(12.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Delete,
                                                    contentDescription = "Borrar",
                                                    modifier = Modifier.height(18.dp)
                                                )
                                                Text(" Borrar Pre-chequeo", fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal Confirmación Eliminar Cuenta
    cuentaAEliminar?.let { cuenta ->
        AlertDialog(
            onDismissRequest = { cuentaAEliminar = null },
            title = {
                Text("¿Eliminar cuenta?", fontWeight = FontWeight.Bold)
            },
            text = {
                Text("Se eliminará permanentemente la cuenta de ${cuenta.nombre} (${cuenta.usuario}). Esta acción no se puede deshacer.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        cuentas.eliminar(cuenta)
                        cuentaAEliminar = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                ) {
                    Text("Eliminar", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { cuentaAEliminar = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Modal Confirmación Eliminar Pre-chequeo
    preChequeoAEliminar?.let { preChequeo ->
        AlertDialog(
            onDismissRequest = { preChequeoAEliminar = null },
            title = {
                Text("¿Eliminar pre-chequeo?", fontWeight = FontWeight.Bold)
            },
            text = {
                Text("Se eliminará permanentemente el pre-chequeo de ${preChequeo.centro} (${preChequeo.fecha}). Esta acción no se puede deshacer.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        historial.eliminar(preChequeo)
                        preChequeoAEliminar = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                ) {
                    Text("Eliminar", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { preChequeoAEliminar = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
