package com.example.application_0

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

@Composable
fun NotificationsScreen(
    onNavigate: (AppRoute) -> Unit,
    onBack: () -> Unit,
    backEnabled: Boolean
) {
    MockupScreenLayout(
        title = "Notifications",
        subtitle = "Updates from your groups and shared expenses.",
        showBottomBar = true,
        bottomBarDestination = AppRoute.Notifications,
        onNavigate = onNavigate,
        onBack = onBack,
        backEnabled = backEnabled
    ) {
        MockupCard {
            Column(verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(20.dp)) {
                MockupInfoRow("New expense added", "Mia added Dinner · 10 minutes ago", "NEW", accent = true)
                MockupInfoRow("Payment recorded", "Noah settled a balance · Yesterday", "NEW", accent = true)
                MockupInfoRow("Group invitation", "You were added to Summer trip · Monday")
            }
        }
    }
}
