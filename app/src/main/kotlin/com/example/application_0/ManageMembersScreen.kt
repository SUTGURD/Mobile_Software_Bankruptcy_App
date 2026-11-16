package com.example.application_0

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ManageMembersScreen(onBack: () -> Unit, backEnabled: Boolean) {
    val viewModel: GroupsViewModel = viewModel()
    val state by viewModel.uiState.collectAsState()
    val activeGroupId by ActiveGroup.groupId.collectAsState()
    val group = state.groups.firstOrNull { it.id == activeGroupId } ?: state.groups.firstOrNull()
    val userId = Authentication.currentUser.collectAsState().value?.userId

    MockupScreenLayout(
        title = "Manage members",
        subtitle = group?.let { "${it.name} · ${it.memberCount} members" } ?: "Group membership",
        onBack = onBack,
        backEnabled = backEnabled
    ) {
        ScreenDataStatus(state.isLoading, state.error)
        when {
            group == null && !state.isLoading && state.error == null ->
                ScreenMessage("You are not a member of any groups.")
            group != null -> {
                MockupSectionTitle("Current members")
                MockupCard {
                    Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
                        group.members.forEach { member ->
                            MockupInfoRow(
                                title = member.name,
                                detail = member.email,
                                trailing = if (member.userId == userId) {
                                    "You · ${member.memberStatus}"
                                } else {
                                    member.memberStatus.name
                                }
                            )
                        }
                    }
                }
                ScreenMessage(
                    "Member changes are not available while using dummy data."
                )
            }
        }
    }
}
