package com.alilopez.kt_demohilt.features.exercise.domain.entities

/**
 * Representa una respuesta paginada de ejercicios remotos.
 * Utilizada para soportar infinite scroll con paginación por cursor.
 */
data class PaginatedExercises(
    val exercises: List<Exercise>,
    val hasNextPage: Boolean,
    val nextCursor: String?,
    val total: Int
)
