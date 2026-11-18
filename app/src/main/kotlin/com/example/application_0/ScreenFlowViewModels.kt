package com.example.application_0

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class ExpenseDraftUiState(
    val amount: String = "480.00",
    val description: String = "Dinner at ÆDEHUS",
    val receiptAttached: Boolean = false,
    val selectedExpense: ExpenseSummary? = null
)

class ExpenseFlowViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ExpenseDraftUiState())
    val uiState = _uiState.asStateFlow()

    fun updateAmount(amount: String) {
        _uiState.update { it.copy(amount = amount.filter { character ->
            character.isDigit() || character == '.'
        }) }
    }

    fun updateDescription(description: String) {
        _uiState.update { it.copy(description = description) }
    }

    fun attachSampleReceipt() {
        _uiState.update { it.copy(receiptAttached = true) }
    }

    fun clearReceipt() {
        _uiState.update { it.copy(receiptAttached = false) }
    }

    fun selectExpense(expense: ExpenseSummary) {
        _uiState.update { it.copy(selectedExpense = expense) }
    }

    fun clearSelection() {
        _uiState.update { it.copy(selectedExpense = null) }
    }
}

data class MemberActionTarget(
    val name: String,
    val email: String,
    val isInvitation: Boolean
)

class MemberManagementViewModel : ViewModel() {
    private val _selectedMember = MutableStateFlow<MemberActionTarget?>(null)
    val selectedMember = _selectedMember.asStateFlow()

    fun selectMember(target: MemberActionTarget) {
        _selectedMember.value = target
    }
}
