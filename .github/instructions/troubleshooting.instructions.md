# Troubleshooting Guide

applyTo: "**/*.kt"

## Common Compilation Errors & Solutions

### "Unresolved reference: NavigationViewModel"
**Cause**: Missing import or wrong package
**Solution**:
```kotlin
// Add to imports:
import com.example.stretchy.navigation.NavigationViewModel
```

### "Unresolved reference: HandleNavigationEvents"  
**Cause**: Missing import or wrong package
**Solution**:
```kotlin
// If in same package (navigation), no import needed
// If in different package:
import com.example.stretchy.navigation.HandleNavigationEvents
```

### "Not enough information to infer type variable VM"
**Cause**: Incorrect ViewModel creation pattern
**Solution**:
```kotlin
// Use project's Dagger pattern:
val vm = createFeatureViewModel(
    component,
    activityComponent.activity(), 
    LocalViewModelStoreOwner.current!!
)
```

### "Package declaration doesn't match directory"
**Cause**: File moved but package not updated
**Solution**:
```kotlin
// File in /navigation/ folder:
package com.example.stretchy.navigation

// File in /ui/screen/ folder:
package com.example.stretchy.ui.screen
```

### "Circular dependency between packages"
**Cause**: Wrong import structure
**Solution**:
- Check if files are importing from correct packages
- Avoid importing parent package from child package
- Use proper dependency direction

## Architecture-Specific Issues

### ViewModel Not Working
**Check**:
1. Is it created with Dagger component?
2. Is the component properly scoped?
3. Are dependencies injected correctly?
4. Is it using the project's `create*ViewModel()` pattern?

### Navigation Not Working
**Check**:
1. Are you using NavigationViewModel instead of NavController?
2. Are callbacks properly connected?
3. Is HandleNavigationEvents set up correctly?
4. Are navigation events being emitted?

### Composable Compilation Issues
**Check**:
1. Are function signatures updated in all usage places?
2. Are preview functions updated?
3. Are imports correct for all referenced types?
4. Is package declaration correct?

## Quick Diagnostic Commands

### Check Compilation
```bash
./gradlew compileDebugKotlin --no-daemon
```

### Check Dependencies
```bash
./gradlew dependencies
```

### Clean Build
```bash
./gradlew clean && ./gradlew compileDebugKotlin
```

## Rollback Procedures

### If Changes Break Build:
1. Identify which files were changed
2. Check git diff to see specific changes
3. Revert problematic changes:
   ```bash
   git checkout -- filename.kt
   ```
4. Apply changes incrementally and test each step

### If Architecture Is Wrong:
1. Look at similar existing code in the project
2. Copy the established pattern
3. Don't invent new patterns - follow existing ones
4. Update documentation if pattern changes

## Prevention Strategies

### Before Making Changes:
1. Understand current architecture
2. Find similar existing code to copy pattern
3. Plan changes to follow established patterns
4. Make incremental changes, not big rewrites

### After Making Changes:
1. **ALWAYS** compile before claiming done
2. Test basic functionality works
3. Check that existing features still work
4. Verify imports and packages are correct

## Emergency Contacts/Resources

### Key Files to Reference:
- `/navigation/NavigationViewModel.kt` - For navigation patterns
- `/features/*/ui/*ViewModel.kt` - For ViewModel patterns  
- `/features/*/di/*Component.kt` - For DI patterns
- `/ui/screen/*.kt` - For screen composable patterns

### Architecture Examples:
- Look at TrainingListViewModel for ViewModel pattern
- Look at TrainingListScreenn for screen composable pattern
- Look at Navigation.kt for navigation setup
- Look at any Component.kt for DI setup

## Success Indicators
- [ ] `./gradlew compileDebugKotlin` runs without errors
- [ ] No "Unresolved reference" errors in IDE
- [ ] All imports are properly resolved
- [ ] Package declarations match file locations
- [ ] Code follows existing project patterns
- [ ] No breaking changes to existing functionality
