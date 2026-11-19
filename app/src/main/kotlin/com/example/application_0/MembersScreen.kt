package com.example.application_0

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import types.MemberRole

@Composable
fun MembersScreen(
    onNavigate: (AppRoute) -> Unit,
    onBack: () -> Unit,
    backEnabled: Boolean,
    memberViewModel: MemberManagementViewModel,
    groupsViewModel: GroupsViewModel
) {
    val state by groupsViewModel.uiState.collectAsState()
    val activeGroupId by ActiveGroup.groupId.collectAsState()
    val group = state.groups.firstOrNull { it.id == activeGroupId } ?: state.groups.firstOrNull()
    val userId = Authentication.currentUser.collectAsState().value?.userId
    val isAdmin = group?.memberStatus == MemberRole.ADMIN
    val canInvite = group != null && (isAdmin || group.allowAllMembersToAdd)
    val canRemoveMembers = group != null && (isAdmin || group.allowAllMembersToRemove)

    ScreenLayout(
        title = "Members",
        showBottomBar = true,
        bottomBarDestination = AppRoute.Groups,
        onNavigate = onNavigate,
        onBack = onBack,
        backEnabled = backEnabled,
        bottomContent = {
            if (canInvite) {
                ActionButton(
                    "Invite a friend",
                    leadingIcon = R.drawable.ic_add,
                    onClick = { onNavigate(AppRoute.AddMember) }
                )
            }
        }
    ) {
        Spacer(modifier = Modifier.height(112.dp))
        Text(
            group?.let { "${it.name} · ${it.memberCount} active members" } ?: "Group members",
            style = MaterialTheme.typography.bodySmall,
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
                        val isCurrentUser = member.userId == userId
                        val memberLabel = if (isCurrentUser) {
                            "You · Group ${member.memberStatus.name.lowercase()}"
                        } else {
                            member.memberStatus.name.lowercase().replaceFirstChar { it.uppercase() }
                        }
                        MemberCard(
                            name = member.name,
                            detail = memberLabel,
                            showArrow = !isCurrentUser && canRemoveMembers,
                            onClick = if (!isCurrentUser && canRemoveMembers) {
                                {
                                    memberViewModel.selectMember(
                                        MemberActionTarget(
                                            name = member.name,
                                            email = member.email,
                                            isInvitation = false,
                                            groupId = group.id,
                                            memberUserId = member.userId
                                        )
                                    )
                                    onNavigate(AppRoute.RemoveMember)
                                }
                            } else {
                                null
                            }
                        )
                    }
                    group.invitations.forEach { invitation ->
                        MemberCard(
                            name = invitation.name,
                            detail = "Invitation pending · Manage",
                            showArrow = true,
                            onClick = {
                                memberViewModel.selectMember(
                                    MemberActionTarget(
                                        invitation.name,
                                        invitation.email,
                                        true,
                                        group.id,
                                        invitationId = invitation.id
                                    )
                                )
                                onNavigate(AppRoute.RemoveMember)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MemberCard(
    name: String,
    detail: String,
    showArrow: Boolean,
    onClick: (() -> Unit)?
) {
    Card(
        modifier = Modifier.clickable(
            enabled = onClick != null,
            role = if (onClick == null) null else Role.Button,
            onClick = { onClick?.invoke() }
        ),
        contentPadding = 14.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = ForestGreen,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = detail,
                    style = MaterialTheme.typography.labelSmall,
                    color = MutedGreen
                )
            }
            if (showArrow) {
                Icon(
                    painter = painterResource(R.drawable.ic_chevron_right),
                    contentDescription = "Manage $name",
                    modifier = Modifier.size(20.dp),
                    tint = ForestGreen
                )
            }
        }
    }
}
