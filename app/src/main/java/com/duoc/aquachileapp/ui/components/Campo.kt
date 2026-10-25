package com.duoc.aquachileapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.duoc.aquachileapp.ui.theme.ChipClaro
import com.duoc.aquachileapp.ui.theme.Morado
import com.duoc.aquachileapp.ui.theme.TextoOscuro

val CENTROS_OPERACION = listOf(
    "Centro Puerto Montt",
    "Centro Osorno",
    "Centro Calbuco"
)

@Composable
fun Campo(
    valor: String,
    alCambiar: (String) -> Unit,
    etiqueta: String,
    modifier: Modifier = Modifier.fillMaxWidth(),
    numero: Boolean = false,
    sobreMorado: Boolean = false
) {
    OutlinedTextField(
        value = valor,
        onValueChange = alCambiar,
        label = { Text(etiqueta) },
        singleLine = numero,
        shape = RoundedCornerShape(14.dp),
        colors = if (sobreMorado) {
            OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedLabelColor = Color.White,
                unfocusedLabelColor = Color.White.copy(alpha = 0.75f),
                focusedBorderColor = Color.White,
                unfocusedBorderColor = Color.White.copy(alpha = 0.35f),
                cursorColor = Color.White
            )
        } else {
            OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextoOscuro,
                unfocusedTextColor = TextoOscuro,
                focusedBorderColor = Morado,
                unfocusedBorderColor = ChipClaro,
                focusedLabelColor = Morado
            )
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = if (numero) KeyboardType.Number else KeyboardType.Text
        ),
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampoDesplegableCentro(
    valor: String,
    alSeleccionar: (String) -> Unit,
    modifier: Modifier = Modifier.fillMaxWidth(),
    opciones: List<String> = CENTROS_OPERACION,
    sobreMorado: Boolean = false
) {
    var expandido by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expandido,
        onExpandedChange = { expandido = !expandido },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = valor,
            onValueChange = {},
            readOnly = true,
            label = { Text("Centro de operación") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandido) },
            shape = RoundedCornerShape(14.dp),
            colors = if (sobreMorado) {
                OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.White.copy(alpha = 0.75f),
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.35f),
                    focusedTrailingIconColor = Color.White,
                    unfocusedTrailingIconColor = Color.White.copy(alpha = 0.75f),
                    cursorColor = Color.White
                )
            } else {
                OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextoOscuro,
                    unfocusedTextColor = TextoOscuro,
                    focusedBorderColor = Morado,
                    unfocusedBorderColor = ChipClaro,
                    focusedLabelColor = Morado
                )
            },
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, enabled = true)
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expandido,
            onDismissRequest = { expandido = false },
            modifier = Modifier.background(Color.White)
        ) {
            opciones.forEach { opcion ->
                DropdownMenuItem(
                    text = { Text(opcion, color = TextoOscuro) },
                    onClick = {
                        alSeleccionar(opcion)
                        expandido = false
                    }
                )
            }
        }
    }
}
