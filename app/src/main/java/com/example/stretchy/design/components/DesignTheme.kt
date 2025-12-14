package com.example.stretchy.design.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

enum class ThemeType {
    STRETCHING,
    TRAINING
}

class DesignColors(
    val accentStart: Color,
    val accentEnd: Color,
    val cardSurface: Color,
    val metricIcon: Color,
    val metricTextPrimary: Color,
    val metricTextSecondary: Color,
    val backgroundDark: Color, // Very dark color for gradient backgrounds
    val bottomBarBackground: Color,
    val floatingButtonBackground: Color
) {
    val accentGradient: List<Color> get() = listOf(accentStart, accentEnd)
    val backgroundGradient: List<Color> get() = listOf(backgroundDark, accentStart.copy(alpha = 0.3f))

    companion object {
        // Stretching Theme - Green colors
        fun stretchingLight() = DesignColors(
            accentStart = Color(0xFF4CAF50), // Green primary
            accentEnd = Color(0xFF81C784), // Light green
            cardSurface = Color.White,
            metricIcon = Color(0xFF9E9E9E),
            metricTextPrimary = Color(0xFF222222),
            metricTextSecondary = Color(0xFF666666),
            backgroundDark = Color(0xFF1B5E20), // Very dark green
            bottomBarBackground = Color(0xFF388E3C), // Medium green for bottom bar
            floatingButtonBackground = Color(0xFF4CAF50)
        )

        fun stretchingDark() = DesignColors(
            accentStart = Color(0xFF66BB6A),
            accentEnd = Color(0xFF81C784),
            cardSurface = Color(0xFF1F1F1F),
            metricIcon = Color(0xFF9E9E9E),
            metricTextPrimary = Color(0xFFEAEAEA),
            metricTextSecondary = Color(0xFFB5B5B5),
            backgroundDark = Color(0xFF0D2818), // Very dark green
            bottomBarBackground = Color(0xFF2E7D32),
            floatingButtonBackground = Color(0xFF66BB6A)
        )

        // Training Theme - Orange colors
        fun trainingLight() = DesignColors(
            accentStart = Color(0xFFFF9800), // Orange primary
            accentEnd = Color(0xFFFFB74D), // Light orange
            cardSurface = Color.White,
            metricIcon = Color(0xFF9E9E9E),
            metricTextPrimary = Color(0xFF222222),
            metricTextSecondary = Color(0xFF666666),
            backgroundDark = Color(0xFFBF360C), // Very dark orange/red
            bottomBarBackground = Color(0xFFE65100), // Medium orange for bottom bar
            floatingButtonBackground = Color(0xFFFF9800)
        )

        fun trainingDark() = DesignColors(
            accentStart = Color(0xFFFFB74D),
            accentEnd = Color(0xFFFFCC02),
            cardSurface = Color(0xFF1F1F1F),
            metricIcon = Color(0xFF9E9E9E),
            metricTextPrimary = Color(0xFFEAEAEA),
            metricTextSecondary = Color(0xFFB5B5B5),
            backgroundDark = Color(0xFF3E1A00), // Very dark orange
            bottomBarBackground = Color(0xFFD84315),
            floatingButtonBackground = Color(0xFFFFB74D)
        )

        // Legacy methods for backward compatibility
        @Deprecated("Use stretchingLight() instead")
        fun light() = stretchingLight()

        @Deprecated("Use stretchingDark() instead")
        fun dark() = stretchingDark()
    }
}

val LocalDesignColors = staticCompositionLocalOf { DesignColors.stretchingLight() }

@Composable
fun DesignTheme(
    themeType: ThemeType = ThemeType.STRETCHING,
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colors = when (themeType) {
        ThemeType.STRETCHING -> if (darkTheme) DesignColors.stretchingDark() else DesignColors.stretchingLight()
        ThemeType.TRAINING -> if (darkTheme) DesignColors.trainingDark() else DesignColors.trainingLight()
    }

    val scheme = if (darkTheme) darkColorScheme(
        primary = colors.accentStart,
        primaryContainer = colors.accentStart.copy(alpha = 0.18f),
        secondary = colors.accentEnd,
        surface = colors.cardSurface,
        background = colors.backgroundDark,
        onPrimary = Color.White,
        onPrimaryContainer = Color.White,
        onSecondary = Color.White,
        onSurface = colors.metricTextPrimary,
        onBackground = colors.metricTextPrimary
    ) else lightColorScheme(
        primary = colors.accentStart,
        primaryContainer = colors.accentStart.copy(alpha = 0.18f),
        secondary = colors.accentEnd,
        surface = colors.cardSurface,
        background = Color.White,
        onPrimary = Color.White,
        onPrimaryContainer = Color.White,
        onSecondary = Color.White,
        onSurface = colors.metricTextPrimary,
        onBackground = colors.metricTextPrimary
    )

    CompositionLocalProvider(LocalDesignColors provides colors) {
        MaterialTheme(colorScheme = scheme, typography = MaterialTheme.typography) {
            content()
        }
    }
}

// Convenience functions for specific themes
@Composable
fun StretchingTheme(darkTheme: Boolean = false, content: @Composable () -> Unit) {
    DesignTheme(themeType = ThemeType.STRETCHING, darkTheme = darkTheme, content = content)
}

@Composable
fun TrainingTheme(darkTheme: Boolean = false, content: @Composable () -> Unit) {
    DesignTheme(themeType = ThemeType.TRAINING, darkTheme = darkTheme, content = content)
}

// Legacy function for backward compatibility
@Composable
@Deprecated("Use StretchingTheme() or TrainingTheme() instead")
fun DesignTheme(darkTheme: Boolean = false, content: @Composable () -> Unit) {
    StretchingTheme(darkTheme = darkTheme, content = content)
}
