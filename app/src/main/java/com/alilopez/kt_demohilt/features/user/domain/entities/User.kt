package com.alilopez.kt_demohilt.features.user.domain.entities

data class User(
    val id: Int,
    val email: String,
    val name: String,
    val lastname: String,
    val birthdate: String?,
    val weight: Double?,
    val height: Double?,
    val gender: String?,
    val age: Int? = null,
    val membership: String? = null
)
