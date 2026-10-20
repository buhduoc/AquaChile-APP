package com.duoc.aquachileapp.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.duoc.aquachileapp.model.PREGUNTAS_SALUD
import com.duoc.aquachileapp.model.PreChequeo
import com.duoc.aquachileapp.model.Usuario
import com.duoc.aquachileapp.ui.components.Campo
import com.duoc.aquachileapp.ui.components.EtiquetaSalud
import com.duoc.aquachileapp.ui.components.FilaChecklist
import com.duoc.aquachileapp.ui.components.Fotos
import com.duoc.aquachileapp.viewmodel.NuevoViewModel

@Composable
fun PreChequeoScreen(
    vm: NuevoViewModel,
    usuario: Usuario?,
    onGuardar: (PreChequeo) -> Unit
) {

    LaunchedEffect(Unit) {
        vm.prepararDatos(usuario)
    }

    val selectorFotos = rememberLauncherForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia()
    ) { uris ->
        vm.fotos.addAll(uris.map { it.toString() })
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text(
            text = "Nuevo pre-chequeo",
            style = MaterialTheme.typography.titleLarge
        )

        Text(
            text = "Datos de la faena",
            style = MaterialTheme.typography.titleMedium
        )

        Campo(vm.centro, { vm.centro = it }, "Centro de operación")
        Campo(vm.buzo, { vm.buzo = it }, "Buzo")
        Campo(vm.supervisor, { vm.supervisor = it }, "Supervisor")

        Text(
            text = "Checklist de seguridad",
            style = MaterialTheme.typography.titleMedium
        )

        vm.items.forEachIndexed { indice, item ->
            FilaChecklist(
                item = item,
                alElegir = { estado -> vm.cambiarEstado(indice, estado) }
            )
        }

        Campo(vm.observaciones, { vm.observaciones = it }, "Observaciones")

        Text(
            text = "Evidencias",
            style = MaterialTheme.typography.titleMedium
        )

        OutlinedButton(
            onClick = {
                selectorFotos.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            }
        ) {
            Text("Agregar fotos")
        }

        Fotos(vm.fotos)

        Text(
            text = "Salud del buzo (datos ficticios)",
            style = MaterialTheme.typography.titleMedium
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Campo(vm.sistolica, { vm.sistolica = it }, "Sistólica", Modifier.weight(1f), numero = true)
            Campo(vm.diastolica, { vm.diastolica = it }, "Diastólica", Modifier.weight(1f), numero = true)
        }

        Campo(vm.saturacion, { vm.saturacion = it }, "Saturación O₂ (%)", numero = true)

        PREGUNTAS_SALUD.forEach { pregunta ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = pregunta in vm.alertas,
                    onCheckedChange = { vm.marcarAlerta(pregunta) }
                )
                Text(pregunta)
            }
        }

        vm.salud?.let { nivel ->
            EtiquetaSalud(nivel)
        }

        vm.error?.let { mensaje ->
            Text(
                text = mensaje,
                color = MaterialTheme.colorScheme.error
            )
        }

        Button(
            onClick = {
                val preChequeo = vm.crear()
                if (preChequeo != null) {
                    onGuardar(preChequeo)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar pre-chequeo")
        }
    }
}
