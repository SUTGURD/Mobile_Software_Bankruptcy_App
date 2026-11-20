package com.example.application_0

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.clickable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material3.Text

@Composable
fun GroupsScreen(
    onNavigate: (AppRoute) -> Unit,
    onBack: (() -> Unit)?,
    backEnabled: Boolean,
    groupsViewModel: GroupsViewModel
) {
    val debtViewModel: DebtSummaryViewModel = viewModel()
    val state by groupsViewModel.uiState.collectAsState()
    val debtState by debtViewModel.uiState.collectAsState()
    val groupIds = state.groups.map { it.id }

    LaunchedEffect(Unit) {
        groupsViewModel.refresh()
    }

    LaunchedEffect(groupIds) {
        val userId = Authentication.currentUser.value?.userId ?: return@LaunchedEffect
        debtViewModel.calculateDebts(
            groupIds.flatMap { groupId ->
                listOf(
                    DebtRequest(DebtType.GROUP_OWED, userId, groupId),
                    DebtRequest(DebtType.GROUP_OWING, userId, groupId)
                )
            }
        )
    }

    ScreenLayout(
        title = "Groups",
        subtitle = "Your shared spaces",
        showBottomBar = true,
        bottomBarDestination = AppRoute.Groups,
        onNavigate = onNavigate,
        onBack = onBack,
        backEnabled = backEnabled,
        bottomContent = {
            ActionButton(
                "Create group",
                leadingIcon = R.drawable.ic_add,
                onClick = { onNavigate(AppRoute.CreateGroup) }
            )
        }
    ) {
        ScreenDataStatus(state.isLoading || debtState.isLoading, state.error ?: debtState.error)
        Spacer(modifier = Modifier.height(18.dp))
        Text(
            text = "ACTIVE GROUPS",
            style = MaterialTheme.typography.labelSmall,
            color = MutedGreen
        )
        Spacer(modifier = Modifier.height(12.dp))
        when {
            state.isLoading || debtState.isLoading || state.error != null || debtState.error != null -> Unit
            state.groups.isEmpty() -> ScreenMessage("You are not a member of any groups.")
            else -> Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    state.groups.forEach { group ->
                        val amountOwed = debtState.debtResults.firstOrNull {
                            it.debtType == DebtType.GROUP_OWING && it.groupId == group.id
                        }?.result ?: 0.0
                        val amountToPay = debtState.debtResults.firstOrNull {
                            it.debtType == DebtType.GROUP_OWED && it.groupId == group.id
                        }?.result ?: 0.0
                        val balance = when {
                            amountOwed > amountToPay ->
                                "You're owed ${formatAmount(amountOwed - amountToPay, group.currency)}"
                            amountToPay > amountOwed ->
                                "You owe ${formatAmount(amountToPay - amountOwed, group.currency)}"
                            else -> "Your balance is settled"
                        }
                        Card(
                            modifier = Modifier
                                .testTag("group_card_${group.id}")
                                .clickable(
                                    role = Role.Button,
                                    onClick = {
                                        ActiveGroup.select(group.id)
                                        onNavigate(AppRoute.GroupDetails)
                                    }
                                ),
                            contentPadding = 20.dp
                        ) {
                            Column {
                                Text(
                                    text = group.name,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = ForestGreen
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${group.memberCount} people · ${group.type ?: "Group"}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MutedGreen
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = balance,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = ForestGreen
                                )
                            }
                        }
                    }
                }
        }
    }
}
