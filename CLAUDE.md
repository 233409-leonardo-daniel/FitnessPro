# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
# Build
./gradlew assembleDebug
./gradlew assembleRelease
./gradlew assembleDev      # dev flavor
./gradlew assembleProd     # prod flavor

# Tests
./gradlew test                    # unit tests (JVM)
./gradlew connectedAndroidTest    # instrumented tests (device/emulator)
./gradlew test --tests "com.alilopez.kt_demohilt.ExampleUnitTest"

# Clean
./gradlew clean build
```

## Architecture

Clean Architecture + MVVM. Root package: `com.alilopez.kt_demohilt`.

**Three layers per feature:**
- `data/` — Repository implementations, remote/local datasources, DTOs, mappers
- `domain/` — Kotlin entities, repository interfaces, use cases
- `presentation/` — Composable screens, reusable components, `@HiltViewModel` ViewModels

**Cross-cutting `core/` modules:**
- `core/di/` — Hilt modules: `NetworkModule` (OkHttp/Retrofit), `FitnessProNetworkModule` (API service), `DatabaseModule` (Room + DAOs), `HardwareModule` (camera)
- `core/network/` — `FitnessProApi` (Retrofit interface, single source of truth for all endpoints)
- `core/database/` — Room `AppDatabase` (version 2), entities, DAOs, type converters, mappers
- `core/navigation/` — `NavigationWrapper.kt` with type-safe Compose NavHost routes
- `core/session/` — `SessionManager` tracking auth state and membership

**Features:** `exercise`, `recipies`, `recipeplans`, `workoutplans`, `user`, `home`. Each feature except `home` has its own `di/` module binding interfaces to implementations.

## Key Tech

- **UI:** Jetpack Compose + Material3, Compose Navigation
- **DI:** Hilt (KSP), `@HiltAndroidApp` on `DemoHiltApp`, `@AndroidEntryPoint` on `MainActivity`
- **Networking:** Retrofit 3 + Gson; multipart form data used for image uploads
- **Images:** Coil (coil-compose, coil-gif)
- **Local DB:** Room 2.8.4
- **State:** `StateFlow`/`MutableStateFlow`, `collectAsStateWithLifecycle`
- **Auth:** Google Sign-In via `androidx.credentials` API
- **Ads:** Google AdMob (`play-services-ads 24.4.0`)
- **Secrets:** Gradle Secrets Plugin manages API keys/AdMob IDs

## Build Flavors

| Flavor | App Name | Base URL | AdMob |
|--------|----------|----------|-------|
| `dev`  | FitnessPro (DEV) | dev endpoint | test ad unit IDs |
| `prod` | FitnessPro | prod endpoint | real ad unit IDs |

## Data Flow Pattern

`Screen` → collects `StateFlow` from `ViewModel` → ViewModel calls `UseCase` → UseCase calls `Repository interface` → `Repository impl` decides remote vs local → returns domain entity → ViewModel maps to UI state.

Mappers live in `data/` and convert DTOs ↔ domain entities. ViewModels never touch DTOs directly.
