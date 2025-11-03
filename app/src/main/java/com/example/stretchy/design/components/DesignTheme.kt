package com.example.stretchy.design.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import com.example.stretchy.theme.Caramel
import com.example.stretchy.theme.GoBananas
import com.example.stretchy.theme.BananaMania

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
            accentStart = Caramel.copy(alpha = 0.95f),
            accentEnd = GoBananas,
            cardSurface = Color.White,
            metricIcon = Caramel.copy(alpha = 0.9f),
            metricTextPrimary = Color(0xFF222222),
            metricTextSecondary = Color(0xFF666666)
        )

        fun dark() = DesignColors(
            accentStart = Caramel,
            accentEnd = GoBananas.copy(alpha = 0.85f),
            cardSurface = Color(0xFF1F1F1F),
            metricIcon = GoBananas,
            metricTextPrimary = Color(0xFFEAEAEA),
            metricTextSecondary = Color(0xFFB5B5B5)
        )
    }
}

val LocalDesignColors = staticCompositionLocalOf { DesignColors.light() }

@Composable
fun DesignTheme(darkTheme: Boolean = false, content: @Composable () -> Unit) {
    val colors = if (darkTheme) DesignColors.dark() else DesignColors.light()
    val scheme = if (darkTheme) darkColorScheme() else lightColorScheme()

    CompositionLocalProvider(LocalDesignColors provides colors) {
        MaterialTheme(colorScheme = scheme, typography = MaterialTheme.typography) {
            content()
        }
    }
}

