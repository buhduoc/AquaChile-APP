package com.duoc.aquachileapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun HomeScreen(
    navController: NavHostController
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),

        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = "Bienvenido a AquaCheck Buceo",
            style = MaterialTheme.typography.headlineSmall
        )

        Text(
            text = "Sistema de pre-chequeo de seguridad para operaciones de buceo."
        )

        Button(
            onClick = {
                navController.navigate("prechequeo")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Nuevo Pre-chequeo")
        }
    }
}