package com.example.application_0

import androidx.lifecycle.viewModelScope
import data.database.DatabaseOperations
import data.database.DatabaseProvider
import data.database.MembershipMutationResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import types.GroupInvitation
import types.Notification
import types.NotificationType
import java.time.LocalDateTime
import java.util.UUID

data class GroupInvitationDetails(
    val id: String,
    val groupId: String,
    val groupName: String,
    val inviteeName: String,
    val inviterName: String
)

data class GroupInvitationsUiState(
    val invitations: List<NotificationSummary> = emptyList(),
    val selectedInvitation: GroupInvitationDetails? = null,
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val error: String? = null
)

class GroupInvitationsViewModel(
    private val database: DatabaseOperations = DatabaseProvider.current()
) : AuthenticatedViewModel() {
    private val _uiState = MutableStateFlow(GroupInvitationsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        observeAuthenticatedUser(::loadInvitations)
    }

    fun refresh() {
        loadInvitations(Authentication.currentUser.value)
    }

    fun selectInvitation(invitationId: String) {
        val userId = Authentication.currentUser.value?.userId ?: return
        _uiState.update { it.copy(selectedInvitation = null, isLoading = true, error = null) }
        viewModelScope.launch {
            try {
                val invitation = database.getGroupInvitations()
                    .firstOrNull { it.id == invitationId }
                    ?: run {
                        _uiState.update {
                            it.copy(isLoading = false, error = "This invitation is no longer available.")
                        }
                        return@launch
                    }
                val user = database.getUserById(userId)
                if (user == null ||
                    (invitation.inviteeUserId != userId &&
                        !invitation.inviteeEmail.equals(user.email, ignoreCase = true))
                ) {
                    _uiState.update {
                        it.copy(isLoading = false, error = "This invitation is not available to your account.")
                    }
                    return@launch
                }
                val group = database.getGroups().firstOrNull { it.id == invitation.groupId }
                val inviter = database.getUserById(invitation.invitedByUserId)
                if (group == null || inviter == null) {
                    _uiState.update {
                        it.copy(isLoading = false, error = "Invitation details could not be loaded.")
                    }
                    return@launch
                }
                _uiState.update {
                    it.copy(
                        selectedInvitation = GroupInvitationDetails(
                            id = invitation.id,
                            groupId = invitation.groupId,
                            groupName = group.name,
                            inviteeName = invitation.inviteeName,
                            inviterName = inviter.name
                        ),
                        isLoading = false,
                        error = null
                    )
                }
            } catch (_: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = "Invitation details could not be loaded.")
                }
            }
        }
    }

    suspend fun createInvitation(groupId: String, email: String): MembershipMutationResult =
        try {
            val userId = Authentication.currentUser.value?.userId
                ?: return MembershipMutationResult.NOT_ALLOWED
            val invitee = database.getUserByEmail(email.trim())
                ?: return MembershipMutationResult.INVALID_INVITEE
            val group = database.getGroups().firstOrNull { it.id == groupId }
                ?: return MembershipMutationResult.NOT_FOUND
            val now = LocalDateTime.now()
            val invitation = GroupInvitation(
                id = "invitation-${UUID.randomUUID()}",
                groupId = groupId,
                inviteeName = invitee.name,
                inviteeEmail = invitee.email,
                invitedByUserId = userId,
                createdAt = now,
                inviteeUserId = invitee.id
            )
            val inviter = database.getUserById(userId)
                ?: return MembershipMutationResult.NOT_ALLOWED
            val notification = Notification(
                id = "notification-invitation-${invitation.id}",
                userId = invitee.id,
                type = NotificationType.INVITATIONS,
                message = "${inviter.name} invited you to join ${group.name}.",
                createdAt = now,
                read = false,
                relatedEntityId = invitation.id
            )
            val result = database.createGroupInvitation(invitation, notification)
            if (result == MembershipMutationResult.SUCCESS) refresh()
            result
        } catch (_: Exception) {
            MembershipMutationResult.FAILURE
        }

    suspend fun resolveInvitation(accept: Boolean): MembershipMutationResult {
        val userId = Authentication.currentUser.value?.userId
            ?: return MembershipMutationResult.NOT_ALLOWED
        val invitation = _uiState.value.selectedInvitation
            ?: return MembershipMutationResult.NOT_FOUND
        _uiState.update { it.copy(isSaving = true, error = null) }
        return try {
            val result = database.resolveGroupInvitation(invitation.id, userId, accept)
            if (result == MembershipMutationResult.SUCCESS) {
                _uiState.update {
                    it.copy(selectedInvitation = null, isSaving = false, error = null)
                }
                refresh()
            } else {
                _uiState.update {
                    it.copy(isSaving = false, error = mutationError(result))
                }
            }
            result
        } catch (_: Exception) {
            _uiState.update {
                it.copy(isSaving = false, error = "The invitation could not be updated.")
            }
            MembershipMutationResult.FAILURE
        }
    }

    suspend fun removeInvitation(invitationId: String): MembershipMutationResult {
        val userId = Authentication.currentUser.value?.userId
            ?: return MembershipMutationResult.NOT_ALLOWED
        return try {
            database.removeGroupInvitation(invitationId, userId).also { result ->
                if (result == MembershipMutationResult.SUCCESS) refresh()
            }
        } catch (_: Exception) {
            MembershipMutationResult.FAILURE
        }
    }

    suspend fun removeMember(groupId: String, memberUserId: String): MembershipMutationResult {
        val userId = Authentication.currentUser.value?.userId
            ?: return MembershipMutationResult.NOT_ALLOWED
        return try {
            database.removeGroupMember(groupId, memberUserId, userId)
        } catch (_: Exception) {
            MembershipMutationResult.FAILURE
        }
    }

    private fun loadInvitations(user: AuthenticatedUser?) {
        if (user == null) {
            _uiState.value = GroupInvitationsUiState(
                isLoading = false,
                error = "No authenticated user."
            )
            return
        }
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            try {
                val userRecord = database.getUserById(user.userId)
                    ?: throw IllegalStateException("Authenticated user was not found.")
                val groups = database.getGroups().associateBy { it.id }
                val inviters = database.getUsers().associateBy { it.id }
                val groupNotifications = if (
                    userRecord.notificationsEnabled &&
                    userRecord.notificationSettings.groupActivity
                ) {
                    database.getNotifications()
                        .filter {
                            it.userId == user.userId && it.type == NotificationType.GROUP
                        }
                        .mapNotNull { notification ->
                            val group = groups[notification.relatedEntityId]
                                ?: return@mapNotNull null
                            NotificationSummary(
                                id = notification.id,
                                type = NotificationType.GROUP,
                                message = notification.message,
                                read = notification.read,
                                createdAt = notification.createdAt,
                                relatedEntityId = notification.relatedEntityId,
                                groupId = group.id,
                                destination = AppRoute.GroupDetails
                            )
                        }
                } else {
                    emptyList()
                }
                val invitationSummaries = database.getGroupInvitations()
                    .filter { invitation ->
                        invitation.inviteeUserId == user.userId ||
                            invitation.inviteeEmail.equals(userRecord.email, ignoreCase = true)
                    }
                    .mapNotNull { invitation ->
                        val group = groups[invitation.groupId] ?: return@mapNotNull null
                        val inviter = inviters[invitation.invitedByUserId]
                        NotificationSummary(
                            id = "invitation-${invitation.id}",
                            type = NotificationType.INVITATIONS,
                            message = "${inviter?.name ?: "A group member"} invited you to join ${group.name}.",
                            read = false,
                            createdAt = invitation.createdAt,
                            relatedEntityId = invitation.id,
                            groupId = group.id,
                            destination = AppRoute.ManageGroupInvitation
                        )
                    }
                val notifications = (groupNotifications + invitationSummaries)
                    .sortedByDescending { it.createdAt }
                _uiState.update {
                    it.copy(invitations = notifications, isLoading = false, error = null)
                }
            } catch (_: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = "Group invitations could not be loaded.")
                }
            }
        }
    }
}

private fun mutationError(result: MembershipMutationResult): String = when (result) {
    MembershipMutationResult.NOT_FOUND -> "This invitation is no longer available."
    MembershipMutationResult.NOT_ALLOWED -> "You cannot manage this invitation."
    MembershipMutationResult.INVALID_INVITEE -> "The invitation is not addressed to your account."
    MembershipMutationResult.ALREADY_MEMBER -> "You are already a member of this group."
    MembershipMutationResult.ALREADY_INVITED -> "This invitation has already been sent."
    MembershipMutationResult.FAILURE -> "The invitation could not be updated."
    MembershipMutationResult.SUCCESS -> ""
}
