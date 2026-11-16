package com.example.application_0

import data.DummyData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import types.NotificationType

data class NotificationSummary(
    val id: String,
    val type: NotificationType,
    val message: String,
    val read: Boolean
)

data class NotificationsUiState(
    val notifications: List<NotificationSummary> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

class NotificationsViewModel : AuthenticatedViewModel() {
    private val _uiState = MutableStateFlow(NotificationsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        observeAuthenticatedUser(::loadNotifications)
    }

    private fun loadNotifications(user: AuthenticatedUser?) {
        _uiState.update { it.copy(isLoading = true, error = null) }
        if (user == null) {
            _uiState.value = NotificationsUiState(
                isLoading = false,
                error = "No authenticated user."
            )
            return
        }

        val notifications = DummyData.notifications
            .filter { it.userId == user.userId }
            .map { notification ->
                NotificationSummary(
                    id = notification.id,
                    type = notification.type,
                    message = notification.message,
                    read = notification.read
                )
            }
        _uiState.value = NotificationsUiState(
            notifications = notifications,
            isLoading = false
        )
    }
}
