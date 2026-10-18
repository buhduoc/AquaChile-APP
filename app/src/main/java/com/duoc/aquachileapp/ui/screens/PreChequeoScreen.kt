package com.duoc.aquachileapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.duoc.aquachileapp.viewmodel.HistorialViewModel

@Composable
fun PreChequeoScreen(
    vm: HistorialViewModel,
    nombreSupervisor: String,
    onContinuar: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text(
            text = "Nuevo pre-chequeo",
            style = MaterialTheme.typography.titleLarge
        )

        Text("Supervisor: $nombreSupervisor")

        OutlinedTextField(
            value = vm.centro,
            onValueChange = { vm.centro = it },
            label = { Text("Centro de operación") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = vm.buzo,
            onValueChange = { vm.buzo = it },
            label = { Text("Buzo") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = onContinuar,
            enabled = vm.centro.isNotBlank() && vm.buzo.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ingresar al CheckList")
        }
    }
}