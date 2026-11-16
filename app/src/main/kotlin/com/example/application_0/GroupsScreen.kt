package com.example.application_0

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun GroupsScreen(onNavigate: (AppRoute) -> Unit, onBack: () -> Unit, backEnabled: Boolean) {
    MockupScreenLayout(
        title = "Groups",
        subtitle = "Shared spaces for the people and plans in your life.",
        showBottomBar = true,
        bottomBarDestination = AppRoute.Groups,
        onNavigate = onNavigate,
        onBack = onBack,
        backEnabled = backEnabled
    ) {
        MockupAction("Create group", onClick = { onNavigate(AppRoute.CreateGroup) })
        Spacer(modifier = Modifier.height(18.dp))
        MockupCard {
            Column(verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(18.dp)) {
                MockupInfoRow(
                    "Copenhagen Weekend",
                    "4 members · 8 expenses",
                    "DKK 180",
                    accent = true,
                    onClick = { onNavigate(AppRoute.GroupDetails) }
                )
                MockupInfoRow(
                    "Flat expenses",
                    "3 members · 12 expenses",
                    "DKK 65",
                    onClick = { onNavigate(AppRoute.GroupDetails) }
                )
                MockupInfoRow(
                    "Summer trip",
                    "5 members · 3 expenses",
                    "DKK 0",
                    onClick = { onNavigate(AppRoute.GroupDetails) }
                )
            }
        }
    }
}
