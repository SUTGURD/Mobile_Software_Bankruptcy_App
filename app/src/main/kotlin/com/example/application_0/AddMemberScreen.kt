package com.example.application_0

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun AddMemberScreen(
    onNavigate: (AppRoute) -> Unit,
    onBack: () -> Unit,
    backEnabled: Boolean
) {
    val groupsViewModel: GroupsViewModel = viewModel()
    val groupsState by groupsViewModel.uiState.collectAsState()
    val activeGroupId by ActiveGroup.groupId.collectAsState()
    val group = groupsState.groups.firstOrNull { it.id == activeGroupId }
        ?: groupsState.groups.firstOrNull()
    var email by remember { mutableStateOf("jason.manden@email.com") }

    ScreenLayout(
        title = "Add member",
        showBottomBar = true,
        bottomBarDestination = AppRoute.Groups,
        onNavigate = onNavigate,
        onBack = onBack,
        backEnabled = backEnabled,
        bottomContent = {
            ActionButton(
                "Send invitation",
                enabled = email.contains('@'),
                onClick = { onNavigate(AppRoute.Members) }
            )
        }
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        SectionTitle("Invite debtor")
        Spacer(modifier = Modifier.height(52.dp))
        ScreenMessage("Invite someone to ${group?.name ?: "your group"}.")
        Spacer(modifier = Modifier.height(34.dp))
        FormField(
            label = "Friend's email",
            value = email,
            onValueChange = { email = it },
            placeholder = "name@example.com"
        )
        Spacer(modifier = Modifier.height(36.dp))
        Card(color = SoftGreen, contentPadding = 18.dp) {
            androidx.compose.material3.Text(
                text = email.substringBefore('@')
                    .split('.', ' ')
                    .joinToString(" ") { it.replaceFirstChar { character -> character.uppercase() } }
                    .ifBlank { "New member" },
                style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                color = ForestGreen
            )
            Spacer(modifier = Modifier.height(10.dp))
            androidx.compose.material3.Text(
                text = "They will receive an invitation to join your group. Existing expenses will not be changed.",
                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                color = MutedGreen
            )
        }
    }
}
