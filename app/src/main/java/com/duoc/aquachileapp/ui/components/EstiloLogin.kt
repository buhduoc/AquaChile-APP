package com.duoc.aquachileapp.ui.components

import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val AzulLogin = Color(0xFF2A3B72)
val RosaError = Color(0xFFFF7A9C)

val FondoLogin = Brush.verticalGradient(
    colors = listOf(
        Color(0xFF1E6A8F),
        AzulLogin,
        Color(0xFF3B1F5C)
    )
)

@Composable
fun coloresCampoLogin() = TextFieldDefaults.colors(
    focusedContainerColor = Color.Transparent,
    unfocusedContainerColor = Color.Transparent,
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White,
    focusedLabelColor = Color.White,
    unfocusedLabelColor = Color.White,
    focusedIndicatorColor = Color.White,
    unfocusedIndicatorColor = Color.White,
    cursorColor = Color.White
)