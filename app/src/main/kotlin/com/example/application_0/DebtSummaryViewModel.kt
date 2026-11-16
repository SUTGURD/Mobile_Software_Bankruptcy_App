package com.example.application_0

import data.DummyData
import domain.amountPersonIsOwedInGroup
import domain.amountPersonOwesAnotherPerson
import domain.amountPersonOwesInGroup
import domain.totalAmountPersonIsOwed
import domain.totalAmountPersonOwes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class DebtType {
    TOTAL_OWED,
    TOTAL_OWING,
    GROUP_OWED,
    GROUP_OWING,
    OWED_BY_USER,
    OWING_TO_USER
}

data class DebtRequest(
    val debtType: DebtType,
    val userId: String,
    val groupId: String? = null,
    val otherUserId: String? = null
)

data class DebtResult(
    val debtType: DebtType,
    val userId: String,
    val groupId: String? = null,
    val otherUserId: String? = null,
    val result: Double
)

data class DebtSummaryUiState(
    val debtResult: DebtResult? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class DebtSummaryViewModel : AuthenticatedViewModel() {
    private val _uiState = MutableStateFlow(DebtSummaryUiState())
    val uiState = _uiState.asStateFlow()

    init {
        observeAuthenticatedUser { user ->
            _uiState.value = DebtSummaryUiState(
                error = if (user == null) "No authenticated user." else null
            )
        }
    }

    fun calculateDebt(request: DebtRequest) {
        _uiState.update { it.copy(isLoading = true, error = null) }

        val user = Authentication.currentUser.value
        if (user == null) {
            setError("No authenticated user.")
            return
        }
        if (request.userId != user.userId) {
            setError("The request user must be the authenticated user.")
            return
        }

        val userGroupIds = DummyData.groupMembers
            .filter { it.userId == user.userId }
            .map { it.groupId }
            .toSet()
        val groups = DummyData.groups.filter { it.id in userGroupIds }

        if (request.debtType == DebtType.GROUP_OWED ||
            request.debtType == DebtType.GROUP_OWING
        ) {
            val groupId = request.groupId
            if (groupId == null) {
                setError("A group is required for this debt type.")
                return
            }
            if (groups.none { it.id == groupId }) {
                setError("You are not a member of this group.")
                return
            }
        }

        val pairDebt = request.debtType == DebtType.OWED_BY_USER ||
            request.debtType == DebtType.OWING_TO_USER
        if (pairDebt) {
            val otherUserId = request.otherUserId
            if (otherUserId == null) {
                setError("Another user is required for this debt type.")
                return
            }
            if (otherUserId == user.userId ||
                DummyData.users.none { it.id == otherUserId }
            ) {
                setError("The other user is not valid.")
                return
            }
        }

        val needsCurrency = request.debtType == DebtType.TOTAL_OWED ||
            request.debtType == DebtType.TOTAL_OWING ||
            pairDebt
        val currencies = groups.map { it.currency }.distinct()
        if (needsCurrency && currencies.size > 1) {
            setError("Debt cannot be combined across different currencies.")
            return
        }
        val currency = currencies.singleOrNull()

        val amount = when (request.debtType) {
            DebtType.TOTAL_OWED -> currency?.let {
                totalAmountPersonOwes(
                    user.userId,
                    it,
                    groups,
                    DummyData.expenses,
                    DummyData.expenseSplits,
                    DummyData.payments
                )
            } ?: 0.0
            DebtType.TOTAL_OWING -> currency?.let {
                totalAmountPersonIsOwed(
                    user.userId,
                    it,
                    groups,
                    DummyData.expenses,
                    DummyData.expenseSplits,
                    DummyData.payments
                )
            } ?: 0.0
            DebtType.GROUP_OWED -> amountPersonOwesInGroup(
                user.userId,
                requireNotNull(request.groupId),
                DummyData.expenses,
                DummyData.expenseSplits,
                DummyData.payments
            )
            DebtType.GROUP_OWING -> amountPersonIsOwedInGroup(
                user.userId,
                requireNotNull(request.groupId),
                DummyData.expenses,
                DummyData.expenseSplits,
                DummyData.payments
            )
            DebtType.OWED_BY_USER -> currency?.let {
                amountPersonOwesAnotherPerson(
                    requireNotNull(request.otherUserId),
                    user.userId,
                    it,
                    groups,
                    DummyData.expenses,
                    DummyData.expenseSplits,
                    DummyData.payments
                )
            } ?: 0.0
            DebtType.OWING_TO_USER -> currency?.let {
                amountPersonOwesAnotherPerson(
                    user.userId,
                    requireNotNull(request.otherUserId),
                    it,
                    groups,
                    DummyData.expenses,
                    DummyData.expenseSplits,
                    DummyData.payments
                )
            } ?: 0.0
        }

        _uiState.value = DebtSummaryUiState(
            debtResult = DebtResult(
                debtType = request.debtType,
                userId = request.userId,
                groupId = request.groupId,
                otherUserId = request.otherUserId,
                result = amount
            )
        )
    }

    private fun setError(message: String) {
        _uiState.value = DebtSummaryUiState(
            isLoading = false,
            error = message
        )
    }
}
