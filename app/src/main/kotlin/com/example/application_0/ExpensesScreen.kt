package com.example.application_0

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ExpensesScreen(onNavigate: (AppRoute) -> Unit, onBack: () -> Unit, backEnabled: Boolean) {
    MockupScreenLayout(
        title = "Expenses",
        subtitle = "Copenhagen Weekend · Recent shared costs.",
        showBottomBar = true,
        bottomBarDestination = AppRoute.Groups,
        onNavigate = onNavigate,
        onBack = onBack,
        backEnabled = backEnabled
    ) {
        MockupAction("Add expense", onClick = { onNavigate(AppRoute.AddExpense) })
        Spacer(modifier = Modifier.height(18.dp))
        MockupCard {
            Column(verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(18.dp)) {
                MockupInfoRow("Dinner", "Today · Paid by Alex", "DKK 320")
                MockupInfoRow("Train tickets", "Yesterday · Paid by Mia", "DKK 480")
                MockupInfoRow("Groceries", "Monday · Paid by Noah", "DKK 215")
            }
        }
    }
}
