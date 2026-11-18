package com.example.application_0

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun MembersScreen(onNavigate: (AppRoute) -> Unit, onBack: () -> Unit, backEnabled: Boolean) {
    val viewModel: GroupsViewModel = viewModel()
    val state by viewModel.uiState.collectAsState()
    val activeGroupId by ActiveGroup.groupId.collectAsState()
    val group = state.groups.firstOrNull { it.id == activeGroupId } ?: state.groups.firstOrNull()
    val userId = Authentication.currentUser.collectAsState().value?.userId

    ScreenLayout(
        title = "Members",
        showBottomBar = true,
        bottomBarDestination = AppRoute.Groups,
        onNavigate = onNavigate,
        onBack = onBack,
        backEnabled = backEnabled,
        bottomContent = {
            if (group != null) {
                ActionButton(
                    "Invite a friend",
                    leadingIcon = R.drawable.ic_add,
                    onClick = { onNavigate(AppRoute.ManageMembers) }
                )
            }
        }
    ) {
        Spacer(modifier = Modifier.height(112.dp))
        Text(
            group?.let { "${it.name} · ${it.memberCount} active members" } ?: "Group members",
            style = MaterialTheme.typography.bodyMedium,
            color = MutedGreen
        )
        Spacer(modifier = Modifier.height(18.dp))
        ScreenDataStatus(state.isLoading, state.error)
        when {
            group == null && !state.isLoading && state.error == null ->
                ScreenMessage("You are not a member of any groups.")
            group != null -> {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    group.members.forEach { member ->
                        Card {
                            Column {
                                Text(
                                    member.name,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = ForestGreen
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    if (member.userId == userId) "You · Group ${member.memberStatus.name.lowercase()}"
                                    else member.memberStatus.name.lowercase().replaceFirstChar { it.uppercase() },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MutedGreen
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
