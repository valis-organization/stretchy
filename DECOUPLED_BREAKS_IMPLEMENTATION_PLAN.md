# Plan Implementacji Decoupled Break System

## ✅ Faza 1: Database Layer (COMPLETED)
- [x] `BreakTemplateEntity` - reusable break templates (simplified: removed name, isSystemDefault)
- [x] `TrainingSequenceEntity` - decoupled exercise-break relationships  
- [x] `BreakTemplateDao` - break management operations
- [x] `TrainingSequenceDao` - sequence operations with JOIN queries

## ✅ Faza 2: Domain Layer (COMPLETED)  
- [x] `TrainingSequence` - clean domain model
- [x] `TrainingActivitySwitcher` - central switching logic with improved SwitchMode comments
- [x] `TrainingSequenceRepository` - intelligent break management

## ✅ Faza 3: ViewModel Integration (PREPARED FOR MIGRATION)
- [x] Added new system structure (commented out for migration)
- [x] Fixed compilation issues 
- [x] Prepared switching methods for activation after migration
- [x] Ready for database migration phase

## 📋 Faza 4: Migration Strategy (PLANNED)
1. **Dual System Phase**: Run both old and new systems in parallel
2. **Data Migration**: Convert existing training_activities to new structure
3. **UI Updates**: Update ExecuteTrainingScreen to use new methods
4. **Testing Phase**: Ensure all flows work correctly
5. **Cleanup**: Remove old complex merging logic

## 📋 Faza 5: UI Layer Updates (PLANNED)
- [ ] Update `ExecuteTrainingScreen` to use new navigation methods
- [ ] Simplify ViewState - remove complex merging structures
- [ ] Update break selection UI in edit screen
- [ ] Add break template picker with popular breaks

## ✅ Zmiany zgodnie z feedback:

### **Removed Fields from BreakTemplateEntity:**
- ❌ `name: String` - Usunięte (UI może generować nazwy dynamicznie)  
- ❌ `isSystemDefault: Boolean` - Usunięte (będzie UI trick podczas edycji)
- ✅ `usageCount: Int` - Zachowane (świetne do trackowania popularności)

### **SwitchMode Behavior Clarification:**
```kotlin
object AutomaticProgression // Timer ended, follow normal flow (show the break after that) 
object UserSkipToNext      // User clicked next exercise, skip the break entirely and start from the new exercise
```

### **Database Structure Simplified:**
```kotlin
data class BreakTemplateEntity(
    val breakId: String,
    val duration: Int,        // in seconds
    val usageCount: Int = 0   // tracks popularity
)
```

## 🎯 Benefits po implementacji:

### **Dla Developera:**
- ✅ **Czysta separacja**: Exercises ≠ Breaks w logice
- ✅ **Jednolita nawigacja**: Jedna metoda `switchToNextActivity()` dla wszystkiego
- ✅ **Łatwe testowanie**: Każda część jest niezależna
- ✅ **Skalowalność**: Łatwo dodać nowe typy (warm-up, cool-down)

### **Dla Użytkownika:**
- ✅ **Reużywalne przerwy**: 5s break użyta w 10 miejscach
- ✅ **Inteligentna edycja**: System wie kiedy tworzyć nową vs edytować
- ✅ **Szybki picker**: "Popularne: 5s (15x), 15s (8x), 30s (3x)"
- ✅ **Spójne zachowanie**: Auto-progression i manual navigation działają identycznie

### **Dla Bazy Danych:**
- ✅ **Mniej redundancji**: 1 rekord zamiast N dla tej samej przerwy
- ✅ **Analytics**: Śledzenie popularności przerw
- ✅ **Optymalizacja**: Cleanup nieużywanych breaks

## 🚀 Następne kroki:
1. Zaimplementować migration script
2. Zaktualizować AppDatabase.kt z nowymi encjami
3. Przetestować basic flows
4. Stopniowo przenosić logic z starego systemu

Czy chcesz kontynuować z którąś z kolejnych faz?
