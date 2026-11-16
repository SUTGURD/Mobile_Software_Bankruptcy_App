package com.example.application_0

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.unit.dp

@Composable
fun NotificationsScreen(
    onNavigate: (AppRoute) -> Unit,
    onBack: () -> Unit,
    backEnabled: Boolean
) {
    val viewModel: NotificationsViewModel = viewModel()
    val state by viewModel.uiState.collectAsState()

    MockupScreenLayout(
        title = "Notifications",
        subtitle = "Updates addressed to your account.",
        showBottomBar = true,
        bottomBarDestination = AppRoute.Notifications,
        onNavigate = onNavigate,
        onBack = onBack,
        backEnabled = backEnabled
    ) {
        ScreenDataStatus(state.isLoading, state.error)
        when {
            state.isLoading || state.error != null -> Unit
            state.notifications.isEmpty() -> ScreenMessage("You have no notifications.")
            else -> MockupCard {
                Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                    state.notifications.forEach { notification ->
                        MockupInfoRow(
                            title = notification.type.name.lowercase()
                                .replace('_', ' ')
                                .replaceFirstChar { it.uppercase() },
                            detail = notification.message,
                            trailing = if (notification.read) "Read" else "Unread",
                            accent = !notification.read
                        )
                    }
                }
            }
        }
    }
}
