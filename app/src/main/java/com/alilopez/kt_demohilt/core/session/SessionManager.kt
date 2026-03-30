package com.alilopez.kt_demohilt.core.session

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor() {

    private var _currentUserId: Int? = null
    val currentUserId: Int? get() = _currentUserId

    private var _accessToken: String? = null
    val accessToken: String? get() = _accessToken

    private val _membership = MutableStateFlow<String?>(null)
    val membership: StateFlow<String?> = _membership.asStateFlow()

    fun saveSession(userId: Int, token: String, membership: String? = null) {
        _currentUserId = userId
        _accessToken = token
        _membership.value = membership
    }

    fun clearSession() {
        _currentUserId = null
        _accessToken = null
        _membership.value = null
    }

    fun isLoggedIn(): Boolean = _currentUserId != null
}
