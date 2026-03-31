package com.alilopez.kt_demohilt.core.session

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(
    @ApplicationContext context: Context
) {

    companion object {
        const val CURRENT_TERMS_VERSION = "1.0"
    }

    private val prefs = context.getSharedPreferences("fitnesspro_session", Context.MODE_PRIVATE)

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

    fun hasAcceptedCurrentTerms(): Boolean {
        val userId = _currentUserId ?: return false
        val acceptedVersion = prefs.getString(termsKey(userId), null)
        return acceptedVersion == CURRENT_TERMS_VERSION
    }

    fun acceptCurrentTerms() {
        val userId = _currentUserId ?: return
        prefs.edit().putString(termsKey(userId), CURRENT_TERMS_VERSION).apply()
    }

    private fun termsKey(userId: Int): String = "terms_version_user_$userId"
}
