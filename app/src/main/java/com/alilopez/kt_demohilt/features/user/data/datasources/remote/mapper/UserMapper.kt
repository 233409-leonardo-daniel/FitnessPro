package com.alilopez.kt_demohilt.features.user.data.datasources.remote.mapper

import com.alilopez.kt_demohilt.features.exercise.data.datasources.remote.mapper.toDomain as exerciseToDomain
import com.alilopez.kt_demohilt.features.recipies.data.datasources.remote.mapper.toDomain as recipeToDomain
import com.alilopez.kt_demohilt.features.user.data.datasources.remote.model.UserDailyResponse
import com.alilopez.kt_demohilt.features.user.data.datasources.remote.model.UserDto
import com.alilopez.kt_demohilt.features.user.domain.entities.UserDailyContent
import com.alilopez.kt_demohilt.features.user.domain.entities.User

fun UserDto.toDomain(): User {
    return User(
        id = this.id,
        email = this.email,
        name = this.name,
        lastname = this.lastname,
        birthdate = this.birthdate,
        weight = this.weight,
        height = this.height,
        gender = this.gender,
        age = this.age
    )
}

fun UserDailyResponse.toDomain(): UserDailyContent {
    return UserDailyContent(
        userId = this.userId,
        userName = this.userName,
        userLastname = this.userLastname,
        day = this.day,
        timezone = this.timezone,
        exercises = this.exercises.map { it.exerciseToDomain() },
        recipes = this.recipes.map { it.recipeToDomain() }
    )
}

