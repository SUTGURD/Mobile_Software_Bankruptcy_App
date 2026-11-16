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
fun HomeScreen(onNavigate: (AppRoute) -> Unit) {
    MockupScreenLayout(
        title = "Good morning, Alex",
        subtitle = "Here is your shared spending overview.",
        showBottomBar = true,
        bottomBarDestination = AppRoute.Home,
        onNavigate = onNavigate
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MockupAmountCard("You owe", "DKK 245", modifier = Modifier.weight(1f))
            MockupAmountCard("You are owed", "DKK 680", positive = true, modifier = Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(24.dp))
        MockupSectionTitle("Your groups")
        Spacer(modifier = Modifier.height(12.dp))
        MockupCard {
            Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
                MockupInfoRow(
                    "Copenhagen Weekend",
                    "4 members · Updated today",
                    "DKK 180",
                    onClick = { onNavigate(AppRoute.GroupDetails) }
                )
                MockupInfoRow(
                    "Flat expenses",
                    "3 members · Updated yesterday",
                    "DKK 65",
                    onClick = { onNavigate(AppRoute.GroupDetails) }
                )
            }
        }
        Spacer(modifier = Modifier.height(22.dp))
        MockupSectionTitle("Recent activity")
        Spacer(modifier = Modifier.height(12.dp))
        MockupCard {
            MockupInfoRow("Dinner", "Copenhagen Weekend · Alex paid", "DKK 320")
        }
        Spacer(modifier = Modifier.height(12.dp))
        MockupAction(
            "View notifications",
            secondary = true,
            onClick = { onNavigate(AppRoute.Notifications) }
        )
    }
}
