package com.duoc.aquachileapp.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.duoc.aquachileapp.ui.theme.ChipClaro
import com.duoc.aquachileapp.ui.theme.Morado
import com.duoc.aquachileapp.ui.theme.TextoOscuro

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
