package com.duoc.aquachileapp.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun Fotos(fotos: List<String>) {
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        fotos.forEach { foto ->
            AsyncImage(
                model = foto,
                contentDescription = "Evidencia",
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(96.dp)
            )
        }
    }
}