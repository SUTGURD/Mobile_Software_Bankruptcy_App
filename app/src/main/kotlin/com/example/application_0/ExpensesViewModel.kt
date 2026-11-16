package com.example.application_0

import data.DummyData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDateTime

data class ExpenseSummary(
    val id: String,
    val groupId: String,
    val description: String?,
    val amount: Double,
    val createdAt: LocalDateTime,
    val paidBy: String,
    val receiptUri: String?
)

data class ExpensesUiState(
    val groupId: String? = null,
    val expenses: List<ExpenseSummary> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

class ExpensesViewModel(groupId: String? = null) : AuthenticatedViewModel() {
    private val _uiState = MutableStateFlow(ExpensesUiState(groupId = groupId))
    val uiState = _uiState.asStateFlow()
    private var selectedGroupId: String? = groupId

    init {
        observeAuthenticatedUser(::loadExpenses)
    }

    fun loadGroup(groupId: String) {
        selectedGroupId = groupId
        loadExpenses(Authentication.currentUser.value)
    }

    private fun loadExpenses(user: AuthenticatedUser?) {
        _uiState.update {
            it.copy(
                groupId = selectedGroupId,
                isLoading = true,
                error = null
            )
        }
        if (user == null) {
            _uiState.value = ExpensesUiState(
                groupId = selectedGroupId,
                isLoading = false,
                error = "No authenticated user."
            )
            return
        }

        val groupId = selectedGroupId
        if (groupId == null) {
            _uiState.value = ExpensesUiState(
                isLoading = false,
                error = "No group selected."
            )
            return
        }
        if (DummyData.groups.none { it.id == groupId }) {
            _uiState.value = ExpensesUiState(
                groupId = groupId,
                isLoading = false,
                error = "Group was not found."
            )
            return
        }
        if (DummyData.groupMembers.none {
                it.groupId == groupId && it.userId == user.userId
            }
        ) {
            _uiState.value = ExpensesUiState(
                groupId = groupId,
                isLoading = false,
                error = "You are not a member of this group."
            )
            return
        }

        val expenses = DummyData.expenses
            .filter { it.groupId == groupId }
            .map { expense ->
                ExpenseSummary(
                    id = expense.id,
                    groupId = expense.groupId,
                    description = expense.description,
                    amount = expense.amount,
                    createdAt = expense.createdAt,
                    paidBy = expense.paidBy,
                    receiptUri = expense.receiptUri
                )
            }
        _uiState.value = ExpensesUiState(
            groupId = groupId,
            expenses = expenses,
            isLoading = false
        )
    }
}
