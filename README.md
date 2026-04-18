# FitnessPro

Aplicación Android de fitness que permite a los usuarios gestionar rutinas de ejercicio, planes de comida, recetas saludables y seguimiento de progreso físico. Soporta contenido de comunidad y contenido premium con suscripción.

---

## Características principales

- **Autenticación** — Login con email/contraseña y Google Sign-In
- **Ejercicios** — Explorar catálogo de la comunidad, crear ejercicios propios, filtrar por grupo muscular, equipamiento y dificultad
- **Rutinas de entrenamiento** — Crear y gestionar planes de workout, agregar/remover ejercicios, descargar para uso offline
- **Recetas** — Catálogo de recetas saludables con ingredientes, instrucciones y video, búsqueda por nombre
- **Planes de comida** — Organizar recetas en planes semanales, descargar offline
- **Progresión** — Registrar y visualizar historial de progreso físico
- **Contenido diario** — Resumen personalizado en pantalla principal según membresía
- **Premium Gate** — Restricción de contenido según nivel de suscripción del usuario
- **Notificaciones push** — Firebase Cloud Messaging para nuevos contenidos y confirmaciones de pago

---

## Tecnologías

### Android / Kotlin
| Tecnología | Uso |
|---|---|
| **Jetpack Compose + Material3** | UI declarativa |
| **Hilt (KSP)** | Inyección de dependencias |
| **Coroutines + Flow / StateFlow** | Concurrencia y estados reactivos |
| **Retrofit 3 + Gson** | Consumo de API REST, multipart para subida de imágenes |
| **Coil (coil-compose, coil-gif)** | Carga asíncrona de imágenes y GIFs |
| **Compose Navigation** | Navegación type-safe entre pantallas |

### Room (base de datos local)
Room v2.8.4 persiste ejercicios, recetas, planes de workout y planes de comida para uso offline. Entidades principales:

- `ExerciseEntity` — ejercicios con días programados, grupos musculares, equipamiento
- `RecipeEntity` — recetas con ingredientes, instrucciones, URL de imagen
- `WorkoutPlanEntity` + `WorkoutPlanExerciseCrossRef` — planes con relación many-to-many
- `RecipePlanEntity` + `RecipePlanRecipeCrossRef` — listas de comidas con relación many-to-many

### Hardware
Integración con hardware del dispositivo a través de interfaces en `core/hardware/`:

| Manager | Función                                                   |
|---|-----------------------------------------------------------|
| `CameraPhotoManager` | Captura de fotos para subir imagen de receta / ejercicios |
| `MicrophoneManager` | Acceso al micrófono                                       |
| `SoundManager` | Reproducción de audio                                     |

### WorkManager
Workers de background con Hilt (`@HiltWorker`) para operaciones de larga duración:

| Worker | Función |
|---|---|
| `PlanSyncWorker` | Sincroniza en paralelo planes de workout y comida descargados con el servidor |
| `DownloadWorkoutPlanWorker` | Descarga un plan de rutina completo para uso offline |
| `DownloadRecipePlanWorker` | Descarga un plan de comidas completo para uso offline |
| `MealReminderWorker` | Envía recordatorios de comida programados |
| `SyncSubscriptionWorker` | Actualiza el estado de membresía tras confirmación de pago |

WorkManager está inicializado manualmente con Hilt (desactivado el `WorkManagerInitializer` por defecto). Los workers críticos corren como Foreground Service con tipo `dataSync`.

### Services
| Servicio | Función |
|---|---|
| `FitnessFCMService` | Firebase Cloud Messaging — recibe push de `PAYMENT_SUCCESS` (dispara `SyncSubscriptionWorker`) y `NEW_CONTENT` (muestra notificación al usuario); actualiza el FCM token en el servidor cuando rota |

### Notificaciones
`NotificationHelper` centraliza la creación de notificaciones locales. Las notificaciones push se integran con WorkManager para garantizar ejecución incluso cuando la app está en background.

---

## Arquitectura

Clean Architecture + MVVM. 

```
core/
├── database/          # Room: AppDatabase, entidades, DAOs, type converters, mappers
├── di/                # Módulos Hilt: Network, Database, Hardware, Worker
├── hardware/          # Interfaces de hardware + implementaciones Android
├── navigation/        # NavigationWrapper con NavHost, rutas type-safe
├── network/           # FitnessProApi (Retrofit), AuthInterceptor
├── notifications/     # FitnessFCMService, NotificationHelper
├── session/           # SessionManager (estado de auth y membresía)
└── ui/theme/          # Colores, tipografía, tema Material3

features/
├── exercise/          # Catálogo y gestión de ejercicios
├── workoutplans/      # Planes de entrenamiento
├── recipies/          # Recetas saludables
├── recipeplans/       # Planes de comida
├── progression/       # Historial de progreso físico
├── user/              # Auth, perfil, registro
└── home/              # Pantalla principal con contenido diario
```

Cada feature sigue el patrón:
```
feature/
├── data/       # DTOs, mappers, datasources remotos/locales, workers, repositorio impl
├── domain/     # Entidades Kotlin, interfaz de repositorio, use cases
└── presentation/  # UIState, ViewModel (@HiltViewModel), Screens/Components (Composables)
```

Flujo de datos: `Screen` → `ViewModel` (StateFlow) → `UseCase` → `Repository` → remote/local → entidad de dominio → UI state.

---

## Flavors de build

| Flavor | App Name | Base URL | AdMob |
|---|---|---|---|
| `dev` | FitnessPro (DEV) | Endpoint de desarrollo | Ad unit IDs de prueba |
| `prod` | FitnessPro | Endpoint de producción | Ad unit IDs reales |

Las API keys y el App ID de AdMob se gestionan con el **Gradle Secrets Plugin** (no están en el repositorio).

---

## Comandos de build

```bash
# Debug
./gradlew assembleDebug

# Por flavor
./gradlew assembleDev
./gradlew assembleProd

# Release
./gradlew assembleRelease

# Tests unitarios
./gradlew test

# Tests instrumentados (requiere dispositivo/emulador)
./gradlew connectedAndroidTest

# Limpiar
./gradlew clean build
```

---

## Permisos requeridos

| Permiso | Motivo |
|---|---|
| `INTERNET` | Comunicación con la API |
| `CAMERA` | Captura de fotos |
| `RECORD_AUDIO` | Acceso a micrófono |
| `POST_NOTIFICATIONS` | Notificaciones push |
| `FOREGROUND_SERVICE` + `WAKE_LOCK` | WorkManager en background |
| `RECEIVE_BOOT_COMPLETED` | Reprogramar workers tras reinicio |

---

## Agradecimientos

Gracias a **[Ali Lopez (alilopez37)](https://github.com/alilopez37)** por aportar su repositorio como base para la construcción de esta aplicación.
