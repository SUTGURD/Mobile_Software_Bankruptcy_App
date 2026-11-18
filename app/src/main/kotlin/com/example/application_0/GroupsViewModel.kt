package com.example.application_0

import data.DummyData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import types.MemberRole
import java.time.LocalDateTime

data class GroupSummary(
    val id: String,
    val name: String,
    val description: String?,
    val type: String?,
    val currency: String,
    val memberCount: Int,
    val expenseCount: Int,
    val totalExpenseAmount: Double,
    val memberStatus: MemberRole,
    val members: List<GroupMemberSummary>
)

data class GroupMemberSummary(
    val userId: String,
    val name: String,
    val email: String,
    val profileImageUri: String?,
    val memberStatus: MemberRole,
    val joinedAt: LocalDateTime
)

data class GroupsUiState(
    val groups: List<GroupSummary> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

class GroupsViewModel : AuthenticatedViewModel() {
    private val _uiState = MutableStateFlow(GroupsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        observeAuthenticatedUser(::loadGroups)
    }

    private fun loadGroups(user: AuthenticatedUser?) {
        _uiState.update { it.copy(isLoading = true, error = null) }
        if (user == null) {
            ActiveGroup.clear()
            _uiState.value = GroupsUiState(
                isLoading = false,
                error = "No authenticated user."
            )
            return
        }

        val memberships = DummyData.groupMembers.filter { it.userId == user.userId }
        val groupsById = DummyData.groups.associateBy { it.id }
        val groupIds = memberships.map { it.groupId }.toSet()
        if (ActiveGroup.groupId.value?.let { it in groupIds } != true) {
            memberships.firstOrNull()?.let { ActiveGroup.select(it.groupId) }
                ?: ActiveGroup.clear()
        }
        val summaries = memberships.mapNotNull { membership ->
            groupsById[membership.groupId]?.let { group ->
                val members = DummyData.groupMembers
                    .filter { it.groupId == group.id }
                    .mapNotNull { groupMember ->
                        DummyData.users.firstOrNull { it.id == groupMember.userId }?.let { member ->
                            GroupMemberSummary(
                                userId = member.id,
                                name = member.name,
                                email = member.email,
                                profileImageUri = member.profileImageUri,
                                memberStatus = groupMember.role,
                                joinedAt = groupMember.joinedAt
                            )
                        }
                    }
                GroupSummary(
                    id = group.id,
                    name = group.name,
                    description = group.description,
                    type = group.type,
                    currency = group.currency,
                    memberCount = DummyData.groupMembers.count { it.groupId == group.id },
                    expenseCount = DummyData.expenses.count { it.groupId == group.id },
                    totalExpenseAmount = DummyData.expenses
                        .filter { it.groupId == group.id }
                        .sumOf { it.amount },
                    memberStatus = membership.role,
                    members = members
                )
            }
        }
        val hasMissingGroup = memberships.any { it.groupId !in groupsById }

        _uiState.value = GroupsUiState(
            groups = summaries,
            isLoading = false,
            error = if (hasMissingGroup) "Some group information is unavailable." else null
        )
    }
}
