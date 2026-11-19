package com.example.application_0

import data.database.DatabaseProvider
import domain.verifyPassword
import domain.hashPassword
import java.time.LocalDateTime
import java.util.UUID
import types.NotificationSettings
import types.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.StateFlow

data class AuthenticatedUser(val userId: String)

enum class AccountCreationResult {
    CREATED,
    INVALID_DETAILS,
    EMAIL_EXISTS,
    DATABASE_ERROR
}

enum class LoginResult {
    AUTHENTICATED,
    DATABASE_RESET,
    INVALID_CREDENTIALS
}

internal fun isDatabaseResetCredentials(email: String, password: String): Boolean =
    email.trim() == "RESETDATABASE" && password == "RESETDATABASE"

object Authentication {
    private val _currentUser = MutableStateFlow<AuthenticatedUser?>(null)

    val currentUser: StateFlow<AuthenticatedUser?> = _currentUser.asStateFlow()

    suspend fun authenticate(email: String, password: String): LoginResult {
        if (isDatabaseResetCredentials(email, password)) {
            logout()
            return if (DatabaseProvider.current().resetDatabaseToDummyData()) {
                LoginResult.DATABASE_RESET
            } else {
                LoginResult.INVALID_CREDENTIALS
            }
        }

        val user = DatabaseProvider.current().getUserByEmail(email.trim())
            ?: return LoginResult.INVALID_CREDENTIALS

        if (!verifyPassword(password, user.passwordHash)) {
            return LoginResult.INVALID_CREDENTIALS
        }

        _currentUser.value = AuthenticatedUser(user.id)
        return LoginResult.AUTHENTICATED
    }

    fun logout() {
        _currentUser.value = null
    }

    suspend fun createAccount(name: String, email: String, password: String): AccountCreationResult {
        val trimmedName = name.trim()
        val trimmedEmail = email.trim().lowercase()
        if (trimmedName.length < 2 ||
            password.length < 8 ||
            !Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+\$").matches(trimmedEmail)
        ) {
            return AccountCreationResult.INVALID_DETAILS
        }
        if (DatabaseProvider.current().getUserByEmail(trimmedEmail) != null) {
            return AccountCreationResult.EMAIL_EXISTS
        }

        val now = LocalDateTime.now()
        val user = User(
            id = "user-${UUID.randomUUID()}",
            name = trimmedName,
            email = trimmedEmail,
            phoneNumber = null,
            profileImageUri = null,
            createdAt = now,
            updatedAt = now,
            notificationsEnabled = true,
            notificationSettings = NotificationSettings(
                expenses = true,
                payments = true,
                reminders = true,
                groupActivity = true
            ),
            passwordHash = hashPassword(password)
        )
        return if (DatabaseProvider.current().createUser(user)) {
            _currentUser.value = AuthenticatedUser(user.id)
            AccountCreationResult.CREATED
        } else {
            AccountCreationResult.DATABASE_ERROR
        }
    }
}
