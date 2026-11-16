package com.example.application_0

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CreateAccountScreen(
    onNavigate: (AppRoute) -> Unit,
    onBack: () -> Unit,
    backEnabled: Boolean
) {
    MockupScreenLayout(
        title = "Create account",
        subtitle = "Set up your details to get started.",
        onBack = onBack,
        backEnabled = backEnabled
    ) {
        MockupField("Name", "Your full name")
        Spacer(modifier = Modifier.height(14.dp))
        MockupField("Email", "name@example.com")
        Spacer(modifier = Modifier.height(14.dp))
        MockupField("Phone number", "Optional")
        Spacer(modifier = Modifier.height(14.dp))
        MockupField("Password", "Create a password")
        Spacer(modifier = Modifier.height(22.dp))
        MockupAction("Create account", onClick = { onNavigate(AppRoute.Home) })
    }
}
