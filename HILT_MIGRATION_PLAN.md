# Step 3: Hilt/Dagger Migration Plan - For Later Implementation

## Problem
ViewModel currently has direct repository access through use case constructors, violating clean architecture principles.

## Solution
Remove repository parameter and inject use cases directly through Hilt/Dagger.

### Current ViewModel Constructor (PROBLEM):
```kotlin
@HiltViewModel
class CreateOrEditTrainingViewModel @Inject constructor(
    repository: Repository,  // ❌ Direct repository access
    private val automaticBreakPreferences: AutomaticBreakPreferences,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    // Use cases created manually with repository
    private val fetchTrainingByIdUseCase = FetchTrainingByIdUseCase(repository)
    private val createTrainingUseCase = CreateTrainingUseCase(repository)
    private val editTrainingUseCase = EditTrainingUseCase(repository)
    internal val breakManagementUseCase = BreakManagementUseCase(repository)
    private val fetchTrainingDomainUseCase = FetchTrainingDomainUseCase(repository)
    private val createTrainingDomainUseCase = CreateTrainingDomainUseCase(repository)
    private val editTrainingDomainUseCase = EditTrainingDomainUseCase(repository)
}
```

### Target ViewModel Constructor (SOLUTION):
```kotlin
@HiltViewModel 
class CreateOrEditTrainingViewModel @Inject constructor(
    private val fetchTrainingByIdUseCase: FetchTrainingByIdUseCase,
    private val createTrainingUseCase: CreateTrainingUseCase,
    private val editTrainingUseCase: EditTrainingUseCase,
    private val breakManagementUseCase: BreakManagementUseCase,
    private val fetchTrainingDomainUseCase: FetchTrainingDomainUseCase,
    private val createTrainingDomainUseCase: CreateTrainingDomainUseCase,
    private val editTrainingDomainUseCase: EditTrainingDomainUseCase,
    private val automaticBreakPreferences: AutomaticBreakPreferences,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    // ❌ DELETE these lines - use cases now injected directly:
    // private val fetchTrainingByIdUseCase = FetchTrainingByIdUseCase(repository)
    // private val createTrainingUseCase = CreateTrainingUseCase(repository)
    // etc.
}
```

### Required Hilt Module Changes:
Add to `ApplicationModule.kt` or create new `UseCaseModule.kt`:

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideFetchTrainingByIdUseCase(repository: Repository): FetchTrainingByIdUseCase =
        FetchTrainingByIdUseCase(repository)

    @Provides  
    @Singleton
    fun provideCreateTrainingUseCase(repository: Repository): CreateTrainingUseCase =
        CreateTrainingUseCase(repository)

    @Provides
    @Singleton  
    fun provideEditTrainingUseCase(repository: Repository): EditTrainingUseCase =
        EditTrainingUseCase(repository)

    @Provides
    @Singleton
    fun provideBreakManagementUseCase(repository: Repository): BreakManagementUseCase =
        BreakManagementUseCase(repository)

    @Provides
    @Singleton
    fun provideFetchTrainingDomainUseCase(repository: Repository): FetchTrainingDomainUseCase =
        FetchTrainingDomainUseCase(repository)

    @Provides
    @Singleton
    fun provideCreateTrainingDomainUseCase(repository: Repository): CreateTrainingDomainUseCase =
        CreateTrainingDomainUseCase(repository)

    @Provides
    @Singleton
    fun provideEditTrainingDomainUseCase(repository: Repository): EditTrainingDomainUseCase =
        EditTrainingDomainUseCase(repository)
}
```

### Benefits After Implementation:
- ✅ Complete dependency inversion achieved
- ✅ ViewModel has no direct repository access
- ✅ Use cases properly injected through DI
- ✅ Clean architecture principles fully implemented
- ✅ Easier testing (can mock individual use cases)

### When to Implement:
This should be done after Steps 1 and 2 are complete and working properly.
