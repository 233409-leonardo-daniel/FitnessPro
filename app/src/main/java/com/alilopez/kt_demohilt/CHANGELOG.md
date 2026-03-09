# Changelog

## [Unreleased]

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

