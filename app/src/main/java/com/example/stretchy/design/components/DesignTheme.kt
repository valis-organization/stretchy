package com.example.stretchy.design.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

class DesignColors(
    val accentStart: Color,
    val accentEnd: Color,
    val cardSurface: Color,
    val metricIcon: Color,
    val metricTextPrimary: Color,
    val metricTextSecondary: Color
) {
    val accentGradient: List<Color> get() = listOf(accentStart, accentEnd)

    companion object {
        fun light() = DesignColors(
            accentStart = Color(0xFFFF8A65), // Orange similar to the image
            accentEnd = Color(0xFF42A5F5), // Blue similar to the image
            cardSurface = Color.White,
            metricIcon = Color(0xFF9E9E9E),
            metricTextPrimary = Color(0xFF222222),
            metricTextSecondary = Color(0xFF666666)
        )

        fun dark() = DesignColors(
            accentStart = Color(0xFFFF8A65),
            accentEnd = Color(0xFF42A5F5),
            cardSurface = Color(0xFF1F1F1F),
            metricIcon = Color(0xFF9E9E9E),
            metricTextPrimary = Color(0xFFEAEAEA),
            metricTextSecondary = Color(0xFFB5B5B5)
        )
    }
}

val LocalDesignColors = staticCompositionLocalOf { DesignColors.light() }

@Composable
fun DesignTheme(darkTheme: Boolean = false, content: @Composable () -> Unit) {
    val colors = if (darkTheme) DesignColors.dark() else DesignColors.light()
    val scheme = if (darkTheme) darkColorScheme(
        primary = colors.accentStart,
        secondary = colors.accentEnd
    ) else lightColorScheme(
        primary = colors.accentStart,
        secondary = colors.accentEnd
    )

    CompositionLocalProvider(LocalDesignColors provides colors) {
        MaterialTheme(colorScheme = scheme, typography = MaterialTheme.typography) {
            content()
        }
    }
}
