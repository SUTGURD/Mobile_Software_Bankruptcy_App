package com.example.application_0

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.time.LocalDate
import types.NotificationType

@Composable
fun NotificationsScreen(
    onNavigate: (AppRoute) -> Unit,
    onBack: () -> Unit,
    backEnabled: Boolean,
    selectedMenu: AppRoute
) {
    val viewModel: NotificationsViewModel = viewModel()
    val state by viewModel.uiState.collectAsState()

    ScreenLayout(
        title = "Notifications",
        showBottomBar = true,
        bottomBarDestination = selectedMenu,
        onNavigate = onNavigate,
        onBack = onBack,
        backEnabled = backEnabled,
        bottomContent = {
            ActionButton(
                "Notification settings",
                secondary = true,
                onClick = { onNavigate(AppRoute.NotificationsSettings) }
            )
        }
    ) {
        Spacer(modifier = Modifier.height(120.dp))
        ScreenDataStatus(state.isLoading, state.error)
        when {
            state.isLoading || state.error != null -> Unit
            state.notifications.isEmpty() -> ScreenMessage("You have no notifications.")
            else -> Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                state.notifications.forEach { notification ->
                    val destination = notification.destination
                    val groupId = notification.groupId
                    val openSubject = if (destination != null && groupId != null) {
                        {
                            ActiveGroup.select(groupId)
                            onNavigate(destination)
                        }
                    } else {
                        null
                    }
                    Card(
                        modifier = Modifier
                            .clickable(
                                enabled = openSubject != null,
                                onClick = { openSubject?.invoke() }
                            )
                            .testTag("notification_${notification.id}")
                    ) {
                        Text(
                            notificationTitle(notification.type),
                            style = MaterialTheme.typography.titleMedium,
                            color = ForestGreen,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            notification.message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MutedGreen
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                notificationDate(notification.createdAt.toLocalDate()),
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.labelSmall,
                                color = ForestGreen
                            )
                            if (openSubject == null) {
                                Text(
                                    "Related content unavailable",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = ForestGreen
                                )
                            } else {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        "View",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = ForestGreen
                                    )
                                    Spacer(modifier = Modifier.size(4.dp))
                                    Icon(
                                        painter = painterResource(R.drawable.ic_arrow_forward),
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp),
                                        tint = ForestGreen
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun notificationTitle(type: NotificationType): String = when (type) {
    NotificationType.EXPENSE -> "Dinner’s on the list"
    NotificationType.GROUP -> "The gang’s all here"
    NotificationType.PAYMENT -> "Payment received"
    NotificationType.REMINDER -> "Payment reminder"
}

private fun notificationDate(date: LocalDate): String = when (date) {
    LocalDate.now().minusDays(1) -> "Yesterday"
    else -> date.format(java.time.format.DateTimeFormatter.ofPattern("d MMM", java.util.Locale.UK))
}
