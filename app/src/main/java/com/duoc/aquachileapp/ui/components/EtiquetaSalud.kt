package com.duoc.aquachileapp.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.duoc.aquachileapp.model.NivelSalud
import com.duoc.aquachileapp.model.Resultado

@Composable
fun EtiquetaSalud(nivel: NivelSalud) {

    val resultado = when (nivel) {
        NivelSalud.NORMAL -> Resultado.CUMPLE
        NivelSalud.ATENCION -> Resultado.OBSERVADO
        NivelSalud.CRITICO -> Resultado.REQUIERE_REVISION
    }

    Surface(
        color = colorDe(resultado),
        shape = RoundedCornerShape(50)
    ) {
        Text(
            text = "Salud: ${nivel.texto}",
            color = Color.White,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
        )
    }
}