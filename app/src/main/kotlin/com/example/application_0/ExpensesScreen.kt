package com.example.application_0

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ExpensesScreen(onNavigate: (AppRoute) -> Unit, onBack: () -> Unit, backEnabled: Boolean) {
    val expensesViewModel: ExpensesViewModel = viewModel()
    val groupsViewModel: GroupsViewModel = viewModel()
    val expensesState by expensesViewModel.uiState.collectAsState()
    val groupsState by groupsViewModel.uiState.collectAsState()
    val activeGroupId by ActiveGroup.groupId.collectAsState()
    val group = groupsState.groups.firstOrNull { it.id == activeGroupId }
        ?: groupsState.groups.firstOrNull()

    LaunchedEffect(group?.id) {
        group?.let {
            ActiveGroup.select(it.id)
            expensesViewModel.loadGroup(it.id)
        }
    }

    MockupScreenLayout(
        title = "Expenses",
        subtitle = group?.let { "${it.name} · ${it.expenseCount} expenses" } ?: "Expenses in your groups.",
        showBottomBar = true,
        bottomBarDestination = AppRoute.Groups,
        onNavigate = onNavigate,
        onBack = onBack,
        backEnabled = backEnabled
    ) {
        ScreenDataStatus(groupsState.isLoading || expensesState.isLoading, groupsState.error ?: expensesState.error)
        MockupAction("Add expense", onClick = { onNavigate(AppRoute.AddExpense) })
        Spacer(modifier = Modifier.height(18.dp))
        when {
            groupsState.isLoading || expensesState.isLoading || expensesState.error != null -> Unit
            expensesState.expenses.isEmpty() -> ScreenMessage("This group has no expenses.")
            else -> MockupCard {
                Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
                    expensesState.expenses.forEach { expense ->
                        MockupInfoRow(
                            title = expense.description ?: "Expense",
                            detail = "${formatDate(expense.createdAt)} · Paid by ${expense.paidByName}",
                            trailing = formatAmount(expense.amount, group?.currency ?: "DKK")
                        )
                    }
                }
            }
        }
    }
}
