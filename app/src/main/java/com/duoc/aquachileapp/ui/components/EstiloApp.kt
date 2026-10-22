package com.duoc.aquachileapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.ui.graphics.vector.ImageVector
import com.duoc.aquachileapp.model.AppSection
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.duoc.aquachileapp.ui.theme.ChipClaro
import com.duoc.aquachileapp.ui.theme.Morado
import com.duoc.aquachileapp.ui.theme.TextoOscuro
import com.duoc.aquachileapp.ui.theme.TextoSuave

/** Pastilla tipo "My tasks / Project / Note" de la imagen. */
@Composable
fun PastillaTab(texto: String, seleccionada: Boolean, onClick: () -> Unit) {
    Text(
        text = texto,
        fontSize = 12.sp,
        color = if (seleccionada) Color.White else TextoOscuro,
        modifier = Modifier
            .clip(CircleShape)
            .background(if (seleccionada) Morado else ChipClaro)
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 8.dp)
    )
}

@Composable
fun colorChip() = FilterChipDefaults.filterChipColors(
    containerColor = ChipClaro,
    labelColor = TextoOscuro,
    selectedContainerColor = Morado,
    selectedLabelColor = Color.White
)

@Composable
fun colorItemNav() = NavigationBarItemDefaults.colors(
    selectedIconColor = Morado,
    selectedTextColor = Morado,
    indicatorColor = ChipClaro,
    unselectedIconColor = TextoSuave,
    unselectedTextColor = TextoSuave
)

@Composable
fun colorItemDrawer() = NavigationDrawerItemDefaults.colors(
    selectedContainerColor = ChipClaro,
    selectedTextColor = Morado,
    selectedIconColor = Morado,
    unselectedContainerColor = Color.Transparent,
    unselectedTextColor = TextoOscuro,
    unselectedIconColor = TextoSuave
)

fun iconoDe(seccion: AppSection): ImageVector = when (seccion) {
    AppSection.PRECHEQUEO -> Icons.Filled.Add
    AppSection.HISTORIAL -> Icons.Filled.History
    AppSection.APROBACIONES -> Icons.Filled.CheckCircle
    AppSection.RESUMEN -> Icons.Filled.BarChart
    AppSection.SESION -> Icons.AutoMirrored.Filled.ExitToApp
}
