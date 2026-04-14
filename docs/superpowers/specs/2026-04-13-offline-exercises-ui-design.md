# Especificación Técnica: UI y Gestión de Ejercicios Offline

**Fecha:** 2026-04-13
**Objetivo:** Proveer a los usuarios una interfaz para acceder y gestionar sus ejercicios descargados, tanto desde una sesión activa (SliderMenu) como cuando no hay conexión a internet y la sesión expiró (LoginScreen).

## 1. Interfaz de Usuario (UI) y Accesos
*   **Pantalla de Login (`LoginScreen`)**:
    *   Se agregará un botón secundario siempre visible con el texto *"Ver mis descargas (Modo Offline)"*.
    *   Navegará directamente a la pantalla de descargas sin requerir autenticación de red.
*   **Menú Lateral (`SliderMenu`)**:
    *   Se añadirá la opción *"Mis Descargas"*.
    *   **Restricción:** Solo será visible si la variable de estado `isPremium` es verdadera.
*   **Pantalla de Descargas (`OfflineExercisesScreen`)**:
    *   Mostrará los ejercicios mediante el componente `ExerciseCard` existente.
    *   **Estado Vacío:** Mostrará *"Aún no tienes ejercicios descargados"* si la lista está vacía.
    *   **Eliminación:** Cada tarjeta tendrá un ícono de papelera que eliminará la descarga **inmediatamente** (sin diálogos de confirmación).
    *   **Navegación:** Al tocar un ejercicio, se abrirá el `ExerciseDetailScreen` (que ya está preparado para leer de la caché local si es necesario).

## 2. Gestión de Pérdida de Premium (Downgrade)
*   **Limpieza Reactiva:** En `MainActivity.kt` se observará el `StateFlow` de `sessionManager.membership`.
*   Si el estado detectado cambia a `"gratuito"`, se ejecutará de forma silenciosa el caso de uso `ClearAllOfflineExercisesUseCase` para remover el estado offline de todos los ejercicios descargados en Room.

## 3. Capa de Navegación (`core/navigation`)
*   Nueva ruta: `data object OfflineExercises`.
*   Se registrará `composable<OfflineExercises>` en el `NavHost` del archivo `NavigationWrapper.kt`.

## 4. Capa de Presentación (`presentation`)
*   **`OfflineExercisesViewModel`**:
    *   Consumirá `GetOfflineExercisesUseCase` para exponer un `StateFlow<List<Exercise>>` a la vista.
    *   Proveerá la función `onRemoveExercise(id: Int)` para eliminar ejercicios descargados individualmente de la caché local.

## 5. Capa de Dominio (`domain`)
*   **Nuevos Casos de Uso (`features/exercise/domain/usecases/`)**:
    *   `GetOfflineExercisesUseCase`: Retorna `Flow<List<Exercise>>`.
    *   `RemoveOfflineExerciseUseCase`: Recibe un ID de ejercicio y actualiza la bandera `offline_available` a false.
    *   `ClearAllOfflineExercisesUseCase`: Actualiza todos los registros donde `offline_available = true` a false.

## 6. Capa de Datos (`data` y `core/database`)
*   **`ExerciseDao` (`core/database/dao/ExerciseDao.kt`)**:
    *   `@Query("SELECT * FROM exercises WHERE offline_available = 1")` que retorna un `Flow<List<ExerciseEntity>>`.
    *   `@Query("UPDATE exercises SET offline_available = 0 WHERE offline_available = 1")` para realizar la limpieza general en una sola transacción.
*   **`ExerciseRepositoryImpl` (`features/exercise/data/repositories/ExerciseRepositoryImpl.kt`)**:
    *   Implementará la lógica de lectura y actualización necesaria usando el DAO y el mapper existente de `ExerciseEntity` a `Exercise`.
