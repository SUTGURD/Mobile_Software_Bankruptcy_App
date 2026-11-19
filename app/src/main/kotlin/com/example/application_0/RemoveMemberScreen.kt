package com.example.application_0

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import data.database.MembershipMutationResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun RemoveMemberScreen(
    onNavigate: (AppRoute) -> Unit,
    onBack: () -> Unit,
    backEnabled: Boolean,
    memberViewModel: MemberManagementViewModel,
    groupsViewModel: GroupsViewModel,
    invitationsViewModel: GroupInvitationsViewModel,
    notificationsViewModel: NotificationsViewModel
) {
    val groupsState by groupsViewModel.uiState.collectAsState()
    val activeGroupId by ActiveGroup.groupId.collectAsState()
    val group = groupsState.groups.firstOrNull { it.id == activeGroupId }
        ?: groupsState.groups.firstOrNull()
    val selectedMember by memberViewModel.selectedMember.collectAsState()
    val target = selectedMember ?: group?.invitations?.firstOrNull()?.let {
        MemberActionTarget(it.name, it.email, true)
    } ?: group?.members?.firstOrNull()?.let {
        MemberActionTarget(it.name, it.email, false)
    }
    val invitation = target?.isInvitation ?: true
    val noun = if (invitation) "invitation" else "member"
    var error by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    ScreenLayout(
        title = "Remove member",
        showBottomBar = true,
        bottomBarDestination = AppRoute.Groups,
        onNavigate = onNavigate,
        onBack = onBack,
        backEnabled = backEnabled,
        bottomContent = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                ActionButton(
                    "Keep $noun",
                    secondary = true,
                    onClick = {
                        memberViewModel.clearMember()
                        onBack()
                    }
                )
                ActionButton(
                    "Remove $noun",
                    danger = true,
                    enabled = target != null,
                    onClick = {
                        val selectedTarget = target ?: return@ActionButton
                        coroutineScope.launch {
                            val result = if (selectedTarget.isInvitation) {
                                selectedTarget.invitationId?.let {
                                    invitationsViewModel.removeInvitation(it)
                                } ?: MembershipMutationResult.NOT_FOUND
                            } else {
                                val groupId = selectedTarget.groupId
                                val memberUserId = selectedTarget.memberUserId
                                if (groupId == null || memberUserId == null) {
                                    MembershipMutationResult.NOT_FOUND
                                } else {
                                    invitationsViewModel.removeMember(groupId, memberUserId)
                                }
                            }
                            withContext(Dispatchers.Main.immediate) {
                                if (result == MembershipMutationResult.SUCCESS) {
                                    error = null
                                    memberViewModel.clearMember()
                                    groupsViewModel.refresh()
                                    notificationsViewModel.refresh()
                                    onBack()
                                } else {
                                    error = when (result) {
                                        MembershipMutationResult.NOT_ALLOWED ->
                                            "You do not have permission to remove this $noun."
                                        MembershipMutationResult.NOT_FOUND ->
                                            "This $noun is no longer available."
                                        else -> "This $noun could not be removed."
                                    }
                                }
                            }
                        }
                    }
                )
            }
        }
    ) {
        Spacer(modifier = Modifier.height(36.dp))
        SectionTitle(
            if (invitation) "Cancel ${target?.name?.substringBefore(' ')}'s invitation?"
            else "Remove ${target?.name ?: "member"}?"
        )
        Spacer(modifier = Modifier.height(10.dp))
        androidx.compose.material3.Text(
            text = group?.name ?: "Group",
            style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
            color = MutedGreen
        )
        Spacer(modifier = Modifier.height(44.dp))
        Card(color = Color(0xFFF6ECE4), contentPadding = 18.dp) {
            androidx.compose.material3.Text(
                text = target?.name ?: "Member",
                style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                color = ForestGreen
            )
            Spacer(modifier = Modifier.height(8.dp))
            androidx.compose.material3.Text(
                text = target?.email.orEmpty(),
                style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                color = MutedGreen
            )
            Spacer(modifier = Modifier.height(18.dp))
            androidx.compose.material3.Text(
                text = if (invitation) {
                    "This removes the pending invitation. No balances or expenses will change."
                } else {
                    "This removes the member. Existing expenses and balances will not change."
                },
                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                color = MutedGreen
            )
        }
        error?.let { message ->
            Spacer(modifier = Modifier.height(14.dp))
            androidx.compose.material3.Text(
                text = message,
                style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                color = androidx.compose.material3.MaterialTheme.colorScheme.error
            )
        }
    }
}
