# Changelog

## [Unreleased]


## Cómo usar este archivo
1. Al terminar un cambio importante, añade una entrada nueva arriba en "Historial de cambios".
2. Sigue el formato de la plantilla (versión/fecha, tipo, breve detalle, archivos tocados, autor, issue).
3. Cuando el cambio requiera actualizar la versión, sigue las reglas en la sección "Versionamiento...".
4. No borres entradas antiguas; sólo añade nuevas.

---

## Versionamiento y reglas para cambiar versión

Usamos un esquema simple tipo SemVer: MAJOR.MINOR.PATCH (ej. 0.1.0).

- Bump PATCH (ej. 0.1.1) = Corrección de bugs o cambios pequeños que no añaden features.
- Bump MINOR (ej. 0.2.0) = Nueva funcionalidad que no rompe compatibilidad (feature visible, nuevos endpoints, UI importante).
- Bump MAJOR (ej. 1.0.0) = Cambio incompatible que rompe flujos existentes, reestructura DB o cambia contratos de API.

Reglas prácticas:
- Si el cambio es solo CSS o texto, solo añade entrada y no cambies versión (opcional: PATCH).
- Si agregas una nueva API o endpoint, sube MINOR.
- Si arreglas un bug visible o typo en lógica, sube PATCH.
- Si cambias la estructura de la base de datos o rompes endpoints, sube MAJOR.

Cómo cambiar la versión en el changelog:
- Edita la línea "Versión actual" al inicio con la nueva versión.
- Añade una entrada nueva con la versión y fecha en la parte superior del "Historial de cambios".

(Nota: el versionamiento en este archivo es manual; si usan git tags, pueden agregar tags aparte.)

## [3.3.2] - 2026-03-22

### Added
- Se agrego una screen para detalles de una recipe.

## [3.3.1] - 2026-03-12

### Fixed
- Se corrigio un error en `AddExerciseScreen` donde la imagen tomada con la camara no se mostraba correctamente debido a un error en la ruta del archivo, ahora se muestra la imagen correctamente al tomar la foto y guardarla en el estado de la pantalla.
- Se corrigio hardcodeo de URL de imagen en `ExerciseCard` para ejercicios locales, ahora se muestra la imagen correcta desde el campo `image_url` de la entidad `LocalExerciseEntity`.

## [3.3.0] - 2026-03-11

### Added
- Microfono para grabar instrucciones de ejercicios locales, con almacenamiento en Firebase Storage y URL guardada en la entidad `LocalExerciseEntity`.

## [3.2.4] - 2026-03-10

### Fixed
- Se corrigio un error de UI en `ExercisesScreen` donde los ejercicios locales no se mostraban correctamente debido a un bucle infinito al cargar los datos, ahora se cargan sin recargar la pantalla.
- Ya funciona el Dao para obtener los ejercicios y para filtrar de manera rapida los ejercicios locales del usuario, mostrando solo los suyos en la seccion "Mis Ejercicios" y el resto en "Ejercicios Recomendados".

## [3.2.3] - 2026-03-09

### Fixed
- Se corrigio bucle infinito en `ExercisesScreen` al cargar ejercicios locales, ahora se cargan correctamente sin recargar la pantalla.

## [3.2.2] - 2026-03-09

### Added
- Soporte para almacenamiento local de ejercicios usando Room.
- Dao `LocalExerciseDao` con métodos para insertar, obtener y eliminar ejercicios locales.

## [3.2.1] - 2026-03-09

### Added
- Dao de ejercicios locales `LocalExerciseDao` con Room para almacenamiento persistente en SQLite.
- Entidad `LocalExerciseEntity` con campos: `id`, `name`, `description`, `instructions`, `exercise_type`, `difficulty`, `scheduled_days`, `image_path`.
-
### Changed
- Refactorizado para la inyeccion de dependencias
- 
## [3.2.0] - 2026-03-08

### Added
- Pantalla `AddExerciseScreen` para crear ejercicios locales con foto tomada desde la cámara del dispositivo.
- `AddExerciseViewModel` con StateFlows separados siguiendo arquitectura limpia (nombre, descripción, instrucciones, tipo, dificultad, días, foto).
- `AddExerciseUiState` como estado de UI dedicado para la pantalla de creación de ejercicio.
- `CreateLocalExerciseUseCase` para encapsular la lógica de creación de ejercicios locales.
- Endpoint `@Multipart @POST("exercises/local")` en `FitnessProApi` con soporte para `Form` fields e imagen.
- Método `createLocalExercise()` en `ExerciseRepository` y `ExercisesRepositoryImpl` con conversión multipart.
- Ruta de navegación `AddExercise` en `Screens.kt` y `NavigationWrapper.kt`.
- Botón `+` en la TopBar de `ExercisesScreen` para navegar a la pantalla de creación.

### Changed
- `ExercisesScreen` ahora recibe `onNavigateToAddExercise` como parámetro de navegación.
- Colores de `ExercisesScreen` y `ExerciseCard` actualizados para respetar el esquema de colores del sistema (verde `#10B981`, fondos `#0F172A`/`#F8FAFC`) con soporte claro/oscuro.

## [3.1.0] - 2026-03-08

### Added
- Soporte para ejercicios locales del usuario via `api/exercises/local`.
- `LocalExerciseDto` con campos: `description`, `user_id`, `scheduled_days`, `image_url`, `bodyparts`, `equipments`, `targetMuscles`, `secondaryMuscles`, `exercise_type`, `instructions`, `difficulty`.
- Mapper `LocalExerciseDto.toDomain()` para convertir ejercicios locales a la entidad `Exercise`.
- `GetLocalExercisesUseCase` para obtener ejercicios propios del usuario.
- `getLocalExercises()` en `ExerciseRepository` y `ExercisesRepositoryImpl`.
- Seccion "Mis Ejercicios" en `ExercisesScreen` mostrando ejercicios locales con badges de tipo y dificultad.

### Changed
- Entidad `Exercise` extendida con campos opcionales: `description`, `userId`, `scheduledDays`, `bodyparts`, `equipments`, `secondaryMuscles`, `exerciseType`, `difficulty`, `isLocal`.
- `ExercisesUiState` ahora incluye `localExercises` para mantener los ejercicios locales separados de los remotos.
- `ExerciseViewModel` carga ambos tipos de ejercicios en `init`.
- `ExerciseCard` actualizado con parametros opcionales `isLocal`, `exerciseType`, `difficulty`; muestra badges y oculta imagen cuando esta vacia.
- Endpoints `exercises/local` y `exercises/local/user/{user_id}` en `FitnessProApi` ahora retornan `List<LocalExerciseDto>` en vez de `ExercisesResponse`.


## [3.0.0] - 2026-03-08

### Added
- Soporte de creacion de recetas via `multipart/form-data` con `@Multipart` en `FitnessProApi` para enviar campos `Form` e imagen (`image`).
- Integracion de camara como hardware real para recetas mediante `CameraPhotoManager` y `AndroidCameraPhotoManager`.
- Configuracion de `FileProvider` (`@xml/file_paths`) y permiso `CAMERA` en `AndroidManifest.xml` para captura segura de fotos.
- Nuevos estados de pantalla separados: `AddRecipeUIState`, `EditRecipeUIState` y `RecipesListUIState`.

### Changed
- Entidad y mapeo de receta alineados con API: `scheduled_days`, `meal_type` e `image_url`.
- Flujo de creacion de receta migrado de `@Body` JSON a request multipart con `RequestBody`/`MultipartBody.Part`.
- `AddRecipeScreen` ahora permite tomar foto con camara y previsualizarla antes de guardar.
- Tipo de comida en formularios de recetas actualizado a seleccion unica (radio buttons) para evitar texto libre.

### Refactored
- Separacion de responsabilidades en recetas: `RecipesListViewModel`, `AddRecipeViewModel` y `EditRecipeViewModel`.
- Adopcion del patron de estado de `LoginViewModel`: variables de formulario en `StateFlow` independientes y `uiState` solo para loading/error/eventos.
- Las screens de recetas consumen estado con `collectAsStateWithLifecycle` y delegan mutaciones al ViewModel.

## [2.1.1] - 2026-03-08

### Fixed
- Se corrigio un error de UI en `HomeScreen` donde las cards de recetas y ejercicios no se mostraban correctamente
- Se ajusto la entidad de receta para aceptar imagenes y se corrigio el mapeo en `HomeViewModel` para mostrar las imagenes en las cards.

## [2.1.0] - 2026-03-08

### Added
- Nueva pantalla `HomeScreen` que combina recetas y ejercicios en una sola vista con scroll vertical.
- `HomeViewModel` con inyeccion de `GetRecipiesUseCase` y `GetExercisesUseCase` para cargar ambos datos al iniciar.
- `HomeUIState` con estados separados de carga/error para recetas y ejercicios.
- Cards compactas de receta (`HomeRecipeCard`) y ejercicio (`HomeExerciseCard`) con estilo verde uniforme.
- Seccion "Recetas Guardadas" con acceso a agregar/editar recetas desde Home.
- Seccion "Recomendados para ti" mostrando los primeros 5 ejercicios con GIF y musculos objetivo.

### Changed
- Flujo de navegacion post-login ahora dirige a `HomeScreen` en lugar de la pantalla de recetas individual.
- `NavigationWrapper` actualizado para importar `HomeScreen` desde `features.home.presentation.screens`.

## [2.0.0] - 2026-03-08

### Fixed
- Se corrigio el crash de navegacion en Compose al intentar ir a `Home` sin destino registrado en el `NavHost`.
- Se registraron rutas faltantes en navegacion (`Home`, `AddRecipe`, `EditRecipe`, `Exercises`) para evitar errores de destino no encontrado.

### Changed
- Se actualizo el contrato de usuario para alinearlo con la API en login/register.
- `UserLoginResponseDto` ahora contempla: `email`, `name`, `lastname`, `birthdate`, `weight`, `height`, `gender`, `id`, `age`, `access_token`, `token_type`.
- `UserCreateDto`, entidad `User`, mappers y flujo de repositorio/use case de registro fueron ajustados con nuevos campos: `birthdate`, `weight`, `height`, `gender`.
- Se agrego validacion basica y parseo numerico (`Double`) para `weight` y `height` en el flujo de registro.

### UI
- En `RegisterScreen`, `birthdate` dejo de ser texto libre y ahora se selecciona con calendario (`DatePickerDialog`).
- El input de fecha mantiene el estilo visual verde reutilizando `InputFitness`.
- `InputFitness` fue extendido para soportar `readOnly`, `trailingIcon` y accion de click en icono trailing.

