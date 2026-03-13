package com.alilopez.kt_demohilt.core.session

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor() {

    private var _currentUserId: Int? = null
    val currentUserId: Int? get() = _currentUserId

    private var _accessToken: String? = null
    val accessToken: String? get() = _accessToken

    fun saveSession(userId: Int, token: String) {
        _currentUserId = userId
        _accessToken = token
    }

    fun clearSession() {
        _currentUserId = null
        _accessToken = null
    }

    fun isLoggedIn(): Boolean = _currentUserId != null
}

