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
fun CreateGroupScreen(onNavigate: (AppRoute) -> Unit, onBack: () -> Unit, backEnabled: Boolean) {
    val viewModel: GroupsViewModel = viewModel()
    val state by viewModel.uiState.collectAsState()

    MockupScreenLayout(
        title = "Create group",
        subtitle = "Your current groups.",
        onBack = onBack,
        backEnabled = backEnabled
    ) {
        ScreenDataStatus(state.isLoading, state.error)
        when {
            state.isLoading || state.error != null -> Unit
            state.groups.isEmpty() -> ScreenMessage("You are not a member of any groups.")
            else -> {
                MockupCard {
                    Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
                        state.groups.forEach { group ->
                            MockupInfoRow(
                                group.name,
                                "${group.memberCount} members · ${group.expenseCount} expenses",
                                formatAmount(group.totalExpenseAmount, group.currency),
                                onClick = {
                                    ActiveGroup.select(group.id)
                                    onNavigate(AppRoute.GroupDetails)
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                ScreenMessage("Creating groups is not available while using dummy data.")
            }
        }
    }
}
