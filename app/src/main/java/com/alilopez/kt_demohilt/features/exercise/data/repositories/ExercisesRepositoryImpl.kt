package com.alilopez.kt_demohilt.features.exercise.data.repositories

import com.alilopez.kt_demohilt.core.network.FitnessProApi
import com.alilopez.kt_demohilt.features.exercise.data.datasources.remote.mapper.toDomain
import com.alilopez.kt_demohilt.features.exercise.domain.entities.Exercise
import com.alilopez.kt_demohilt.features.exercise.domain.repositories.ExerciseRepository
import javax.inject.Inject

class ExercisesRepositoryImpl @Inject constructor(
    private val api: FitnessProApi
) : ExerciseRepository {

    override suspend fun getExercises(): List<Exercise> {
        val response = api.getExercisesRemote(
            limit = 2
        )
        return response.data.map { it.toDomain() }
    }

    override suspend fun getExercisesByBodyPart(bodyPart: String): List<Exercise> {
        val response = api.getExercisesByBodyPartRemote(
            limit = 5,
            bodyPart = bodyPart
        )
        return response.data.map { it.toDomain() }
    }

    override suspend fun getLocalExercises(): List<Exercise> {
        return api.getExercisesLocal().map { it.toDomain() }
    }
}

