package com.example.application_0

import data.DummyData
import domain.verifyPassword
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.StateFlow

data class AuthenticatedUser(val userId: String)

object Authentication {
    private val _currentUser = MutableStateFlow<AuthenticatedUser?>(null)

    val currentUser: StateFlow<AuthenticatedUser?> = _currentUser.asStateFlow()

    fun authenticate(email: String, password: String): Boolean {
        val user = DummyData.users.firstOrNull {
            it.email.equals(email.trim(), ignoreCase = true)
        } ?: return false

        if (!verifyPassword(password, user.passwordHash)) return false

        _currentUser.value = AuthenticatedUser(user.id)
        return true
    }

    fun logout() {
        _currentUser.value = null
    }
}
