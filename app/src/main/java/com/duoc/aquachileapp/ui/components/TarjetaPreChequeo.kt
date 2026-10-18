package com.duoc.aquachileapp.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.duoc.aquachileapp.model.PreChequeo
import com.duoc.aquachileapp.model.Resultado

fun colorDe(resultado: Resultado): Color {
    return when (resultado) {
        Resultado.CUMPLE -> Color(0xFF00FF0E)
        Resultado.OBSERVADO -> Color(0xFFFFDE00)
        Resultado.REQUIERE_REVISION -> Color(0xFFFF0000)
    }
}

@Composable
fun EtiquetaResultado(resultado: Resultado) {
    Surface(
        color = colorDe(resultado),
        shape = RoundedCornerShape(50)
    ) {
        Text(
            text = resultado.texto,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun TarjetaPreChequeo(preChequeo: PreChequeo) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "${preChequeo.centro} · ${preChequeo.buzo}",
                style = MaterialTheme.typography.titleMedium
            )
            Text(preChequeo.fecha)
            Text("Supervisor: ${preChequeo.supervisor}")
            EtiquetaResultado(preChequeo.resultado)
        }
    }
}