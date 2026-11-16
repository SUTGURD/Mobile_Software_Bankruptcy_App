package com.example.application_0

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun DebtSummaryScreen(
    onNavigate: (AppRoute) -> Unit,
    onBack: () -> Unit,
    backEnabled: Boolean
) {
    MockupScreenLayout(
        title = "Debt summary",
        subtitle = "Copenhagen Weekend · Group balances",
        showBottomBar = true,
        bottomBarDestination = AppRoute.Groups,
        onNavigate = onNavigate,
        onBack = onBack,
        backEnabled = backEnabled
    ) {
        MockupCard {
            Column {
                Text("Total group expenses", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF69758B))
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "DKK 1,240",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color(0xFF17233C),
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        MockupSectionTitle("Who owes whom")
        Spacer(modifier = Modifier.height(12.dp))
        MockupCard {
            Column(verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(18.dp)) {
                MockupInfoRow("Mia Jensen", "Owes Alex Morgan", "DKK 145")
                MockupInfoRow("Noah Larsen", "Owes Mia Jensen", "DKK 275")
                MockupInfoRow("Sofie Holm", "Owes Alex Morgan", "DKK 90")
            }
        }
    }
}
