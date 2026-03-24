package com.alilopez.kt_demohilt.features.user.data.datasources.remote.mapper

import com.alilopez.kt_demohilt.features.user.data.datasources.remote.model.UserDto
import com.alilopez.kt_demohilt.features.user.domain.entities.User

fun UserDto.toDomain(): User {
    return User(
        id = id,
        email = email,
        name = name,
        lastname = lastname,
        birthdate = birthdate,
        weight = weight,
        height = height,
        gender = gender,
        age = age,
        membership = membership
    )
}
