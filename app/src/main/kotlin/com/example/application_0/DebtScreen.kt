package com.example.application_0

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DebtScreen(onNavigate: (AppRoute) -> Unit, onBack: () -> Unit, backEnabled: Boolean) {
    MockupScreenLayout(
        title = "Debt",
        subtitle = "A quick view of your outstanding balances.",
        showBottomBar = true,
        bottomBarDestination = AppRoute.Debt,
        onNavigate = onNavigate,
        onBack = onBack,
        backEnabled = backEnabled
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MockupAmountCard("You owe", "DKK 245", modifier = Modifier.weight(1f))
            MockupAmountCard("Owed to you", "DKK 680", positive = true, modifier = Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(24.dp))
        MockupSectionTitle("Balances with people")
        Spacer(modifier = Modifier.height(12.dp))
        MockupCard {
            Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
                MockupInfoRow("Mia Jensen", "You owe Mia", "DKK 145")
                MockupInfoRow("Noah Larsen", "Noah owes you", "DKK 420", accent = true)
                MockupInfoRow("Sofie Holm", "Sofie owes you", "DKK 260", accent = true)
            }
        }
    }
}
