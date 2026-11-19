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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

internal fun accountCreationValidationMessage(
    name: String,
    email: String,
    password: String
): String? {
    val missingFields = buildList {
        if (name.trim().isEmpty()) add("full name")
        if (email.trim().isEmpty()) add("email address")
        if (password.isEmpty()) add("password")
    }

    val messages = mutableListOf<String>()
    if (missingFields.isNotEmpty()) {
        messages += "Missing ${missingFields.joinToString(", ")}."
    }
    if (name.trim().isNotEmpty() && name.trim().length < 2) {
        messages += "Full name must be at least 2 characters."
    }
    if (email.trim().isNotEmpty() &&
        !Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+\$").matches(email.trim())
    ) {
        messages += "Enter a valid email address."
    }
    if (password.isNotEmpty() && password.length < 8) {
        messages += "Password must be at least 8 characters."
    }
    return messages.joinToString("\n").ifEmpty { null }
}

@Composable
fun CreateAccountScreen(
    onNavigate: (AppRoute) -> Unit,
    onBack: () -> Unit,
    backEnabled: Boolean,
    onCreateAccount: suspend (String, String, String) -> AccountCreationResult = Authentication::createAccount,
    onCreated: () -> Unit = {}
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

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
                onValueChange = {
                    name = it
                    errorMessage = null
                }
            )
            FormField(
                label = "Email address",
                value = email,
                onValueChange = {
                    email = it
                    errorMessage = null
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                )
            )
            FormField(
                label = "Password",
                value = password,
                onValueChange = {
                    password = it
                    errorMessage = null
                },
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
        errorMessage?.let { message ->
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
        ActionButton(
            "Create account",
            onClick = {
                val validationMessage =
                    accountCreationValidationMessage(name, email, password)
                if (validationMessage != null) {
                    errorMessage = validationMessage
                } else {
                    coroutineScope.launch {
                        errorMessage = null
                        when (onCreateAccount(name, email, password)) {
                            AccountCreationResult.CREATED -> onCreated()
                            AccountCreationResult.INVALID_DETAILS ->
                                errorMessage = "Enter a name, valid email address, and a password of at least 8 characters."
                            AccountCreationResult.EMAIL_EXISTS ->
                                errorMessage = "An account already exists for this email address."
                            AccountCreationResult.DATABASE_ERROR ->
                                errorMessage = "The account could not be created. Please try again."
                        }
                    }
                }
            }
        )
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
