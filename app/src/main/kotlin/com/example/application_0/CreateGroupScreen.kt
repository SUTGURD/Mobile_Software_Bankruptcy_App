package com.example.application_0

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CreateGroupScreen(onNavigate: (AppRoute) -> Unit, onBack: () -> Unit, backEnabled: Boolean) {
    var groupName by remember { mutableStateOf("Friday dinner") }
    var groupType by remember { mutableStateOf("Dinner") }
    var showUnavailableMessage by remember { mutableStateOf(false) }

    ScreenLayout(
        title = "Create group",
        showBottomBar = true,
        bottomBarDestination = AppRoute.Groups,
        onNavigate = onNavigate,
        onBack = onBack,
        backEnabled = backEnabled,
        bottomContent = {
            ActionButton(
                "Create group",
                enabled = groupName.isNotBlank() && groupType.isNotBlank(),
                onClick = { showUnavailableMessage = true }
            )
        }
    ) {
        Spacer(modifier = Modifier.height(112.dp))
        FormField(
            label = "Group name",
            value = groupName,
            onValueChange = { groupName = it },
            placeholder = "Friday dinner"
        )
        Spacer(modifier = Modifier.height(14.dp))
        FormField(
            label = "Group type",
            value = groupType,
            onValueChange = { groupType = it },
            placeholder = "Dinner"
        )
        Spacer(modifier = Modifier.height(14.dp))
        androidx.compose.material3.Text(
            text = "Currency",
            style = androidx.compose.material3.MaterialTheme.typography.labelMedium,
            color = MutedGreen
        )
        Spacer(modifier = Modifier.height(6.dp))
        Card(contentPadding = 14.dp) {
            androidx.compose.material3.Text(
                text = "DKK · Danish krone",
                color = ForestGreen
            )
        }
        if (showUnavailableMessage) {
            Spacer(modifier = Modifier.height(16.dp))
            ScreenMessage("Creating groups is not available while using dummy data.")
        }
    }
}
