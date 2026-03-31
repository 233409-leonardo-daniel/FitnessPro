# AGENTS.md

Guidance for autonomous and semi-autonomous coding agents working in this repository.

## 1) Project Overview

- Android app built with Kotlin + Jetpack Compose.
- Architecture style: Clean Architecture + MVVM.
- Main layers by feature: `data/`, `domain/`, `presentation/`.
- Dependency injection: Hilt.
- Networking: Retrofit + Gson.
- Local persistence: Room.
- Async/reactive: Coroutines + Flow + StateFlow.
- Product flavors: `dev` and `prod` (dimension: `environment`).

## 2) Source Layout

- App module: `app/`.
- Main code: `app/src/main/java/com/alilopez/kt_demohilt/...`.
- Unit tests (JVM): `app/src/test/...`.
- Instrumented tests (device/emulator): `app/src/androidTest/...`.
- Version catalog: `gradle/libs.versions.toml`.
- Changelog: `CHANGELOG.md`.

## 3) Build, Lint, and Test Commands

Use Gradle wrapper from repository root.

### Core commands

- Build everything: `./gradlew :app:build`
- Assemble debug (all flavors): `./gradlew :app:assembleDebug`
- Assemble specific flavor: `./gradlew :app:assembleDevDebug`
- Run lint (default variant): `./gradlew :app:lint`
- Run lint for specific variant: `./gradlew :app:lintDevDebug`
- Run all unit tests: `./gradlew :app:test`
- Run variant unit tests: `./gradlew :app:testDevDebugUnitTest`
- Run connected instrumented tests: `./gradlew :app:connectedDevDebugAndroidTest`

### Run a single test (important)

- Single JVM unit test class:
  `./gradlew :app:testDevDebugUnitTest --tests "com.alilopez.kt_demohilt.ExampleUnitTest"`
- Single JVM unit test method:
  `./gradlew :app:testDevDebugUnitTest --tests "com.alilopez.kt_demohilt.ExampleUnitTest.addition_isCorrect"`
- Single instrumented test class:
  `./gradlew :app:connectedDevDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.alilopez.kt_demohilt.ExampleInstrumentedTest`
- Single instrumented test method:
  `./gradlew :app:connectedDevDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.alilopez.kt_demohilt.ExampleInstrumentedTest#useAppContext`

### Useful verification commands

- Fast sanity check before PR: `./gradlew :app:lint :app:testDevDebugUnitTest`
- Clean build artifacts: `./gradlew :app:clean`
- List available tasks: `./gradlew tasks --all`

### Windows note

- On Windows shells that do not support `./gradlew`, use `gradlew.bat` with the same task names.

## 4) Required Local Configuration

- This project reads secrets/values from `local.properties`.
- Required keys include: `BACKEND_URL`, `GOOGLE_CLIENT_ID`.
- Flavor/build ad values may also come from local properties (for prod).
- Do not commit secrets or personal local values.

## 5) Architecture and Design Rules

- Keep feature code grouped by layer: `data`, `domain`, `presentation`.
- `domain` should not depend on Android UI framework classes.
- `presentation` should expose immutable UI state and events.
- Prefer use cases for business actions; keep ViewModels orchestration-focused.
- Repository interfaces belong in `domain`; implementations belong in `data`.
- Use DI modules to wire dependencies instead of manual singletons.
- Respect existing package naming and feature boundaries.

## 6) Kotlin Style Guidelines

- Follow Kotlin official style and existing code conventions in this repo.
- Use 4-space indentation; avoid tab characters.
- Prefer expression clarity over over-compact one-liners.
- Keep functions small and single-purpose.
- Prefer immutable `val`; use `var` only when mutation is required.
- Use trailing commas where they improve diffs/readability.
- Avoid wildcard imports; import explicit symbols.
- Keep import groups clean; remove unused imports.
- Use meaningful names; avoid abbreviations except established ones (`dto`, `dao`).

## 7) Naming Conventions

- Classes/objects/interfaces: `PascalCase`.
- Functions/properties/locals/params: `camelCase`.
- Constants: `UPPER_SNAKE_CASE`.
- Compose screens/components end with `Screen`/`...Card`/`...Dialog` as already used.
- State holder classes end with `UIState`.
- ViewModels end with `ViewModel`.
- Use cases end with `UseCase` and typically expose `operator fun invoke(...)`.
- Repository implementation names should be `...RepositoryImpl` (use `Impl`, not `Imp`, for new code).

## 8) Types, Nullability, and Data Modeling

- Always declare explicit public API types when inference is unclear.
- Use nullable types only when null is a real domain state.
- Convert external DTOs/entities to domain models via mapper functions.
- Avoid passing framework models across layers.
- Prefer sealed/result-style modeling for complex operation states.

## 9) Error Handling and Logging

- Never swallow exceptions silently.
- Catch exceptions at layer boundaries where you can recover or map errors.
- Map technical exceptions to user-meaningful UI messages when possible.
- Preserve original causes when rethrowing/mapping.
- Keep logs concise and actionable; avoid logging sensitive data.
- Do not expose secrets/tokens/base URLs in logs or error messages.

## 10) Compose and UI Practices

- Keep composables mostly stateless; hoist mutable state upward.
- Separate screen-level orchestration from reusable UI components.
- Use `collectAsStateWithLifecycle` pattern consistently.
- Ensure loading, empty, content, and error states are represented in UI state.
- Keep navigation concerns in navigation wrappers/graphs, not deep UI leaves.

## 11) Dependency and Build Constraints (Project Rules)

- Do not modify `build.gradle` files without explicit team approval.
- Do not add external dependencies without explicit approval.
- Do not perform architecture-wide refactors without prior discussion.
- Prefer using existing libraries from `libs.versions.toml`.

## 12) Documentation and Change Management

- Update `CHANGELOG.md` for significant changes.
- Keep PR/commit descriptions focused on why and impact.
- If a backend/frontend integration is complex, document it in `CONTEXTO_BACKEND.md`.
- Keep code comments minimal; add them only where intent is non-obvious.

## 13) Agent Workflow Expectations

- Before editing, inspect nearby code and follow existing patterns.
- Make minimal, targeted changes; avoid unrelated refactors.
- Prefer fixing root causes over superficial patches.
- After changes, run relevant lint/tests for touched code paths.
- If tests cannot run locally (missing device/SDK/secrets), state exactly what was not validated.

## 14) Cursor/Copilot Rules Check

- `.cursorrules`: not present.
- `.cursor/rules/`: not present.
- `.github/copilot-instructions.md`: not present.
- Therefore, this file and in-repo conventions are the active agent guidance.
