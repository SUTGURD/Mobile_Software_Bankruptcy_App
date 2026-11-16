package com.example.application_0

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ManageMembersScreen(onBack: () -> Unit, backEnabled: Boolean) {
    MockupScreenLayout(
        title = "Manage members",
        subtitle = "Copenhagen Weekend · 4 members",
        onBack = onBack,
        backEnabled = backEnabled
    ) {
        MockupSectionTitle("Add a member")
        Spacer(modifier = Modifier.height(12.dp))
        MockupField("Email address", "name@example.com")
        Spacer(modifier = Modifier.height(12.dp))
        MockupAction("Add member")
        Spacer(modifier = Modifier.height(24.dp))
        MockupSectionTitle("Current members")
        Spacer(modifier = Modifier.height(12.dp))
        MockupCard {
            Column(verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(18.dp)) {
                MockupInfoRow("Alex Morgan", "You · Group admin", "ADMIN")
                MockupInfoRow("Mia Jensen", "Member", "REMOVE")
                MockupInfoRow("Noah Larsen", "Member", "REMOVE")
                MockupInfoRow("Sofie Holm", "Member", "REMOVE")
            }
        }
    }
}
