package com.example.application_0

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
fun ProfileScreen(onNavigate: (AppRoute) -> Unit, onBack: () -> Unit, backEnabled: Boolean) {
    MockupScreenLayout(
        title = "Profile",
        subtitle = "Your account details and preferences.",
        showBottomBar = true,
        bottomBarDestination = AppRoute.Profile,
        onNavigate = onNavigate,
        onBack = onBack,
        backEnabled = backEnabled
    ) {
        MockupCard {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                BrandMark(modifier = Modifier.size(72.dp))
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "Alex Morgan",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color(0xFF17233C),
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "alex.morgan@example.com",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF69758B)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        MockupCard {
            Column(verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(18.dp)) {
                MockupInfoRow("Phone number", "+45 12 34 56 78")
                MockupInfoRow("Notifications", "Enabled")
                MockupInfoRow("Password", "Last updated recently")
            }
        }
    }
}
