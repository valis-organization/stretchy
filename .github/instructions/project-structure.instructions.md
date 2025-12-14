# Project Structure Reference

## Directory Layout
```
app/src/main/java/com/example/stretchy/
├── activity/                           # Main activity and DI
├── app/                               # Application class and DI
├── common/                            # Shared utilities
├── database/                          # Room database, DAOs, entities
├── extensions/                        # Kotlin extensions
├── features/                          # Feature modules
│   ├── createtraining/               # Create/edit training feature
│   │   ├── domain/                   # Domain logic, mappers
│   │   ├── ui/                       # ViewModels, composables
│   │   └── di/                       # Dagger components/modules
│   ├── executetraining/              # Training execution feature
│   ├── traininglist/                 # Training list feature
│   └── datatransport/                # Import/export feature
├── navigation/                        # Navigation logic
│   ├── NavigationViewModel.kt        # Centralized navigation
│   ├── NavigationHandler.kt          # Event handler
│   ├── Navigation.kt                 # NavHost setup
│   └── BottomNavScreen.kt           # Bottom navigation
├── permission/                        # Permission handling
├── repository/                        # Data layer
├── theme/                            # Compose themes
└── ui/                               # UI components
    ├── navigation/                   # UI navigation components
    └── screen/                       # Screen-level composables
```

## Package Naming Rules

### File Location → Package Declaration
- `/navigation/Navigation.kt` → `package com.example.stretchy.navigation`
- `/ui/screen/TrainingScreen.kt` → `package com.example.stretchy.ui.screen`  
- `/features/traininglist/ui/TrainingListViewModel.kt` → `package com.example.stretchy.features.traininglist.ui`
- `/features/traininglist/di/TrainingListComponent.kt` → `package com.example.stretchy.features.traininglist.di`

### Import Rules
```kotlin
// From any file, importing Navigation:
import com.example.stretchy.navigation.Navigation

// From any file, importing Screen:
import com.example.stretchy.Screen

// From same package, no import needed:
// Files in /navigation/ can use NavigationViewModel without import
```

## Feature Module Structure

### Each feature follows this pattern:
```
features/featurename/
├── domain/                    # Business logic, use cases, mappers
│   ├── FeatureMappers.kt     # Data transformations
│   └── FeatureUseCases.kt    # Business logic
├── ui/                       # UI layer
│   ├── FeatureViewModel.kt   # State management
│   ├── composable/           # Composables
│   │   └── FeatureScreenn.kt # Screen composable (note: ends with 'n')
│   └── data/                 # UI data classes
│       ├── FeatureUiState.kt # UI state definitions
│       └── FeatureUiData.kt  # UI data models
└── di/                       # Dependency injection
    ├── FeatureComponent.kt   # Dagger component
    ├── FeatureModule.kt      # Dagger module
    └── FeatureScope.kt       # Custom scope
```

## Naming Conventions

### Composables
- **Screen-level**: `FeatureScreenn` (ends with 'n')
- **View-level**: `FeatureVieww` (ends with 'w')  
- **Component-level**: `FeatureComponent`

### ViewModels
- Pattern: `FeatureViewModel`
- Location: `/features/featurename/ui/`
- Package: `com.example.stretchy.features.featurename.ui`

### DI Components
- Pattern: `FeatureComponent`
- Location: `/features/featurename/di/`
- Package: `com.example.stretchy.features.featurename.di`

### Use Cases
- Pattern: `ActionSubjectUseCase` (e.g., `FetchTrainingListUseCase`)
- Location: `/features/domain/usecases/` or feature-specific domain folder

## Dependencies & Imports

### Common Imports by File Type
```kotlin
// ViewModels:
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharedFlow

// Composables:
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier

// Navigation (POST-REFACTOR):
import com.example.stretchy.navigation.NavigationViewModel
// NOT: import androidx.navigation.NavController

// DI Components:
import dagger.Component
import dagger.Module
import dagger.Provides
```

## File Templates

### ViewModel Template
```kotlin
package com.example.stretchy.features.featurename.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*

class FeatureViewModel(
    private val useCase: UseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<FeatureUiState>(FeatureUiState.Initial)
    val uiState: StateFlow<FeatureUiState> = _uiState.asStateFlow()
    
    private val _events = MutableSharedFlow<UiEvent>()
    val events: SharedFlow<UiEvent> = _events.asSharedFlow()
    
    sealed class UiEvent {
        data class ShowToast(val message: String) : UiEvent()
    }
}
```

### Screen Composable Template
```kotlin
package com.example.stretchy.features.featurename.ui.composable

import androidx.compose.runtime.*
import com.example.stretchy.navigation.NavigationViewModel

@Composable
fun FeatureScreenn(
    viewModel: FeatureViewModel,
    navigationViewModel: NavigationViewModel
) {
    val state = viewModel.uiState.collectAsState().value
    
    FeatureVieww(
        state = state,
        onAction = { /* handle action */ }
    )
}
```

This structure reference should help maintain consistency across the codebase.
