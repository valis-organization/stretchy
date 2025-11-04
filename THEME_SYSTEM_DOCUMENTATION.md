# Stretchy App - Theme System Documentation

## Przegląd

Aplikacja Stretchy teraz obsługuje dwa oddzielne motywy kolorystyczne:
- **Zielony motyw** - dla sekcji stretching (rozciąganie)
- **Pomarańczowy motyw** - dla sekcji training (trening)

## Nowe kolory

### Stretching Theme (Zielony)
- **Primary**: `#4CAF50` (Green primary)
- **Secondary**: `#81C784` (Light green)
- **Background Dark**: `#1B5E20` (Very dark green dla gradientów)
- **Bottom Bar**: `#388E3C` (Medium green)
- **Floating Button**: `#4CAF50`

### Training Theme (Pomarańczowy)
- **Primary**: `#FF9800` (Orange primary)
- **Secondary**: `#FFB74D` (Light orange)
- **Background Dark**: `#BF360C` (Very dark orange/red dla gradientów)
- **Bottom Bar**: `#E65100` (Medium orange)
- **Floating Button**: `#FF9800`

## Użycie

### Automatyczne przełączanie motywów
Motywy są automatycznie przełączane w `BottomNavBar` na podstawie aktualnej trasy:
```kotlin
when (currentRoute) {
    Screen.StretchingListScreen.route -> StretchingTheme { content() }
    Screen.TrainingListScreen.route -> TrainingTheme { content() }
    Screen.MetaTrainingScreen.route -> StretchingTheme { content() }
    else -> StretchingTheme { content() }
}
```

### Manualne użycie motywów
```kotlin
// Dla stretching
StretchingTheme {
    // Twoja zawartość UI
}

// Dla training
TrainingTheme {
    // Twoja zawartość UI
}
```

### Dostęp do kolorów
```kotlin
@Composable
fun MyComponent() {
    val colors = LocalDesignColors.current
    
    Box(
        modifier = Modifier.background(
            brush = Brush.verticalGradient(colors.backgroundGradient)
        )
    ) {
        // Użyj colors.accentStart, colors.bottomBarBackground itp.
    }
}
```

## Komponenty zaktualizowane

1. **DesignTheme.kt** - Nowe definicje kolorów i motywów
2. **BottomNavBar.kt** - Automatyczne przełączanie motywów
3. **TrainingListComposable.kt** - Używa odpowiedniego motywu na podstawie typu treningu
4. **ThemePreview.kt** - Preview komponent do testowania motywów

## Kolory tła gradientowego

Dodano bardzo ciemne kolory tła (`backgroundDark`) aby gradienty były bardziej widoczne:
- Zielony: `#1B5E20` (light), `#0D2818` (dark)
- Pomarańczowy: `#BF360C` (light), `#3E1A00` (dark)

Te kolory są dostępne poprzez `colors.backgroundGradient` jako lista kolorów do użycia z `Brush.verticalGradient()`.

## Bottom Bar i Floating Action Button

- **Bottom Bar**: Ma teraz białe tło z kolorowymi ikonami zgodnie z motywem (zielone dla stretching, pomarańczowe dla training)
- **Floating Action Button**: Używa kolorów motywu poprzez `colors.floatingButtonBackground`

## Status Bar (Pasek statusu)

Status bar jest teraz przezroczysty i pokazuje kolor tła aktywnego motywu, tworząc spójny wygląd z resztą aplikacji.

## UI Structure Changes

- **TopAppBar usunięty**: Zastąpiony zwykłym tekstem tytułu w górnej części ekranu
- **Menu Import/Export**: Teraz znajduje się jako ikona obok tytułu (nie w TopAppBar)
- **Bottom Bar**: Dodano `navigationBarsPadding()` aby nie chodził pod system UI

## Karty (Cards)

- **Normalne karty**: Mają białe tło dla lepszej czytelności
- **Draft karty**: Mają jasnoróżowe tło (`#FFEBEE`) aby wyróżnić się jako nieukończone
- **Kolorowa linia**: Lewa strona karty ma kolorową linię zgodną z motywem lub czerwoną dla drafts
