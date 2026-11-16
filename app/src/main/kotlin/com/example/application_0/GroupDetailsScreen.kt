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
fun GroupDetailsScreen(onNavigate: (AppRoute) -> Unit, onBack: () -> Unit, backEnabled: Boolean) {
    MockupScreenLayout(
        title = "Copenhagen Weekend",
        subtitle = "Group details · 4 members",
        showBottomBar = true,
        bottomBarDestination = AppRoute.Groups,
        onNavigate = onNavigate,
        onBack = onBack,
        backEnabled = backEnabled
    ) {
        MockupCard {
            Column {
                Text("Group balance", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF69758B))
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "DKK 1,240",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color(0xFF17233C),
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text("Total expenses", style = MaterialTheme.typography.bodySmall, color = Color(0xFF69758B))
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        MockupAction("Members", secondary = true, onClick = { onNavigate(AppRoute.Members) })
        Spacer(modifier = Modifier.height(20.dp))
        MockupAction("Expenses", secondary = true, onClick = { onNavigate(AppRoute.Expenses) })
        Spacer(modifier = Modifier.height(12.dp))
        MockupAction("Debt summary", secondary = true, onClick = { onNavigate(AppRoute.DebtSummary) })
    }
}
