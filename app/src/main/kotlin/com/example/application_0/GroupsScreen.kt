package com.example.application_0

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun GroupsScreen(onNavigate: (AppRoute) -> Unit, onBack: () -> Unit, backEnabled: Boolean) {
    val viewModel: GroupsViewModel = viewModel()
    val state by viewModel.uiState.collectAsState()

    MockupScreenLayout(
        title = "Groups",
        subtitle = "Groups you belong to.",
        showBottomBar = true,
        bottomBarDestination = AppRoute.Groups,
        onNavigate = onNavigate,
        onBack = onBack,
        backEnabled = backEnabled
    ) {
        ScreenDataStatus(state.isLoading, state.error)
        Spacer(modifier = Modifier.height(12.dp))
        MockupAction("Create group", onClick = { onNavigate(AppRoute.CreateGroup) })
        Spacer(modifier = Modifier.height(18.dp))
        when {
            state.isLoading || state.error != null -> Unit
            state.groups.isEmpty() -> ScreenMessage("You are not a member of any groups.")
            else -> MockupCard {
                Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
                    state.groups.forEach { group ->
                        MockupInfoRow(
                            title = group.name,
                            detail = "${group.memberCount} members · ${group.expenseCount} expenses · ${group.memberStatus}",
                            trailing = formatAmount(group.totalExpenseAmount, group.currency),
                            accent = true,
                            onClick = {
                                ActiveGroup.select(group.id)
                                onNavigate(AppRoute.GroupDetails)
                            }
                        )
                    }
                }
            }
        }
    }
}
