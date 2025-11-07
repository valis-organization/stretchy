# Copilot Instructions for Stretchy Android Project

This directory contains comprehensive guidelines for working with the Stretchy Android project. These instructions help ensure consistency, prevent compilation errors, and maintain architectural integrity.

## Instruction Files

### 📱 [android-kotlin.instructions.md](./android-kotlin.instructions.md)
**Applies to:** `**/*.kt`  
**Purpose:** General Android Kotlin project guidelines covering:
- Project structure and architecture overview
- ViewModel patterns and Dagger DI usage
- Navigation architecture (post-refactor)
- Package structure rules
- **CRITICAL: Mandatory compilation verification steps**

### 🧭 [compose-navigation.instructions.md](./compose-navigation.instructions.md)  
**Applies to:** `**/ui/**/*.kt`, `**/navigation/**/*.kt`, `**/composable/**/*.kt`  
**Purpose:** Specific Compose UI and navigation patterns:
- Navigation refactoring rules (what NOT to do vs what TO do)
- Composable architecture patterns
- State management in Compose
- Navigation callback patterns
- Preview function updates

### 🔄 [project-workflow.instructions.md](./project-workflow.instructions.md)
**Applies to:** `**/*.kt`  
**Purpose:** Mandatory workflow for ALL code changes:
- 4-phase workflow (Analysis → Implementation → Verification → Completion)
- Architecture decision rules
- **CRITICAL: Error prevention checklist**
- Quality standards and success metrics

### 🏗️ [project-structure.instructions.md](./project-structure.instructions.md)
**Applies to:** `**/*.kt`  
**Purpose:** Detailed project structure reference:
- Complete directory layout
- Package naming rules and import guidelines
- Feature module structure patterns
- File templates and naming conventions

### 🚨 [troubleshooting.instructions.md](./troubleshooting.instructions.md)
**Applies to:** `**/*.kt`  
**Purpose:** Common issues and solutions:
- Compilation error fixes
- Architecture-specific problems
- Diagnostic commands
- Rollback procedures
- Emergency contacts/resources

## Key Principles

### 🔴 NEVER Skip These Steps:
1. **Always run compilation check** before claiming work is complete
2. **Fix ALL compilation errors** - never leave unresolved references  
3. **Verify imports** match package structure

### ✅ Always Follow:
- Use NavigationViewModel pattern (not direct NavController)
- Create ViewModels using Dagger components
- Package declarations must match file locations
- Update preview functions when changing composable signatures

### 🎯 Success Criteria:
- [ ] Code compiles without errors: `./gradlew compileDebugKotlin --no-daemon`
- [ ] All references are resolved
- [ ] Follows established architecture patterns
- [ ] No breaking changes to existing functionality

## Quick Reference

### Navigation (Post-Refactor)
```kotlin
// ✅ DO: Use callbacks and NavigationViewModel
@Composable
fun MyScreen(
    viewModel: MyViewModel,
    navigationViewModel: NavigationViewModel
)

// ❌ DON'T: Pass NavController directly
@Composable  
fun MyScreen(navController: NavController)
```

### ViewModel Creation
```kotlin
// ✅ DO: Use Dagger component pattern
val component = FeatureComponent.create(activityComponent, params)
val vm = createFeatureViewModel(component, activity, viewModelStoreOwner)

// ❌ DON'T: Use standard ViewModelProvider
val vm = viewModel<MyViewModel>()
```

### Package Structure
```kotlin
// ✅ DO: Match package to file location
// File: /navigation/Navigation.kt
package com.example.stretchy.navigation

// ❌ DON'T: Mismatch package and location  
// File: /navigation/Navigation.kt
package com.example.stretchy // Wrong!
```

## Usage

These instructions are designed to be referenced by AI assistants (like GitHub Copilot) to ensure consistent, error-free development on this project. Each file contains specific guidance for different aspects of the codebase.

When working on this project, always:
1. Read the relevant instruction files first
2. Follow the established patterns
3. Run compilation checks
4. Verify your changes don't break existing functionality

## Maintenance

Update these instruction files when:
- Architecture patterns change
- New common issues are discovered
- Project structure evolves
- New best practices are established

The goal is to have a self-documenting codebase that guides contributors toward consistent, maintainable solutions.
