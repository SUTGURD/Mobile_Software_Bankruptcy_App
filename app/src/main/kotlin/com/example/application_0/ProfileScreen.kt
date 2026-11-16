package com.example.application_0

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ProfileScreen(onNavigate: (AppRoute) -> Unit, onBack: () -> Unit, backEnabled: Boolean) {
    val viewModel: UserProfileViewModel = viewModel()
    val state by viewModel.uiState.collectAsState()
    val profile = state.profile

    MockupScreenLayout(
        title = "Profile",
        subtitle = "Your account details and preferences.",
        showBottomBar = true,
        bottomBarDestination = AppRoute.Profile,
        onNavigate = onNavigate,
        onBack = onBack,
        backEnabled = backEnabled
    ) {
        ScreenDataStatus(state.isLoading, state.error)
        if (profile != null) {
            MockupCard {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    BrandMark(modifier = Modifier.size(72.dp))
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = profile.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(profile.email, style = MaterialTheme.typography.bodyMedium)
                    Text("User ID: ${profile.id}", style = MaterialTheme.typography.bodySmall)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            MockupCard {
                Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
                    MockupInfoRow("Phone number", profile.phoneNumber ?: "Not provided")
                    MockupInfoRow(
                        "Notifications",
                        if (profile.notificationsEnabled) "Enabled" else "Disabled"
                    )
                    MockupInfoRow(
                        "Expense notifications",
                        if (profile.notificationSettings.expenses) "Enabled" else "Disabled"
                    )
                    MockupInfoRow(
                        "Payment notifications",
                        if (profile.notificationSettings.payments) "Enabled" else "Disabled"
                    )
                    MockupInfoRow(
                        "Reminder notifications",
                        if (profile.notificationSettings.reminders) "Enabled" else "Disabled"
                    )
                    MockupInfoRow(
                        "Group activity notifications",
                        if (profile.notificationSettings.groupActivity) "Enabled" else "Disabled"
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            MockupAction(
                "Log out",
                secondary = true,
                onClick = {
                    Authentication.logout()
                    ActiveGroup.clear()
                    onNavigate(AppRoute.Entry)
                }
            )
        }
    }
}
