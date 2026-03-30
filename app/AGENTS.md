# AGENTS.md

## Descripción del Proyecto
Este proyecto es una aplicación móvil con Jetpack Compose sobre fitness, principalmente ejercicios y recetas, los usuarios pueden crear su propio perfil, agregar sus ejercicios favoritos, crear rutinas personalizadas y compartirlas con otros usuarios. Además, la aplicación ofrece una sección de recetas saludables para complementar las rutinas de ejercicio.
## Reglas de Codificación
- Seguir buenas practicas de codificación, como nombrado claro de variables y funciones, modularidad y reutilización de código.
- Mantener el código limpio y bien documentado.
- Utilizar control de versiones (CHANGELOG) para gestionar los cambios en el código.

## Arquitectura
- Utilizar una arquitectura limpia y modular, separando las responsabilidades en capas (por ejemplo, capa de presentación, capa de dominio, capa de datos).
- Implementar patrones de diseño adecuados, como MVVM o Clean Architecture, para mejorar la mantenibilidad y escalabilidad del código.
- Seguir el patron repository para la gestión de datos, permitiendo una fácil integración con diferentes fuentes de datos (por ejemplo, API, base de datos local).
- Utilizar herramientas de inyección de dependencias para gestionar las dependencias entre componentes y mejorar la testabilidad del código.

## Límite de Acciones
- No modificar el archivo `build.gradle`.
- No agregar dependencias externas sin la aprobación del equipo.
- No realizar cambios en la arquitectura del proyecto sin discutirlo previamente con el developer.

## Reglas de Interacción
- Modificar changelog.md para documentar cambios significativos.
- Mantener la comunicación clara y concisa en los comentarios y documentación.

## Reglas de comunicación
- Si hay una implementacion compleja entre Backend y Frontend, se debe documentar en CONTEXTO_BACKEND.md para que el equipo de Backend pueda entenderlo y colaborar de manera efectiva.