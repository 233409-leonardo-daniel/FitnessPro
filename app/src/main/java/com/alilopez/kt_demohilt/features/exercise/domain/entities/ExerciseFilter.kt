package com.alilopez.kt_demohilt.features.exercise.domain.entities

data class ExerciseFilter(
    val bodyPart: String? = null,
    val difficulty: String? = null,
    val exerciseType: String? = null
) {
    val isEmpty: Boolean
        get() = bodyPart == null && difficulty == null && exerciseType == null
}

