package com.example.application_0

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MembersScreen(onNavigate: (AppRoute) -> Unit, onBack: () -> Unit, backEnabled: Boolean) {
    MockupScreenLayout(
        title = "Members",
        subtitle = "People in Copenhagen Weekend.",
        showBottomBar = true,
        bottomBarDestination = AppRoute.Groups,
        onNavigate = onNavigate,
        onBack = onBack,
        backEnabled = backEnabled
    ) {
        MockupCard {
            Column(verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(18.dp)) {
                MockupInfoRow("Alex Morgan", "You · Joined 12 May")
                MockupInfoRow("Mia Jensen", "Joined 12 May")
                MockupInfoRow("Noah Larsen", "Joined 13 May")
                MockupInfoRow("Sofie Holm", "Joined 13 May")
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        MockupAction("Manage members", onClick = { onNavigate(AppRoute.ManageMembers) })
    }
}
