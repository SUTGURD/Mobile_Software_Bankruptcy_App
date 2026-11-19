package com.example.application_0

import data.database.DatabaseProvider
import domain.verifyPassword
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.StateFlow

data class AuthenticatedUser(val userId: String)

object Authentication {
    private val _currentUser = MutableStateFlow<AuthenticatedUser?>(null)

    val currentUser: StateFlow<AuthenticatedUser?> = _currentUser.asStateFlow()

    suspend fun authenticate(email: String, password: String): Boolean {
        val user = DatabaseProvider.current().getUserByEmail(email.trim())
            ?: return false

        if (!verifyPassword(password, user.passwordHash)) return false

        _currentUser.value = AuthenticatedUser(user.id)
        return true
    }

    fun logout() {
        _currentUser.value = null
    }
}
