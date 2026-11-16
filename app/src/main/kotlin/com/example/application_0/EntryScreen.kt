package com.example.application_0

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun EntryScreen(onNavigate: (AppRoute) -> Unit) {
    MockupScreenLayout(title = "Shared expenses, made simple") {
        Spacer(modifier = Modifier.height(32.dp))
        MockupCard {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                BrandMark(modifier = Modifier.size(76.dp))
                Spacer(modifier = Modifier.height(22.dp))
                Text(
                    text = "Pennywise",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color(0xFF17233C),
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                MockupCaption("Keep shared spending clear and fair.")
            }
        }
        Spacer(modifier = Modifier.height(26.dp))
        MockupAction("Log in", onClick = { onNavigate(AppRoute.Login) })
        Spacer(modifier = Modifier.height(12.dp))
        MockupAction(
            "Create account",
            secondary = true,
            onClick = { onNavigate(AppRoute.CreateAccount) }
        )
    }
}
