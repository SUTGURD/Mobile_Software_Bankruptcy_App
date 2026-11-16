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
fun HomeScreen(onNavigate: (AppRoute) -> Unit) {
    val profileViewModel: UserProfileViewModel = viewModel()
    val groupsViewModel: GroupsViewModel = viewModel()
    val debtViewModel: DebtSummaryViewModel = viewModel()
    val profileState by profileViewModel.uiState.collectAsState()
    val groupsState by groupsViewModel.uiState.collectAsState()
    val debtState by debtViewModel.uiState.collectAsState()
    val groupIds = groupsState.groups.map { it.id }
    val debtError = debtState.error
    val groupsError = groupsState.error

    LaunchedEffect(profileState.profile?.id, groupIds) {
        val userId = profileState.profile?.id ?: return@LaunchedEffect
        debtViewModel.calculateDebts(
            listOf(
                DebtRequest(DebtType.TOTAL_OWED, userId),
                DebtRequest(DebtType.TOTAL_OWING, userId)
            )
        )
    }

    MockupScreenLayout(
        title = profileState.profile?.let { "Good morning, ${it.name}" } ?: "Home",
        subtitle = "Your shared spending overview.",
        showBottomBar = true,
        bottomBarDestination = AppRoute.Home,
        onNavigate = onNavigate
    ) {
        ScreenDataStatus(profileState.isLoading || groupsState.isLoading, profileState.error ?: groupsError)
        if (profileState.profile != null && debtError != null) {
            ScreenMessage(debtError)
        }
        val currency = groupsState.groups.firstOrNull()?.currency ?: "DKK"
        val totalOwed = debtState.debtResults.firstOrNull { it.debtType == DebtType.TOTAL_OWED }
        val totalOwing = debtState.debtResults.firstOrNull { it.debtType == DebtType.TOTAL_OWING }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MockupAmountCard(
                "You owe",
                totalOwed?.let { formatAmount(it.result, currency) } ?: "Loading",
                modifier = Modifier.weight(1f)
            )
            MockupAmountCard(
                "Owed to you",
                totalOwing?.let { formatAmount(it.result, currency) } ?: "Loading",
                positive = true,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        MockupSectionTitle("Your groups")
        Spacer(modifier = Modifier.height(12.dp))
        when {
            groupsState.isLoading -> ScreenMessage("Loading groups...")
            groupsError != null -> ScreenMessage(groupsError)
            groupsState.groups.isEmpty() -> ScreenMessage("You are not a member of any groups.")
            else -> MockupCard {
                Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
                    groupsState.groups.forEach { group ->
                        MockupInfoRow(
                            title = group.name,
                            detail = "${group.memberCount} members · ${group.expenseCount} expenses",
                            trailing = formatAmount(group.totalExpenseAmount, group.currency),
                            onClick = {
                                ActiveGroup.select(group.id)
                                onNavigate(AppRoute.GroupDetails)
                            }
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(18.dp))
        MockupAction(
            "View notifications",
            secondary = true,
            onClick = { onNavigate(AppRoute.Notifications) }
        )
    }
}
