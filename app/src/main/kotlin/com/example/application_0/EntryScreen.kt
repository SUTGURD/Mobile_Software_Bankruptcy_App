package com.example.application_0

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun EntryScreen(onNavigate: (AppRoute) -> Unit) {
    ScreenLayout(showBottomBar = false, scrollContent = false) {
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.height(30.dp))
            Text(
                "Bankruptcy",
                style = MaterialTheme.typography.headlineMedium,
                color = ForestGreen,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "Debt’s better\nwhen it’s shared.",
                style = MaterialTheme.typography.headlineLarge,
                color = ForestGreen,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(72.dp))
            ActionButton("Get started", onClick = { onNavigate(AppRoute.CreateAccount) })
            Spacer(modifier = Modifier.height(12.dp))
            ActionButton(
                "I already have an account",
                secondary = true,
                onClick = { onNavigate(AppRoute.Login) }
            )
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
