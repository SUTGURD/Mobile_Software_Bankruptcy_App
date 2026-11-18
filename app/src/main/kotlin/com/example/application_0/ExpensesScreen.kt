package com.example.application_0

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
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

    ScreenLayout(
        title = "Expenses",
        showBottomBar = true,
        bottomBarDestination = AppRoute.Groups,
        onNavigate = onNavigate,
        onBack = onBack,
        backEnabled = backEnabled,
        bottomContent = {
            ActionButton(
                "Add expense",
                leadingIcon = R.drawable.ic_add,
                onClick = { onNavigate(AppRoute.AddExpense) }
            )
        }
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            group?.name ?: "Your group",
            style = MaterialTheme.typography.headlineSmall,
            color = ForestGreen,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(40.dp))
        ScreenDataStatus(groupsState.isLoading || expensesState.isLoading, groupsState.error ?: expensesState.error)
        if (group != null) {
            Card(color = ForestGreen) {
                Text(
                    "TOTAL SPENT · ${group.currency}",
                    style = MaterialTheme.typography.labelSmall,
                    color = PaleGreen
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    formatCompactAmount(group.totalExpenseAmount),
                    style = MaterialTheme.typography.headlineMedium,
                    color = androidx.compose.ui.graphics.Color.White,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
        Spacer(modifier = Modifier.height(18.dp))
        when {
            groupsState.isLoading || expensesState.isLoading || expensesState.error != null -> Unit
            expensesState.expenses.isEmpty() -> ScreenMessage("This group has no expenses.")
            else -> Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                expensesState.expenses.forEach { expense ->
                    Card {
                        androidx.compose.foundation.layout.Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    expense.description ?: "Expense",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = ForestGreen,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    "${expense.paidByName} paid · ${formatDate(expense.createdAt)} · Split equally",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MutedGreen
                                )
                            }
                            Text(
                                formatCompactAmount(expense.amount) + " ${group?.currency ?: "DKK"}",
                                color = ForestGreen,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun formatCompactAmount(amount: Double): String =
    java.lang.String.format(java.util.Locale.UK, "%,.0f", amount)
