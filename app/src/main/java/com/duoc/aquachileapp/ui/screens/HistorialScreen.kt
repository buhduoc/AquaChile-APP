package com.duoc.aquachileapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.duoc.aquachileapp.ui.components.TarjetaPreChequeo
import com.duoc.aquachileapp.viewmodel.HistorialViewModel

@Composable
fun HistorialScreen(
    vm: HistorialViewModel,
    onDetalle: (Int) -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Pre-chequeos realizados",
            style = MaterialTheme.typography.titleLarge
        )

        Text(
            text = "Toca uno para revisarlo",
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
        )

        if (vm.registros.isEmpty()) {
            Text("Aún no hay pre-chequeos guardados")
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(vm.registros) { preChequeo ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onDetalle(preChequeo.id) }
                ) {
                    TarjetaPreChequeo(preChequeo)
                }
            }
        }
    }
}
