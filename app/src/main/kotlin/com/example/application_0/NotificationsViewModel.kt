package com.example.application_0

import data.DummyData
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
                val groupId = when (notification.type) {
                    NotificationType.EXPENSE -> DummyData.expenses
                        .firstOrNull { it.id == notification.relatedEntityId }?.groupId
                    NotificationType.PAYMENT -> DummyData.payments
                        .firstOrNull { it.id == notification.relatedEntityId }?.groupId
                    NotificationType.REMINDER -> DummyData.reminders
                        .firstOrNull { it.id == notification.relatedEntityId }?.groupId
                    NotificationType.GROUP -> DummyData.groups
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
            notifications = notifications,
            isLoading = false
        )
    }
}
