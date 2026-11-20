package com.example.application_0

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.platform.testTag
import androidx.compose.foundation.clickable
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.util.Locale

@Composable
fun DebtScreen(
    onNavigate: (AppRoute) -> Unit,
    onBack: (() -> Unit)?,
    backEnabled: Boolean,
    groupsViewModel: GroupsViewModel
) {
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

    ScreenLayout(
        title = "Debt",
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
        val totalsLoaded = owed != null && owing != null
        val netBalance = (owing?.result ?: 0.0) - (owed?.result ?: 0.0)
        BalanceCard(
            label = "Your net balance · $currency",
            amount = if (totalsLoaded) {
                val sign = when {
                    netBalance > 0.0 -> "+"
                    netBalance < 0.0 -> "-"
                    else -> ""
                }
                sign + String.format(Locale.UK, "%,.0f", kotlin.math.abs(netBalance))
            } else {
                "Loading"
            },
            detail = when {
                !totalsLoaded -> ""
                else -> "Across all your groups"
            },
            action = "",
            currency = "",
            owing = netBalance < 0.0,
            onClick = {}
        )
        Spacer(modifier = Modifier.height(24.dp))
        val groupBalances = groupsState.groups.mapNotNull { group ->
            val amountOwed = debtState.debtResults.firstOrNull {
                it.debtType == DebtType.GROUP_OWING && it.groupId == group.id
            }?.result ?: 0.0
            val amountToPay = debtState.debtResults.firstOrNull {
                it.debtType == DebtType.GROUP_OWED && it.groupId == group.id
            }?.result ?: 0.0
            val net = amountOwed - amountToPay
            if (net == 0.0) null else group to net
        }
        when {
            groupsState.isLoading || debtState.isLoading ||
                groupsState.error != null || debtState.error != null -> Unit
            groupsState.groups.isEmpty() -> ScreenMessage("You are not a member of any groups.")
            groupBalances.isEmpty() -> ScreenMessage("All your group balances are settled.")
            else -> Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                val owedGroups = groupBalances.filter { it.second > 0.0 }
                val owingGroups = groupBalances.filter { it.second < 0.0 }
                if (owedGroups.isNotEmpty()) {
                    BalanceGroupHeading("YOU ARE OWED")
                    owedGroups.forEach { (group, net) ->
                        GroupBalanceCard(
                            group = group,
                            balance = "You're owed ${formatAmount(net, group.currency)}",
                            onClick = {
                                ActiveGroup.select(group.id)
                                onNavigate(AppRoute.GroupDetails)
                            }
                        )
                    }
                }
                if (owingGroups.isNotEmpty()) {
                    BalanceGroupHeading("YOU OWE")
                    owingGroups.forEach { (group, net) ->
                        GroupBalanceCard(
                            group = group,
                            balance = "${formatAmount(kotlin.math.abs(net), group.currency)}",
                            onClick = {
                                ActiveGroup.select(group.id)
                                onNavigate(AppRoute.GroupDetails)
                            },
                            owing = true
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BalanceGroupHeading(label: String) {
    Text(
        text = label,
        style = MaterialTheme.typography.labelSmall,
        color = MutedGreen
    )
    Spacer(modifier = Modifier.height(4.dp))
}

@Composable
private fun GroupBalanceCard(
    group: GroupSummary,
    balance: String,
    onClick: () -> Unit,
    owing: Boolean = false
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("debt_group_card_${group.id}")
            .clickable(role = Role.Button, onClick = onClick),
        contentPadding = 14.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = group.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = ForestGreen
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = balance,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (owing) WarmRed else ForestGreen
                )
            }
            Icon(
                painter = painterResource(R.drawable.ic_chevron_right),
                contentDescription = null,
                tint = ForestGreen
            )
        }
    }
}
