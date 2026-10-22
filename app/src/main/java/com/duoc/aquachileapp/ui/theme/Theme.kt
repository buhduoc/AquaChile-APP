package com.duoc.aquachileapp.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val EsquemaApp = lightColorScheme(
    primary = Morado,
    onPrimary = Color.White,
    primaryContainer = ChipClaro,
    onPrimaryContainer = TextoOscuro,
    secondary = Morado,
    secondaryContainer = ChipClaro,
    onSecondaryContainer = TextoOscuro,
    background = FondoApp,
    onBackground = TextoOscuro,
    surface = Color.White,
    onSurface = TextoOscuro,
    surfaceVariant = ChipClaro,
    onSurfaceVariant = TextoSuave,
    surfaceContainerHighest = Color.White,
    outline = Color(0xFFCBD0E8)
)

private val FormasApp = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp)
)

@Composable
fun AquaChileAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = EsquemaApp,
        typography = Typography,
        shapes = FormasApp,
        content = content
    )
}
