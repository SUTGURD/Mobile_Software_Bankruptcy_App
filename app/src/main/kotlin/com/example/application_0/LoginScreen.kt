package com.example.application_0

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun LoginScreen(
    onNavigate: (AppRoute) -> Unit,
    onLogin: suspend (String, String) -> LoginResult,
    onAuthenticated: () -> Unit,
    onBack: () -> Unit,
    backEnabled: Boolean
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loginFailed by remember { mutableStateOf(false) }
    var resetSucceeded by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    ScreenLayout(
        showBottomBar = false,
        onBack = onBack,
        backEnabled = backEnabled,
        scrollContent = false
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.weight(1.2f))
            FormField(
                label = "Email address",
                value = email,
                onValueChange = {
                    email = it
                    loginFailed = false
                    resetSucceeded = false
                },
                modifier = Modifier.testTag("login_email"),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            FormField(
                label = "Password",
                value = password,
                onValueChange = {
                    password = it
                    loginFailed = false
                    resetSucceeded = false
                },
                modifier = Modifier.testTag("login_password"),
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
            if (resetSucceeded) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Database reset to dummy data.",
                    color = ForestGreen
                )
            }
            Spacer(modifier = Modifier.height(72.dp))
            ActionButton(
                "Log in",
                onClick = {
                    coroutineScope.launch {
                        when (onLogin(email, password)) {
                            LoginResult.AUTHENTICATED ->
                                withContext(Dispatchers.Main) { onAuthenticated() }
                            LoginResult.DATABASE_RESET -> {
                                loginFailed = false
                                resetSucceeded = true
                            }
                            LoginResult.INVALID_CREDENTIALS -> {
                                resetSucceeded = false
                                loginFailed = true
                            }
                        }
                    }
                }
            )
            Spacer(modifier = Modifier.height(12.dp))
            ActionButton(
                "New here? Create an account",
                secondary = true,
                onClick = { onNavigate(AppRoute.CreateAccount) }
            )
            Spacer(modifier = Modifier.weight(0.8f))
        }
    }
}
