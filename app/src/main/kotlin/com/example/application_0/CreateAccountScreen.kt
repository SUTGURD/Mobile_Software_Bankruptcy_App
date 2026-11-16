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
        subtitle = "Account creation is not available with dummy data.",
        onBack = onBack,
        backEnabled = backEnabled
    ) {
        ScreenMessage("Log in with one of the existing dummy accounts to continue.")
        Spacer(modifier = Modifier.height(22.dp))
        MockupAction("Log in", onClick = { onNavigate(AppRoute.Login) })
    }
}
