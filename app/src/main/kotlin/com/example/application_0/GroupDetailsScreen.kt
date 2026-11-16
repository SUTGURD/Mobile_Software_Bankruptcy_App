package com.example.application_0

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

    MockupScreenLayout(
        title = group?.name ?: "Group details",
        subtitle = group?.description ?: "Details for one of your groups.",
        showBottomBar = true,
        bottomBarDestination = AppRoute.Groups,
        onNavigate = onNavigate,
        onBack = onBack,
        backEnabled = backEnabled
    ) {
        ScreenDataStatus(groupsState.isLoading || debtState.isLoading, groupsState.error ?: debtState.error)
        if (group != null) {
            MockupCard {
                Column {
                    MockupInfoRow("Members", group.memberCount.toString())
                    Spacer(modifier = Modifier.height(12.dp))
                    MockupInfoRow("Expenses", group.expenseCount.toString())
                    Spacer(modifier = Modifier.height(12.dp))
                    MockupInfoRow("Total expenses", formatAmount(group.totalExpenseAmount, group.currency))
                    Spacer(modifier = Modifier.height(12.dp))
                    MockupInfoRow("Your member status", group.memberStatus.name)
                    Spacer(modifier = Modifier.height(12.dp))
                    val owed = debtState.debtResults.firstOrNull { it.debtType == DebtType.GROUP_OWED }
                    val owing = debtState.debtResults.firstOrNull { it.debtType == DebtType.GROUP_OWING }
                    MockupInfoRow(
                        "You owe",
                        owed?.let { formatAmount(it.result, group.currency) } ?: "Loading"
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    MockupInfoRow(
                        "Owed to you",
                        owing?.let { formatAmount(it.result, group.currency) } ?: "Loading"
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            MockupAction("Members", secondary = true, onClick = { onNavigate(AppRoute.Members) })
            Spacer(modifier = Modifier.height(12.dp))
            MockupAction("Expenses", secondary = true, onClick = { onNavigate(AppRoute.Expenses) })
            Spacer(modifier = Modifier.height(12.dp))
            MockupAction("Debt summary", secondary = true, onClick = { onNavigate(AppRoute.DebtSummary) })
        } else if (!groupsState.isLoading && groupsState.error == null) {
            ScreenMessage("You are not a member of any groups.")
        }
    }
}
