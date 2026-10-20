package com.duoc.aquachileapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.duoc.aquachileapp.ui.components.EtiquetaResultado
import com.duoc.aquachileapp.ui.components.EtiquetaSalud
import com.duoc.aquachileapp.ui.components.Fotos
import com.duoc.aquachileapp.viewmodel.HistorialViewModel

@Composable
fun DetalleScreen(
    vm: HistorialViewModel,
    id: Int,
    onVolver: () -> Unit
) {

    val preChequeo = vm.buscar(id)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        if (preChequeo == null) {

            Text("No se encontró el pre-chequeo")

        } else {

            EtiquetaResultado(preChequeo.resultado)

            Text(
                text = "${preChequeo.centro} · ${preChequeo.fecha}",
                style = MaterialTheme.typography.titleMedium
            )

            Text("Buzo: ${preChequeo.buzo}")
            Text("Supervisor: ${preChequeo.supervisor}")

            Text(
                text = "Checklist",
                style = MaterialTheme.typography.titleMedium
            )

            preChequeo.items.forEach { item ->
                Text("• ${item.nombre}: ${item.estado.texto}")
            }

            if (preChequeo.observaciones.isNotEmpty()) {
                Text(
                    text = "Observaciones",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(preChequeo.observaciones)
            }

            if (preChequeo.fotos.isNotEmpty()) {
                Text(
                    text = "Evidencias",
                    style = MaterialTheme.typography.titleMedium
                )
                Fotos(preChequeo.fotos)
            }

            Text(
                text = "Salud (datos ficticios)",
                style = MaterialTheme.typography.titleMedium
            )

            Text("Presión ${preChequeo.sistolica}/${preChequeo.diastolica} · Saturación ${preChequeo.saturacion}%")

            preChequeo.alertas.forEach { alerta ->
                Text("⚠ $alerta")
            }

            EtiquetaSalud(preChequeo.salud)
        }

        OutlinedButton(
            onClick = onVolver,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver")
        }
    }
}
