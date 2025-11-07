# ViewModel Guidelines for GitHub Copilot - Post-Hilt Migration

**Status**: ✅ Successfully migrated from Dagger to Hilt (Dagger 2.57.2)

## Current Architecture Overview

### ✅ Successfully Migrated Components

1. **Dependency Injection**: Fully migrated from Dagger to Hilt
   - Single `ApplicationModule` replaces all Dagger modules
   - `@HiltAndroidApp` on Application class
   - `@AndroidEntryPoint` on MainActivity
   - All ViewModels use Hilt with assisted injection

2. **ViewModels Structure**: Modern Hilt-based architecture
   ```kotlin
   @HiltViewModel(assistedFactory = TrainingListViewModel.Factory::class)
   class TrainingListViewModel @AssistedInject constructor(
       repository: Repository,
       private val dataImporterImpl: DataImporterImpl,
       private val dataExporterImpl: DataExporterImpl,
       @Assisted private val trainingType: TrainingType
   ) : ViewModel() {
       
       @AssistedFactory
       interface Factory {
           fun create(trainingType: TrainingType): TrainingListViewModel
       }
   }
   ```

3. **Navigation**: Clean centralized architecture
   - `NavigationViewModel` for centralized navigation events
   - `NavigationHandler` for event processing
   - ViewModels are decoupled from navigation logic

### ✅ Critical Issue Resolved

**Kotlin 2.1.0 + Compose Compiler Plugin**: 
- ✅ Updated to Kotlin 2.1.0 in build.gradle
- ✅ Added required `org.jetbrains.kotlin.plugin.compose` plugin for Kotlin 2.0+
- ✅ Added Compose Compiler plugin to both project and app level build.gradle
- ✅ Build configuration now compatible with Kotlin 2.1.0

### 🔧 Remaining Architecture Issues (Minor Cleanup)

1. **Complete Navigation Refactor** (Medium Priority)
   - `CreateTrainingScreenn` still uses `NavController` directly
   - `ExecuteTrainingScreenn` still uses `NavController` directly
   - Need to complete migration to `NavigationViewModel` pattern

2. **SavedStateHandle Pattern** ✅ (Final Solution)
   ```kotlin
   // ViewModels now use SavedStateHandle for runtime parameters
   @HiltViewModel
   class TrainingListViewModel @Inject constructor(
       repository: Repository,
       private val savedStateHandle: SavedStateHandle
   ) : ViewModel() {
       
       private var trainingType: TrainingType = savedStateHandle.get<TrainingType>("trainingType") ?: TrainingType.STRETCH
       
       fun setTrainingType(type: TrainingType) {
           if (trainingType != type) {
               trainingType = type
               savedStateHandle["trainingType"] = type
               fetchTrainings()
           }
       }
   }
   
   // Usage in Navigation.kt
   val vm: TrainingListViewModel = hiltViewModel()
   vm.setTrainingType(TrainingType.TRAINING)
   ```

## Guidelines for Working with This Architecture

### ✅ DO - Modern Hilt Best Practices

1. **Use Hilt ViewModels with Assisted Injection**
   ```kotlin
   @HiltViewModel(assistedFactory = YourViewModel.Factory::class)
   class YourViewModel @AssistedInject constructor(
       dependency: SomeDependency,
       @Assisted runtimeParam: RuntimeParam
   ) : ViewModel() {
       
       @AssistedFactory
       interface Factory {
           fun create(runtimeParam: RuntimeParam): YourViewModel
       }
   }
   ```

2. **Use SavedStateHandle for Runtime Parameters**
   ```kotlin
   // In ViewModel
   @HiltViewModel
   class MyViewModel @Inject constructor(
       dependency: SomeDependency,
       private val savedStateHandle: SavedStateHandle
   ) : ViewModel() {
       
       private val runtimeParam = savedStateHandle.get<String>("param") ?: "default"
       
       fun setRuntimeParam(param: String) {
           savedStateHandle["param"] = param
           // React to parameter change
       }
   }
   
   // In Navigation.kt
   val vm: MyViewModel = hiltViewModel()
   vm.setRuntimeParam(actualParam)
   ```

3. **Inject Dependencies via Hilt**
   ```kotlin
   @Module
   @InstallIn(SingletonComponent::class)
   object ApplicationModule {
       @Provides
       @Singleton
       fun provideDependency(): Dependency = DependencyImpl()
   }
   ```

### ❌ DON'T - Avoid These Patterns

1. **Don't pass NavController to ViewModels**
   ```kotlin
   // BAD - tight coupling
   class BadViewModel(private val navController: NavController)
   
   // GOOD - use events
   class GoodViewModel {
       private val _navEvents = MutableSharedFlow<NavEvent>()
   }
   ```

2. **Don't use old Dagger patterns** (all removed)
   - No more `@Component` interfaces
   - No more complex DI component chains
   - No more manual provider factories

3. **Don't create ViewModels manually**
   ```kotlin
   // BAD - manual creation
   val vm = ViewModelProvider(this)[MyViewModel::class.java]
   
   // GOOD - Hilt assisted injection
   val factory = hiltViewModel<MyViewModel.Factory>()
   val vm = factory.create(runtimeParam)
   ```

### 🔄 Migration Steps for New Features

1. **For ViewModels with Runtime Parameters**
   ```kotlin
   @HiltViewModel
   class NewViewModel @Inject constructor(
       repository: Repository,
       private val savedStateHandle: SavedStateHandle
   ) : ViewModel() {
       
       private val param = savedStateHandle.get<RuntimeParam>("param") ?: defaultValue
       
       fun setParam(param: RuntimeParam) {
           savedStateHandle["param"] = param
           // Handle parameter change
       }
   }
   ```

2. **For Simple ViewModels (no runtime params)**
   ```kotlin
   @HiltViewModel
   class SimpleViewModel @Inject constructor(
       repository: Repository
   ) : ViewModel()
   ```

## Current VM and Compose Status

### ✅ Strengths
- Clean DI with Hilt
- Centralized navigation architecture
- ViewModels properly scoped and lifecycle-aware
- No tight coupling between VMs and UI navigation

### 🔧 Issues for UI Migration Readiness

1. **✅ FIXED** - Kotlin 2.1.0 + Compose Compiler plugin compatibility
2. **Medium Priority** - Complete navigation refactor
   - Remove NavController from CreateTrainingScreenn
   - Remove NavController from ExecuteTrainingScreenn
   - Add missing NavigationViewModel events

### 🎯 Ready for UI Framework Migration

After addressing the Kotlin version issue, this architecture is **well-positioned** for migrating to a new UI framework:

- ✅ ViewModels are UI-agnostic
- ✅ Clean separation of concerns
- ✅ Modern DI with Hilt
- ✅ Centralized navigation that can be adapted
- ✅ No tight coupling to Compose-specific APIs in VMs

The remaining issues are **minimal cleanup** rather than architectural blockers.

## Quick Reference Commands

```kotlin
// Inject ViewModel with runtime params using SavedStateHandle
val vm: MyViewModel = hiltViewModel()
vm.setRuntimeParam(runtimeParam)

// Inject simple ViewModel
val vm: SimpleViewModel = hiltViewModel()

// Navigation from Composable
navigationViewModel.navigateToScreen(param)
```

**Next Steps**: Fix Kotlin version compatibility, then complete navigation refactor for full UI migration readiness.
