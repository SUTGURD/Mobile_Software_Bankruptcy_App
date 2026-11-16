package com.example.application_0

import data.DummyData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import types.MemberRole

data class GroupSummary(
    val id: String,
    val name: String,
    val memberCount: Int,
    val expenseCount: Int,
    val memberStatus: MemberRole
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
            _uiState.value = GroupsUiState(
                isLoading = false,
                error = "No authenticated user."
            )
            return
        }

        val memberships = DummyData.groupMembers.filter { it.userId == user.userId }
        val groupsById = DummyData.groups.associateBy { it.id }
        val summaries = memberships.mapNotNull { membership ->
            groupsById[membership.groupId]?.let { group ->
                GroupSummary(
                    id = group.id,
                    name = group.name,
                    memberCount = DummyData.groupMembers.count { it.groupId == group.id },
                    expenseCount = DummyData.expenses.count { it.groupId == group.id },
                    memberStatus = membership.role
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
