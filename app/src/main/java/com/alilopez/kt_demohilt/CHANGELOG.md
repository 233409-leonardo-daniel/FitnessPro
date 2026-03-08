# Changelog

## [Unreleased]

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

