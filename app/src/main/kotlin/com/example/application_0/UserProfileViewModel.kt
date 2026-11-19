package com.example.application_0

import data.database.DatabaseOperations
import data.database.DatabaseProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import types.NotificationSettings
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

data class UserProfile(
    val id: String,
    val name: String,
    val email: String,
    val phoneNumber: String?,
    val profileImageUri: String?,
    val memberSince: String,
    val notificationsEnabled: Boolean,
    val notificationSettings: NotificationSettings
)

private val memberSinceFormatter = DateTimeFormatter.ofPattern("MMM yyyy", Locale.UK)

data class UserProfileUiState(
    val profile: UserProfile? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)

class UserProfileViewModel(
    private val database: DatabaseOperations = DatabaseProvider.current()
) : AuthenticatedViewModel() {
    private val _uiState = MutableStateFlow(UserProfileUiState())
    val uiState = _uiState.asStateFlow()

    init {
        observeAuthenticatedUser(::loadProfile)
    }

    private fun loadProfile(user: AuthenticatedUser?) {
        _uiState.update { it.copy(isLoading = true, error = null) }
        if (user == null) {
            _uiState.value = UserProfileUiState(
                isLoading = false,
                error = "No authenticated user."
            )
            return
        }

        viewModelScope.launch {
            try {
                val profile = database.getUserById(user.userId)
                if (profile == null) {
                    _uiState.value = UserProfileUiState(
                        isLoading = false,
                        error = "Authenticated user was not found."
                    )
                    return@launch
                }

                _uiState.value = UserProfileUiState(
                    profile = UserProfile(
                        id = profile.id,
                        name = profile.name,
                        email = profile.email,
                        phoneNumber = profile.phoneNumber,
                        profileImageUri = profile.profileImageUri,
                        memberSince = profile.createdAt.format(memberSinceFormatter),
                        notificationsEnabled = profile.notificationsEnabled,
                        notificationSettings = profile.notificationSettings
                    ),
                    isLoading = false
                )
            } catch (_: Exception) {
                _uiState.value = UserProfileUiState(
                    isLoading = false,
                    error = "Profile data could not be loaded."
                )
            }
        }
    }

    suspend fun updateProfile(
        name: String,
        email: String,
        phoneNumber: String?,
        profileImageUri: String?
    ): Boolean {
        val userId = Authentication.currentUser.value?.userId ?: return false
        val existing = database.getUserById(userId) ?: return false
        val updated = existing.copy(
            name = name.trim(),
            email = email.trim().lowercase(),
            phoneNumber = phoneNumber?.trim()?.ifEmpty { null },
            profileImageUri = profileImageUri,
            updatedAt = LocalDateTime.now()
        )
        val updatedSuccessfully = database.updateUser(updated)
        if (updatedSuccessfully) loadProfile(Authentication.currentUser.value)
        return updatedSuccessfully
    }

    suspend fun updateNotificationSettings(
        notificationsEnabled: Boolean,
        notificationSettings: NotificationSettings
    ): Boolean {
        val userId = Authentication.currentUser.value?.userId ?: return false
        val existing = database.getUserById(userId) ?: return false
        val updated = existing.copy(
            notificationsEnabled = notificationsEnabled,
            notificationSettings = notificationSettings,
            updatedAt = LocalDateTime.now()
        )
        val updatedSuccessfully = database.updateUser(updated)
        if (updatedSuccessfully) loadProfile(Authentication.currentUser.value)
        return updatedSuccessfully
    }
}
