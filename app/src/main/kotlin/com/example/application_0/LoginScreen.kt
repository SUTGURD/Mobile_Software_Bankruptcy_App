package com.example.application_0

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun LoginScreen(
    onNavigate: (AppRoute) -> Unit,
    onLogin: (String, String) -> Boolean,
    onAuthenticated: () -> Unit,
    onBack: () -> Unit,
    backEnabled: Boolean
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loginFailed by remember { mutableStateOf(false) }

    MockupScreenLayout(
        title = "Log in",
        subtitle = "Welcome back. Enter your details below.",
        onBack = onBack,
        backEnabled = backEnabled
    ) {
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                loginFailed = false
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("login_email"),
            label = { Text("Email") },
            placeholder = { Text("name@example.com") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                loginFailed = false
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("login_password"),
            label = { Text("Password") },
            placeholder = { Text("Enter your password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            )
        )
        if (loginFailed) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Email or password is incorrect.",
                color = MaterialTheme.colorScheme.error
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        MockupAction(
            "Log in",
            onClick = {
                if (onLogin(email, password)) {
                    onAuthenticated()
                } else {
                    loginFailed = true
                }
            }
        )
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
