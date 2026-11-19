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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun GroupInvitationsScreen(
    onNavigate: (AppRoute) -> Unit,
    onBack: () -> Unit,
    backEnabled: Boolean,
    selectedMenu: AppRoute,
    invitationsViewModel: GroupInvitationsViewModel
) {
    val state by invitationsViewModel.uiState.collectAsState()

    ScreenLayout(
        title = "Group invitations",
        showBottomBar = true,
        bottomBarDestination = selectedMenu,
        onNavigate = onNavigate,
        onBack = onBack,
        backEnabled = backEnabled
    ) {
        Spacer(modifier = Modifier.height(120.dp))
        ScreenDataStatus(state.isLoading, state.error)
        when {
            state.isLoading || state.error != null -> Unit
            state.invitations.isEmpty() -> ScreenMessage("You have no group invitations.")
            else -> Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                state.invitations.forEach { invitation ->
                    NotificationCard(
                        notification = invitation,
                        onClick = {
                            if (invitation.type == types.NotificationType.INVITATIONS) {
                                invitation.relatedEntityId?.let(invitationsViewModel::selectInvitation)
                            } else {
                                invitation.groupId?.let(ActiveGroup::select)
                            }
                            onNavigate(invitation.destination ?: AppRoute.ManageGroupInvitation)
                        },
                        testTag = "group_invitation_${invitation.relatedEntityId}"
                    )
                }
            }
        }
    }
}

@Composable
fun ManageGroupInvitationScreen(
    onNavigate: (AppRoute) -> Unit,
    onBack: () -> Unit,
    backEnabled: Boolean,
    selectedMenu: AppRoute,
    invitationsViewModel: GroupInvitationsViewModel,
    notificationsViewModel: NotificationsViewModel,
    groupsViewModel: GroupsViewModel
) {
    val state by invitationsViewModel.uiState.collectAsState()
    val invitation = state.selectedInvitation
    val coroutineScope = rememberCoroutineScope()

    ScreenLayout(
        title = "Group invitation",
        showBottomBar = true,
        bottomBarDestination = selectedMenu,
        onNavigate = onNavigate,
        onBack = onBack,
        backEnabled = backEnabled,
        bottomContent = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                ActionButton(
                    label = "Accept invitation",
                    enabled = invitation != null && !state.isSaving,
                    onClick = {
                        coroutineScope.launch {
                            val result = invitationsViewModel.resolveInvitation(accept = true)
                            if (result == data.database.MembershipMutationResult.SUCCESS) {
                                withContext(Dispatchers.Main.immediate) {
                                    notificationsViewModel.refresh()
                                    invitationsViewModel.refresh()
                                    groupsViewModel.refresh()
                                    onBack()
                                }
                            }
                        }
                    }
                )
                ActionButton(
                    label = "Dismiss invitation",
                    secondary = true,
                    enabled = invitation != null && !state.isSaving,
                    onClick = {
                        coroutineScope.launch {
                            val result = invitationsViewModel.resolveInvitation(accept = false)
                            if (result == data.database.MembershipMutationResult.SUCCESS) {
                                withContext(Dispatchers.Main.immediate) {
                                    notificationsViewModel.refresh()
                                    invitationsViewModel.refresh()
                                    onBack()
                                }
                            }
                        }
                    }
                )
            }
        }
    ) {
        Spacer(modifier = Modifier.height(36.dp))
        ScreenDataStatus(state.isLoading, state.error)
        if (invitation != null) {
            SectionTitle("You are invited to join")
            Spacer(modifier = Modifier.height(12.dp))
            Card(contentPadding = 18.dp) {
                Text(
                    text = invitation.groupName,
                    style = MaterialTheme.typography.titleLarge,
                    color = ForestGreen
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Invited by ${invitation.inviterName}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MutedGreen
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Accepting adds you as a member of this group. Your existing expenses will not change.",
                style = MaterialTheme.typography.bodyMedium,
                color = MutedGreen
            )
        } else if (!state.isLoading && state.error == null) {
            ScreenMessage("This invitation is no longer available.")
        }
        if (state.error != null) {
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = state.error.orEmpty(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}
