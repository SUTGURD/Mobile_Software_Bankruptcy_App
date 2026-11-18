package com.example.application_0

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp

@Composable
fun CreateAccountScreen(
    onNavigate: (AppRoute) -> Unit,
    onBack: () -> Unit,
    backEnabled: Boolean
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    ScreenLayout(
        title = null,
        showBottomBar = false,
        onBack = onBack,
        backEnabled = backEnabled
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            "Bankruptcy",
            style = MaterialTheme.typography.headlineSmall,
            color = ForestGreen,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(96.dp))
        Text("Create your account.", color = MutedGreen)
        Spacer(modifier = Modifier.height(20.dp))
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            FormField(
                label = "Full name",
                value = name,
                onValueChange = { name = it }
            )
            FormField(
                label = "Email address",
                value = email,
                onValueChange = { email = it },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                )
            )
            FormField(
                label = "Password",
                value = password,
                onValueChange = { password = it },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                )
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            "At least 8 characters.",
            style = MaterialTheme.typography.bodySmall,
            color = MutedGreen
        )
        Spacer(modifier = Modifier.height(24.dp))
        ActionButton("Create account", enabled = false)
        Spacer(modifier = Modifier.height(10.dp))
        ActionButton(
            "Already a member? Log in",
            secondary = true,
            onClick = { onNavigate(AppRoute.Login) }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "By continuing, you agree to our terms and privacy policy.",
            style = MaterialTheme.typography.bodySmall,
            color = MutedGreen
        )
    }
}
