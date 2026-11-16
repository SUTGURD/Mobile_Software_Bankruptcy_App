package com.example.application_0

import data.DummyData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import types.NotificationSettings

data class UserProfile(
    val id: String,
    val name: String,
    val email: String,
    val phoneNumber: String?,
    val notificationsEnabled: Boolean,
    val notificationSettings: NotificationSettings
)

data class UserProfileUiState(
    val profile: UserProfile? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)

class UserProfileViewModel : AuthenticatedViewModel() {
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

        val profile = DummyData.users.firstOrNull { it.id == user.userId }
        if (profile == null) {
            _uiState.value = UserProfileUiState(
                isLoading = false,
                error = "Authenticated user was not found."
            )
            return
        }

        _uiState.value = UserProfileUiState(
            profile = UserProfile(
                id = profile.id,
                name = profile.name,
                email = profile.email,
                phoneNumber = profile.phoneNumber,
                notificationsEnabled = profile.notificationsEnabled,
                notificationSettings = profile.notificationSettings
            ),
            isLoading = false
        )
    }
}
