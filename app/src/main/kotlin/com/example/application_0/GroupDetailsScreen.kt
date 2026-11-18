package com.example.application_0

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.clickable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun GroupDetailsScreen(onNavigate: (AppRoute) -> Unit, onBack: () -> Unit, backEnabled: Boolean) {
    val groupsViewModel: GroupsViewModel = viewModel()
    val debtViewModel: DebtSummaryViewModel = viewModel()
    val groupsState by groupsViewModel.uiState.collectAsState()
    val debtState by debtViewModel.uiState.collectAsState()
    val activeGroupId by ActiveGroup.groupId.collectAsState()
    val group = groupsState.groups.firstOrNull { it.id == activeGroupId }
        ?: groupsState.groups.firstOrNull()
    val userId = Authentication.currentUser.collectAsState().value?.userId

    LaunchedEffect(group?.id, userId) {
        if (group != null && userId != null) {
            ActiveGroup.select(group.id)
            debtViewModel.calculateDebts(
                listOf(
                    DebtRequest(DebtType.GROUP_OWED, userId, group.id),
                    DebtRequest(DebtType.GROUP_OWING, userId, group.id)
                )
            )
        }
    }

    ScreenLayout(
        title = "Group details",
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
        ScreenDataStatus(groupsState.isLoading || debtState.isLoading, groupsState.error ?: debtState.error)
        if (group != null) {
            Spacer(modifier = Modifier.height(132.dp))
            Text(
                text = group.name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                color = ForestGreen
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${group.memberCount} people · ${group.type ?: "Group"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MutedGreen
                )
                Text(
                    text = "Members →",
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .clickable(onClick = { onNavigate(AppRoute.Members) }),
                    style = MaterialTheme.typography.bodySmall,
                    color = ForestGreen
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            val amountOwed = debtState.debtResults.firstOrNull {
                it.debtType == DebtType.GROUP_OWING
            }?.result ?: 0.0
            val amountToPay = debtState.debtResults.firstOrNull {
                it.debtType == DebtType.GROUP_OWED
            }?.result ?: 0.0
            val netBalance = amountOwed - amountToPay
            Card {
                Text(
                    text = "TOTAL GROUP SPENDING",
                    style = MaterialTheme.typography.labelSmall,
                    color = MutedGreen
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = String.format(java.util.Locale.UK, "%,.0f", group.totalExpenseAmount),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = ForestGreen
                    )
                    Text(
                        text = group.currency,
                        modifier = Modifier.padding(bottom = 5.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MutedGreen
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = when {
                        netBalance > 0.0 ->
                            "You're owed ${formatAmount(netBalance, group.currency)}"
                        netBalance < 0.0 ->
                            "You owe ${formatAmount(kotlin.math.abs(netBalance), group.currency)}"
                        else -> "Your balance is settled"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = if (netBalance < 0.0) WarmRed else ForestGreen
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            ActionButton(
                "Expenses",
                secondary = true,
                onClick = { onNavigate(AppRoute.Expenses) }
            )
            Spacer(modifier = Modifier.height(10.dp))
            ActionButton(
                "Debt summary",
                secondary = true,
                onClick = { onNavigate(AppRoute.DebtSummary) }
            )
        } else if (!groupsState.isLoading && groupsState.error == null) {
            ScreenMessage("You are not a member of any groups.")
        }
    }
}
