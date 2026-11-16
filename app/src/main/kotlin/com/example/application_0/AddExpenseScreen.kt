package com.example.application_0

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.unit.dp

@Composable
fun AddExpenseScreen(onBack: () -> Unit, backEnabled: Boolean) {
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
        title = "Add expense",
        subtitle = group?.name ?: "Select a group to view its expenses.",
        onBack = onBack,
        backEnabled = backEnabled
    ) {
        ScreenDataStatus(groupsState.isLoading || expensesState.isLoading, groupsState.error ?: expensesState.error)
        when {
            group == null && !groupsState.isLoading && groupsState.error == null ->
                ScreenMessage("You are not a member of any groups.")
            group != null -> {
                MockupCard {
                    Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
                        expensesState.expenses.forEach { expense ->
                            MockupInfoRow(
                                expense.description ?: "Expense",
                                "${formatDate(expense.createdAt)} · Paid by ${expense.paidByName}",
                                formatAmount(expense.amount, group.currency)
                            )
                        }
                    }
                }
                ScreenMessage("Adding expenses is not available while using dummy data.")
            }
        }
    }
}
