package com.alilopez.kt_demohilt.features.exercise.data.datasources.remote.model

/**
 * Respuesta paginada del endpoint GET /exercises/remote
 * Nuevo contrato API v1.6.1
 */
data class ExerciseListResponse(
    val success: Boolean,
    val meta: ExerciseMeta,
    val data: List<ExercisesDto>
)

/**
 * Metadata de paginación para navegación por cursor
 */
data class ExerciseMeta(
    val total: Int,
    val hasNextPage: Boolean,
    val hasPreviousPage: Boolean,
    val nextCursor: String?
)

/**
 * DTO de ejercicio remoto - Contrato API v1.6.1
 * Nota: La API remota ya no provee imágenes (gifUrl/imageUrl eliminados del contrato)
 */
data class ExercisesDto(
    val exerciseId: String,
    val name: String,
    val exerciseType: String? = null,
    val targetMuscles: List<String> = emptyList(),
    val bodyParts: List<String> = emptyList(),
    val equipments: List<String> = emptyList(),
    val secondaryMuscles: List<String> = emptyList(),
    val keywords: List<String> = emptyList(),
    val instructions: List<String> = emptyList()
)
