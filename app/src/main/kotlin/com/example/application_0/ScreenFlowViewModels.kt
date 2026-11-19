package com.example.application_0

import androidx.lifecycle.ViewModel
import data.database.DatabaseOperations
import data.database.DatabaseProvider
import data.database.ExpenseCreationResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDateTime
import java.util.UUID
import types.Expense
import types.ExpenseSplit

internal fun amountToMinorUnits(amount: String): Long? =
    try {
        BigDecimal(amount.trim())
            .setScale(2, RoundingMode.UNNECESSARY)
            .movePointRight(2)
            .longValueExact()
            .takeIf { it > 0 }
    } catch (_: NumberFormatException) {
        null
    } catch (_: ArithmeticException) {
        null
    }

internal fun allocateEqualShares(
    amountMinorUnits: Long,
    memberUserIds: List<String>
): List<Pair<String, Long>>? {
    val members = memberUserIds.distinct().sorted()
    if (amountMinorUnits <= 0 || members.isEmpty() || members.size != memberUserIds.size) {
        return null
    }
    val baseShare = amountMinorUnits / members.size
    val remainder = (amountMinorUnits % members.size).toInt()
    return members.mapIndexed { index, userId ->
        userId to (baseShare + if (index < remainder) 1 else 0)
    }
}

data class ExpenseDraftUiState(
    val amount: String = "480.00",
    val description: String = "",
    val receiptAttached: Boolean = false,
    val selectedExpense: ExpenseSummary? = null,
    val isSaving: Boolean = false,
    val saveError: String? = null,
    val savedExpenseId: String? = null
)

class ExpenseFlowViewModel(
    private val database: DatabaseOperations = DatabaseProvider.current()
) : ViewModel() {
    private val _uiState = MutableStateFlow(ExpenseDraftUiState())
    val uiState = _uiState.asStateFlow()

    fun updateAmount(amount: String) {
        _uiState.update {
            if (it.isSaving || it.savedExpenseId != null) {
                it
            } else {
                it.copy(
                    amount = amount.filter { character ->
                        character.isDigit() || character == '.'
                    },
                    saveError = null
                )
            }
        }
    }

    fun updateDescription(description: String) {
        _uiState.update {
            if (it.isSaving || it.savedExpenseId != null) it
            else it.copy(description = description, saveError = null)
        }
    }

    fun attachSampleReceipt() {
        _uiState.update {
            if (it.isSaving || it.savedExpenseId != null) it
            else it.copy(receiptAttached = true, saveError = null)
        }
    }

    fun clearReceipt() {
        _uiState.update {
            if (it.isSaving || it.savedExpenseId != null) it
            else it.copy(receiptAttached = false, saveError = null)
        }
    }

    fun selectExpense(expense: ExpenseSummary) {
        _uiState.update { it.copy(selectedExpense = expense) }
    }

    fun clearSelection() {
        _uiState.update { it.copy(selectedExpense = null) }
    }

    fun beginNewExpense() {
        _uiState.value = ExpenseDraftUiState()
    }

    suspend fun saveExpense(groupId: String): Boolean {
        val draft = _uiState.value
        if (draft.isSaving || draft.savedExpenseId != null) return false
        _uiState.update { it.copy(isSaving = true, saveError = null) }
        var savedExpenseId: String? = null

        val creation = try {
            val userId = Authentication.currentUser.value?.userId
            if (userId == null) {
                setSaveFailure("Sign in before saving an expense.")
                return false
            }
            val amountMinorUnits = amountToMinorUnits(draft.amount)
            if (amountMinorUnits == null) {
                setSaveFailure("Enter an amount greater than zero with no more than two decimal places.")
                return false
            }
            if (database.getGroups().none { it.id == groupId }) {
                setSaveFailure("The selected group could not be found.")
                return false
            }
            val memberIds = database.getGroupMembers()
                .filter { it.groupId == groupId }
                .map { it.userId }
                .distinct()
                .sorted()
            if (userId !in memberIds) {
                setSaveFailure("You must be a member of this group to add an expense.")
                return false
            }
            val shares = allocateEqualShares(amountMinorUnits, memberIds)
            if (shares == null) {
                setSaveFailure("Add at least one group member before saving an expense.")
                return false
            }

            val expenseId = "expense-${UUID.randomUUID()}"
            savedExpenseId = expenseId
            val expense = Expense(
                id = expenseId,
                groupId = groupId,
                amount = amountMinorUnits / 100.0,
                description = draft.description.trim().ifEmpty { null },
                createdAt = LocalDateTime.now(),
                paidBy = userId,
                receiptUri = if (draft.receiptAttached) "sample://expense-receipt" else null,
                authorId = userId
            )
            val splits = shares.map { (memberUserId, shareMinorUnits) ->
                ExpenseSplit(
                    id = "split-${UUID.randomUUID()}",
                    expenseId = expenseId,
                    userId = memberUserId,
                    amount = shareMinorUnits / 100.0
                )
            }
            database.createExpense(expense, splits, userId)
        } catch (_: Exception) {
            ExpenseCreationResult.FAILURE
        }

        if (creation == ExpenseCreationResult.CREATED) {
            _uiState.update {
                it.copy(isSaving = false, saveError = null, savedExpenseId = savedExpenseId)
            }
            return true
        }

        val message = when (creation) {
            ExpenseCreationResult.GROUP_NOT_FOUND -> "The selected group could not be found."
            ExpenseCreationResult.NOT_GROUP_MEMBER ->
                "You must be a member of this group to add an expense."
            ExpenseCreationResult.INVALID_EXPENSE ->
                "Check the amount and group split before saving the expense."
            ExpenseCreationResult.FAILURE -> "The expense could not be saved. Try again."
            ExpenseCreationResult.CREATED -> null
        }
        setSaveFailure(message ?: "The expense could not be saved. Try again.")
        return false
    }

    private fun setSaveFailure(message: String) {
        _uiState.update { it.copy(isSaving = false, saveError = message) }
    }
}

data class MemberActionTarget(
    val name: String,
    val email: String,
    val isInvitation: Boolean,
    val groupId: String? = null,
    val memberUserId: String? = null,
    val invitationId: String? = null
)

class MemberManagementViewModel : ViewModel() {
    private val _selectedMember = MutableStateFlow<MemberActionTarget?>(null)
    val selectedMember = _selectedMember.asStateFlow()

    fun selectMember(target: MemberActionTarget) {
        _selectedMember.value = target
    }

    fun clearMember() {
        _selectedMember.value = null
    }
}
