package com.example.application_0

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
fun DebtScreen(onNavigate: (AppRoute) -> Unit, onBack: () -> Unit, backEnabled: Boolean) {
    val groupsViewModel: GroupsViewModel = viewModel()
    val debtViewModel: DebtSummaryViewModel = viewModel()
    val groupsState by groupsViewModel.uiState.collectAsState()
    val debtState by debtViewModel.uiState.collectAsState()
    val groupIds = groupsState.groups.map { it.id }

    LaunchedEffect(groupIds) {
        val userId = Authentication.currentUser.value?.userId ?: return@LaunchedEffect
        debtViewModel.calculateDebts(
            buildList {
                add(DebtRequest(DebtType.TOTAL_OWED, userId))
                add(DebtRequest(DebtType.TOTAL_OWING, userId))
                groupIds.forEach { groupId ->
                    add(DebtRequest(DebtType.GROUP_OWED, userId, groupId))
                    add(DebtRequest(DebtType.GROUP_OWING, userId, groupId))
                }
            }
        )
    }

    MockupScreenLayout(
        title = "Debt",
        subtitle = "Outstanding balances calculated from your expenses.",
        showBottomBar = true,
        bottomBarDestination = AppRoute.Debt,
        onNavigate = onNavigate,
        onBack = onBack,
        backEnabled = backEnabled
    ) {
        ScreenDataStatus(groupsState.isLoading || debtState.isLoading, groupsState.error ?: debtState.error)
        val currency = groupsState.groups.firstOrNull()?.currency ?: "DKK"
        val owed = debtState.debtResults.firstOrNull { it.debtType == DebtType.TOTAL_OWED }
        val owing = debtState.debtResults.firstOrNull { it.debtType == DebtType.TOTAL_OWING }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MockupAmountCard(
                "You owe",
                owed?.let { formatAmount(it.result, currency) } ?: "Loading",
                modifier = Modifier.weight(1f)
            )
            MockupAmountCard(
                "Owed to you",
                owing?.let { formatAmount(it.result, currency) } ?: "Loading",
                positive = true,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        MockupSectionTitle("Balances by group")
        Spacer(modifier = Modifier.height(12.dp))
        when {
            groupsState.isLoading || debtState.isLoading || debtState.error != null -> Unit
            groupsState.groups.isEmpty() -> ScreenMessage("You are not a member of any groups.")
            else -> MockupCard {
                Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
                    groupsState.groups.forEach { group ->
                        val groupOwed = debtState.debtResults.firstOrNull {
                            it.debtType == DebtType.GROUP_OWED && it.groupId == group.id
                        }
                        val groupOwing = debtState.debtResults.firstOrNull {
                            it.debtType == DebtType.GROUP_OWING && it.groupId == group.id
                        }
                        MockupInfoRow(
                            group.name,
                            "You owe ${formatAmount(groupOwed?.result ?: 0.0, group.currency)}",
                            trailing = "Owed ${formatAmount(groupOwing?.result ?: 0.0, group.currency)}"
                        )
                    }
                }
            }
        }
    }
}
