package com.example.application_0

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
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

    MockupScreenLayout(
        title = "Members",
        subtitle = group?.let { "${it.name} · ${it.memberCount} members" } ?: "Members in your groups.",
        showBottomBar = true,
        bottomBarDestination = AppRoute.Groups,
        onNavigate = onNavigate,
        onBack = onBack,
        backEnabled = backEnabled
    ) {
        ScreenDataStatus(state.isLoading, state.error)
        when {
            group == null && !state.isLoading && state.error == null ->
                ScreenMessage("You are not a member of any groups.")
            group != null -> {
                MockupSectionTitle("Group members")
                Spacer(modifier = Modifier.height(12.dp))
                MockupCard {
                    Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
                        group.members.forEach { member ->
                            MockupInfoRow(
                                title = member.name,
                                detail = "${member.email} · Joined ${formatDate(member.joinedAt)}",
                                trailing = if (member.userId == userId) {
                                    "You · ${member.memberStatus}"
                                } else {
                                    member.memberStatus.name
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                MockupAction("Manage members", onClick = { onNavigate(AppRoute.ManageMembers) })
            }
        }
    }
}
