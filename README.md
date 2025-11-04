# Stretchy

Stretchy - stretching and training planning app

## Theme system

This project now includes a theme system that supports two separate visual themes:

- Stretching (green) — used for stretching screens
- Training (orange) — used for training screens

See `THEME_SYSTEM_DOCUMENTATION.md` for full details (colors, usage, previews).

### Quick guide

- The theme is applied automatically in `BottomNavBar` based on the current navigation route.
- You can also wrap any UI with `StretchingTheme { ... }` or `TrainingTheme { ... }` to force a specific theme.
- To access theme colors programmatically, use `LocalDesignColors.current`.

Example usage:

```kotlin
// Force the stretching theme for a composable
StretchingTheme {
    // content
}

// Use the current design colors
val colors = LocalDesignColors.current
Box(modifier = Modifier.background(brush = Brush.verticalGradient(colors.backgroundGradient))) {
    // content
}
```

For more details, see `THEME_SYSTEM_DOCUMENTATION.md`.

