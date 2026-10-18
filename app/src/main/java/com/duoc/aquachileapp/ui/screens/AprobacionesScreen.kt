package com.duoc.aquachileapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.duoc.aquachileapp.viewmodel.CuentasViewModel

@Composable
fun AprobacionesScreen(
    cuentas: CuentasViewModel
) {

    val pendientes = cuentas.pendientes

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Cuentas pendientes de aprobación",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (pendientes.isEmpty()) {
            Text("No hay cuentas pendientes")
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(pendientes) { cuenta ->

                Card(modifier = Modifier.fillMaxWidth()) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = cuenta.nombre,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text("Usuario: ${cuenta.usuario}")
                            Text("Rol: ${cuenta.rol}")
                        }

                        Button(
                            onClick = {
                                cuentas.aprobar(cuenta)
                            }
                        ) {
                            Text("Aprobar")
                        }
                    }
                }
            }
        }
    }
}