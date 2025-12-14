# Android Kotlin Project Guidelines

## Overview
This is an Android project using Kotlin, Jetpack Compose, Dagger DI, and MVVM architecture. The project follows a feature-based modular structure with clean architecture principles.

## Project Structure
- **Feature-based modules**: Each feature has its own `ui`, `domain`, `di` directories
- **Dagger Dependency Injection**: Uses component-based DI with scoped ViewModels
- **Navigation**: Centralized navigation using NavigationViewModel pattern
- **Repository Pattern**: Data layer abstraction with Room database

## ViewModels in This Project

### Architecture Pattern
- ViewModels are **scoped to Dagger components** (not standard ViewModelProvider)
- Each feature has its own DI component (e.g., `TrainingListComponent`, `ExecuteTrainingComponent`)
- ViewModels are created using `daggerViewModel()` extension function

### ViewModel Structure
```kotlin
// Standard pattern in this project:
class FeatureViewModel(
    private val useCase1: UseCase1,
    private val useCase2: UseCase2
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<UiState>(UiState.Initial)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
    
    private val _events = MutableSharedFlow<UiEvent>()
    val events: SharedFlow<UiEvent> = _events.asSharedFlow()
    
    sealed class UiEvent {
        data class ShowToast(val message: String) : UiEvent()
        // ... other events
    }
}
```

### ViewModel Creation Pattern
```kotlin
// In composable screens:
val component = FeatureComponent.create(activityComponent, parameters)
val vm = createFeatureViewModel(
    component,
    activityComponent.activity(),
    LocalViewModelStoreOwner.current!!
)
```

## Navigation Architecture

### Current Pattern (POST-REFACTOR)
- **DO NOT** pass `NavController` directly to composables
- **USE** `NavigationViewModel` for centralized navigation
- **USE** callback functions in composables that trigger navigation events

### Navigation Flow
```
UI Component → Callback → NavigationViewModel → NavEvent → HandleNavigationEvents → NavController
```

### Example Usage
```kotlin
// In composables - USE callbacks:
TrainingListScreenn(
    viewModel = vm,
    navigationViewModel = navigationViewModel,
    // callbacks for navigation
)

// NOT direct NavController usage:
// ❌ TrainingListScreenn(navController = navController, ...)
```

## Package Structure Rules

### File Location vs Package Declaration
- **Navigation files**: Located in `/navigation/` folder → package `com.example.stretchy.navigation`
- **UI files**: Located in `/ui/screen/` → package `com.example.stretchy.ui.screen`
- **Feature files**: Located in `/features/featurename/` → package `com.example.stretchy.features.featurename`

### Import Rules
- Always check package declarations match file locations
- Avoid circular imports between packages
- Use proper package imports, not file-location-based imports

## Compose UI Patterns

### State Management
- ViewModels expose `StateFlow<UiState>` for UI state
- ViewModels expose `SharedFlow<UiEvent>` for one-time events
- Composables collect state using `collectAsState()`

### Composable Structure
```kotlin
@Composable
fun FeatureScreen(
    viewModel: FeatureViewModel,
    navigationViewModel: NavigationViewModel,
    // other dependencies
) {
    val state = viewModel.uiState.collectAsState().value
    // UI implementation
    FeatureView(
        //this is main composable UI function -> here comes the UI Code
    )
}
```

## CRITICAL: Compilation Verification

### MANDATORY STEPS BEFORE COMPLETION
1. **Always run compilation check** before claiming work is complete
2. **Use**: `./gradlew compileDebugKotlin --no-daemon`
3. **Fix ALL compilation errors** - never leave unresolved references
4. **Verify imports** match package structure
5. **Check for circular dependencies**

### Common Compilation Issues
- Package declarations not matching file locations
- Missing imports for classes in different packages
- Circular import references
- Incorrect ViewModel creation patterns

## Dagger DI Patterns

### Component Structure
```kotlin
@FeatureScope
@Component(dependencies = [ActivityComponent::class])
interface FeatureComponent {
    companion object {
        fun create(
            activityComponent: ActivityComponent,
            parameter: Type
        ): FeatureComponent = DaggerFeatureComponent.builder()
            .activityComponent(activityComponent)
            .featureModule(FeatureModule(parameter))
            .build()
    }
}
```

### ViewModel Injection
- ViewModels are injected through Dagger components
- Use scoped components for proper lifecycle management
- Follow the existing `create*ViewModel()` pattern

## Testing Considerations
- ViewModels should be testable without Android dependencies
- Navigation logic should be testable via NavigationViewModel
- Composables should be testable without NavController dependencies

## Code Style
- Use sealed classes for UI states and events
- Prefer StateFlow over LiveData
- Use meaningful variable names (avoid abbreviations)
- Follow existing naming conventions in the project
