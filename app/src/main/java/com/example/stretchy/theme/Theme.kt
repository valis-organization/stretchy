package com.example.stretchy.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp

private val DarkColorPalette = darkColors(
    primary = Purple200,
    primaryVariant = Purple700,
    secondary = Teal200
)

private val LightColorPalette = lightColors(
    primary = Purple500,
    primaryVariant = Purple700,
    secondary = Teal200

)

@Composable
fun StretchyTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}

@Preview(name = "Light Theme", showBackground = true)
@Composable
private fun StretchyLightThemePreview() {
    StretchyTheme(darkTheme = false) {
        Column(modifier = androidx.compose.ui.Modifier.padding(16.dp)) {
            Text("Primary Text", color = MaterialTheme.colors.primary)
            Text("Secondary Text", color = MaterialTheme.colors.secondary)
            Text("On Background Text", color = MaterialTheme.colors.onBackground)
        }
    }
}

@Preview(name = "Dark Theme", showBackground = true)
@Composable
private fun StretchyDarkThemePreview() {
    StretchyTheme(darkTheme = true) {
        Column(modifier = androidx.compose.ui.Modifier.padding(16.dp)) {
            Text("Primary Text", color = MaterialTheme.colors.primary)
            Text("Secondary Text", color = MaterialTheme.colors.secondary)
            Text("On Background Text", color = MaterialTheme.colors.onBackground)
        }
    }
}
