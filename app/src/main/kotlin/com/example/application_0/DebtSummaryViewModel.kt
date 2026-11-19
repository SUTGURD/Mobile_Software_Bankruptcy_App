package com.example.application_0

import data.database.DatabaseOperations
import data.database.DatabaseProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
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
    val debtResults: List<DebtResult> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class DebtSummaryViewModel(
    private val database: DatabaseOperations = DatabaseProvider.current()
) : AuthenticatedViewModel() {
    private val _uiState = MutableStateFlow(DebtSummaryUiState())
    val uiState = _uiState.asStateFlow()
    private var observedUserId = Authentication.currentUser.value?.userId

    init {
        observeAuthenticatedUser { user ->
            val userId = user?.userId
            if (userId != observedUserId) {
                observedUserId = userId
                _uiState.value = DebtSummaryUiState(
                    error = if (user == null) "No authenticated user." else null
                )
            } else if (user == null) {
                _uiState.update { it.copy(error = "No authenticated user.") }
            }
        }
    }

    fun calculateDebt(request: DebtRequest) {
        calculateDebts(listOf(request))
    }

    fun calculateDebts(requests: List<DebtRequest>) {
        _uiState.update { it.copy(isLoading = true, error = null) }

        val user = Authentication.currentUser.value
        if (user == null) {
            setError("No authenticated user.")
            return
        }
        viewModelScope.launch {
            try {
                val memberships = database.getGroupMembers()
                val allGroups = database.getGroups()
                val users = database.getUsers()
                val expenses = database.getExpenses()
                val expenseSplits = database.getExpenseSplits()
                val payments = database.getPayments()
                val userGroupIds = memberships
                    .filter { it.userId == user.userId }
                    .map { it.groupId }
                    .toSet()
                val groups = allGroups.filter { it.id in userGroupIds }
                val results = mutableListOf<DebtResult>()

                for (request in requests) {
                    val result = calculateResult(
                        request,
                        user,
                        groups,
                        users,
                        expenses,
                        expenseSplits,
                        payments
                    )
                    if (result == null) return@launch
                    results += result
                }

                _uiState.value = DebtSummaryUiState(
                    debtResult = results.lastOrNull(),
                    debtResults = results
                )
            } catch (_: Exception) {
                setError("Debt data could not be loaded.")
            }
        }
    }

    private fun calculateResult(
        request: DebtRequest,
        user: AuthenticatedUser,
        groups: List<types.Group>,
        users: List<types.User>,
        expenses: List<types.Expense>,
        expenseSplits: List<types.ExpenseSplit>,
        payments: List<types.Payment>
    ): DebtResult? {
        if (request.userId != user.userId) {
            setError("The request user must be the authenticated user.")
            return null
        }

        if (request.debtType == DebtType.GROUP_OWED ||
            request.debtType == DebtType.GROUP_OWING
        ) {
            val groupId = request.groupId
            if (groupId == null) {
                setError("A group is required for this debt type.")
                return null
            }
            if (groups.none { it.id == groupId }) {
                setError("You are not a member of this group.")
                return null
            }
        }

        val pairDebt = request.debtType == DebtType.OWED_BY_USER ||
            request.debtType == DebtType.OWING_TO_USER
        if (pairDebt) {
            val otherUserId = request.otherUserId
            if (otherUserId == null) {
                setError("Another user is required for this debt type.")
                return null
            }
            if (otherUserId == user.userId ||
                users.none { it.id == otherUserId }
            ) {
                setError("The other user is not valid.")
                return null
            }
        }

        val needsCurrency = request.debtType == DebtType.TOTAL_OWED ||
            request.debtType == DebtType.TOTAL_OWING ||
            pairDebt
        val currencies = groups.map { it.currency }.distinct()
        if (needsCurrency && currencies.size > 1) {
            setError("Debt cannot be combined across different currencies.")
            return null
        }
        val currency = currencies.singleOrNull()
        val amount = when (request.debtType) {
            DebtType.TOTAL_OWED -> currency?.let {
                totalAmountPersonOwes(
                    user.userId,
                    it,
                    groups,
                    expenses,
                    expenseSplits,
                    payments
                )
            } ?: 0.0
            DebtType.TOTAL_OWING -> currency?.let {
                totalAmountPersonIsOwed(
                    user.userId,
                    it,
                    groups,
                    expenses,
                    expenseSplits,
                    payments
                )
            } ?: 0.0
            DebtType.GROUP_OWED -> amountPersonOwesInGroup(
                user.userId,
                requireNotNull(request.groupId),
                expenses,
                expenseSplits,
                payments
            )
            DebtType.GROUP_OWING -> amountPersonIsOwedInGroup(
                user.userId,
                requireNotNull(request.groupId),
                expenses,
                expenseSplits,
                payments
            )
            DebtType.OWED_BY_USER -> currency?.let {
                amountPersonOwesAnotherPerson(
                    requireNotNull(request.otherUserId),
                    user.userId,
                    it,
                    groups,
                    expenses,
                    expenseSplits,
                    payments
                )
            } ?: 0.0
            DebtType.OWING_TO_USER -> currency?.let {
                amountPersonOwesAnotherPerson(
                    user.userId,
                    requireNotNull(request.otherUserId),
                    it,
                    groups,
                    expenses,
                    expenseSplits,
                    payments
                )
            } ?: 0.0
        }

        return DebtResult(
            debtType = request.debtType,
            userId = request.userId,
            groupId = request.groupId,
            otherUserId = request.otherUserId,
            result = amount
        )
    }

    private fun setError(message: String) {
        _uiState.value = DebtSummaryUiState(
            isLoading = false,
            error = message
        )
    }
}
