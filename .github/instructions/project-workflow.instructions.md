# Project Workflow Instructions

applyTo: "**/*.kt"

## Mandatory Workflow for ALL Code Changes

### 1. Analysis Phase
- [ ] Read existing code structure before making changes
- [ ] Understand the current architecture patterns
- [ ] Identify the specific problem or feature request
- [ ] Plan changes according to existing patterns

### 2. Implementation Phase  
- [ ] Follow existing naming conventions
- [ ] Use established architectural patterns
- [ ] Maintain consistency with current codebase
- [ ] Update related files (previews, tests, etc.)

### 3. Verification Phase (CRITICAL - NEVER SKIP)
- [ ] **ALWAYS run compilation check**: `./gradlew compileDebugKotlin --no-daemon`
- [ ] Fix ALL compilation errors before claiming completion
- [ ] Verify imports are correct and resolved
- [ ] Check package declarations match file locations
- [ ] Test that the changes work as expected

### 4. Completion Criteria
- [ ] Code compiles without errors
- [ ] All references are resolved
- [ ] Follows project architecture patterns
- [ ] No breaking changes to existing functionality

## Architecture Decision Rules

### ViewModels
- Use Dagger-scoped ViewModels, not standard ViewModelProvider
- Follow the `create*ViewModel()` pattern for instantiation  
- Expose StateFlow for UI state, SharedFlow for events

### Navigation
- **POST-REFACTOR**: Use NavigationViewModel pattern, NOT direct NavController
- Pass callbacks to composables, not navigation dependencies
- Centralize navigation logic in NavigationViewModel

### Dependency Injection
- Use Dagger components with proper scoping
- Follow existing component creation patterns
- Inject dependencies through constructors

### Package Structure
- File location MUST match package declaration
- Features go in `/features/featurename/`
- UI goes in `/ui/` with appropriate subfolders
- Navigation goes in `/navigation/`

## Error Prevention Checklist

### Before Claiming Work Complete:
1. **Compilation Check**: Run `./gradlew compileDebugKotlin --no-daemon`
2. **Import Verification**: All imports resolve correctly
3. **Package Check**: Package declarations match file paths  
4. **Architecture Compliance**: Changes follow existing patterns
5. **No Breaking Changes**: Existing functionality still works

### Common Gotchas:
- Package declarations not matching folder structure
- Circular imports between packages
- Using old navigation patterns (direct NavController)
- Missing imports after refactoring
- Preview functions not updated after signature changes

## Quality Standards

### Code Quality
- Follow Kotlin conventions
- Use meaningful names
- Avoid abbreviations in variable names
- Use sealed classes for state/events
- Prefer StateFlow over LiveData

### Testing Considerations  
- ViewModels should be testable without Android dependencies
- Composables should be testable without external dependencies
- Use dependency injection for testability

### Documentation
- Update documentation when changing public APIs
- Add KDoc for complex functions
- Keep README files up to date

## Emergency Procedures

### If Build Fails After Changes:
1. Check compilation output for specific errors
2. Verify package declarations match file locations
3. Check for missing or incorrect imports
4. Look for circular dependency issues
5. Revert changes if necessary and apply incrementally

### If Architecture Seems Wrong:
1. Review existing similar code in the project
2. Follow established patterns, don't invent new ones
3. Ask for clarification rather than guessing
4. Maintain consistency with current codebase

## Success Metrics
- [ ] Code compiles successfully
- [ ] No runtime crashes introduced  
- [ ] Follows established architecture patterns
- [ ] Maintains code quality standards
- [ ] Documentation is updated if needed
