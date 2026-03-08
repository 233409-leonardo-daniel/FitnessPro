package com.alilopez.kt_demohilt.features.user.data.datasources.remote.mapper

import com.alilopez.kt_demohilt.features.user.data.datasources.remote.model.UserDto
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
