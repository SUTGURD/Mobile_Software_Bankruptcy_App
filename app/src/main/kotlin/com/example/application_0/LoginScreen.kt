package com.example.application_0

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LoginScreen(onNavigate: (AppRoute) -> Unit, onBack: () -> Unit, backEnabled: Boolean) {
    MockupScreenLayout(
        title = "Log in",
        subtitle = "Welcome back. Enter your details below.",
        onBack = onBack,
        backEnabled = backEnabled
    ) {
        MockupField("Email", "name@example.com")
        Spacer(modifier = Modifier.height(16.dp))
        MockupField("Password", "Enter your password")
        Spacer(modifier = Modifier.height(24.dp))
        MockupAction("Log in", onClick = { onNavigate(AppRoute.Home) })
        Spacer(modifier = Modifier.height(18.dp))
        MockupAction(
            "Create account",
            secondary = true,
            onClick = { onNavigate(AppRoute.CreateAccount) }
        )
        Spacer(modifier = Modifier.height(18.dp))
        MockupCaption("Forgot password?")
    }
}
