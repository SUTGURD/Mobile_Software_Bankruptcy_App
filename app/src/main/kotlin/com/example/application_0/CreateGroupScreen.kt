package com.example.application_0

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CreateGroupScreen(onNavigate: (AppRoute) -> Unit, onBack: () -> Unit, backEnabled: Boolean) {
    MockupScreenLayout(
        title = "Create group",
        subtitle = "Give your shared space a name.",
        onBack = onBack,
        backEnabled = backEnabled
    ) {
        MockupField("Group name", "For example, Weekend trip")
        Spacer(modifier = Modifier.height(16.dp))
        MockupField("Description", "What is this group for?")
        Spacer(modifier = Modifier.height(24.dp))
        MockupCard {
            MockupInfoRow("Split expenses equally", "You can choose who shares each expense.")
        }
        Spacer(modifier = Modifier.height(24.dp))
        MockupAction("Create group", onClick = { onNavigate(AppRoute.GroupDetails) })
    }
}
