package com.duoc.aquachileapp.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable

fun PerfilScreen(onPerfil : () -> Unit){
    Column(modifier = Modifier.padding(24.dp).fillMaxSize().statusBarsPadding()) {
        Text("Perfil del usuario")
        Button(onPerfil) {Text("Regresar al inicio") }
    }
}