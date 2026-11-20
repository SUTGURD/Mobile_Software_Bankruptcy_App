package com.example.application_0

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.runtime.rememberCoroutineScope

internal fun groupCreationValidationMessage(name: String, type: String): String? {
    return if (name.trim().isEmpty()) "Missing group name." else null
}

@Composable
fun CreateGroupScreen(
    onNavigate: (AppRoute) -> Unit,
    onBack: () -> Unit,
    backEnabled: Boolean,
    groupsViewModel: GroupsViewModel
) {
    var groupName by remember { mutableStateOf("") }
    var groupType by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isCreating by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    ScreenLayout(
        title = "Create group",
        showBottomBar = true,
        bottomBarDestination = AppRoute.Groups,
        onNavigate = onNavigate,
        onBack = onBack,
        backEnabled = backEnabled,
        bottomContent = {
            ActionButton(
                "Create group",
                enabled = !isCreating,
                onClick = {
                    val trimmedName = groupName.trim()
                    val trimmedType = groupType.trim()
                    val validationMessage =
                        groupCreationValidationMessage(trimmedName, trimmedType)
                    if (validationMessage != null) {
                        errorMessage = validationMessage
                    } else {
                        isCreating = true
                        errorMessage = null
                        coroutineScope.launch {
                            val groupId = groupsViewModel.createGroup(trimmedName, trimmedType)
                            isCreating = false
                            if (groupId != null) {
                                withContext(Dispatchers.Main) { onBack() }
                            } else {
                                errorMessage = "The group could not be created."
                            }
                        }
                    }
                }
            )
        }
    ) {
        Spacer(modifier = Modifier.height(112.dp))
        FormField(
            label = "Group name",
            value = groupName,
            onValueChange = { groupName = it },
            placeholder = "Enter a group name"
        )
        Spacer(modifier = Modifier.height(14.dp))
        FormField(
            label = "Group type",
            value = groupType,
            onValueChange = { groupType = it },
            placeholder = "Optional"
        )
        Spacer(modifier = Modifier.height(14.dp))
        androidx.compose.material3.Text(
            text = "Currency",
            style = androidx.compose.material3.MaterialTheme.typography.labelMedium,
            color = MutedGreen
        )
        Spacer(modifier = Modifier.height(6.dp))
        Card(contentPadding = 14.dp) {
            androidx.compose.material3.Text(
                text = "DKK · Danish krone",
                color = ForestGreen
            )
        }
        errorMessage?.let { message ->
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}
