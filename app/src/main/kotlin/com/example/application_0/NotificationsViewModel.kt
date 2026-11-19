package com.example.application_0

import data.database.DatabaseOperations
import data.database.DatabaseProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDateTime
import types.NotificationType

data class NotificationSummary(
    val id: String,
    val type: NotificationType,
    val message: String,
    val read: Boolean,
    val createdAt: LocalDateTime,
    val relatedEntityId: String?,
    val groupId: String?,
    val destination: AppRoute?
)

data class NotificationsUiState(
    val notifications: List<NotificationSummary> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

class NotificationsViewModel(
    private val database: DatabaseOperations = DatabaseProvider.current()
) : AuthenticatedViewModel() {
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

        viewModelScope.launch {
            try {
                val notifications = database.getNotifications()
                    .filter { it.userId == user.userId }
                val expenses = database.getExpenses()
                val payments = database.getPayments()
                val reminders = database.getReminders()
                val groups = database.getGroups()
                val summaries = notifications.map { notification ->
                    val groupId = when (notification.type) {
                        NotificationType.EXPENSE -> expenses
                            .firstOrNull { it.id == notification.relatedEntityId }?.groupId
                        NotificationType.PAYMENT -> payments
                            .firstOrNull { it.id == notification.relatedEntityId }?.groupId
                        NotificationType.REMINDER -> reminders
                            .firstOrNull { it.id == notification.relatedEntityId }?.groupId
                        NotificationType.GROUP -> groups
                            .firstOrNull { it.id == notification.relatedEntityId }?.id
                    }
                    val destination = when {
                        groupId == null -> null
                        notification.type == NotificationType.EXPENSE ||
                            notification.type == NotificationType.REMINDER -> AppRoute.Expenses
                        notification.type == NotificationType.PAYMENT -> AppRoute.DebtSummary
                        notification.type == NotificationType.GROUP -> AppRoute.GroupDetails
                        else -> AppRoute.GroupDetails
                    }
                    NotificationSummary(
                        id = notification.id,
                        type = notification.type,
                        message = notification.message,
                        read = notification.read,
                        createdAt = notification.createdAt,
                        relatedEntityId = notification.relatedEntityId,
                        groupId = groupId,
                        destination = destination
                    )
                }
                _uiState.value = NotificationsUiState(
                    notifications = summaries,
                    isLoading = false
                )
            } catch (_: Exception) {
                _uiState.value = NotificationsUiState(
                    isLoading = false,
                    error = "Notification data could not be loaded."
                )
            }
        }
    }
}
