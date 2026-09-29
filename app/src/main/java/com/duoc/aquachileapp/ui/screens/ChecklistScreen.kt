package com.duoc.aquachileapp.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.duoc.aquachileapp.viewmodel.AquaCheckViewmodel


@Composable
fun ChecklistScreen(
    viewmodel: AquaCheckViewmodel
) {
    Column (
        modifier = Modifier.padding(20.dp)
    ) {
        Text("Checklist de Seguridad")

        viewmodel.checklist.forEachIndexed { index, item ->
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = item.aprobado,
                    onCheckedChange = {
                        viewmodel.cambiarEstado(index)
                    }
                )

                Text(
                    text = item.nombre
                )
            }
        }
    }
}