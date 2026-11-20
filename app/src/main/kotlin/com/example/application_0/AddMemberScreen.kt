package com.example.application_0

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import data.database.MembershipMutationResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun AddMemberScreen(
    onNavigate: (AppRoute) -> Unit,
    onBack: () -> Unit,
    backEnabled: Boolean,
    groupsViewModel: GroupsViewModel,
    invitationsViewModel: GroupInvitationsViewModel
) {
    val groupsState by groupsViewModel.uiState.collectAsState()
    val activeGroupId by ActiveGroup.groupId.collectAsState()
    val group = groupsState.selectedGroupOrFirst(activeGroupId)
    var email by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    ScreenLayout(
        title = "Add member",
        showBottomBar = true,
        bottomBarDestination = AppRoute.Groups,
        onNavigate = onNavigate,
        onBack = onBack,
        backEnabled = backEnabled,
        bottomContent = {
            ActionButton(
                "Send invitation",
                enabled = group != null && email.contains('@'),
                onClick = {
                    val selectedGroup = group ?: return@ActionButton
                    coroutineScope.launch {
                        val result = invitationsViewModel.createInvitation(selectedGroup.id, email)
                        withContext(Dispatchers.Main.immediate) {
                            when (result) {
                            MembershipMutationResult.SUCCESS -> {
                                error = null
                                invitationsViewModel.refresh()
                                groupsViewModel.refresh()
                                onBack()
                            }
                            MembershipMutationResult.INVALID_INVITEE ->
                                error = "No account matches this email address."
                            MembershipMutationResult.ALREADY_MEMBER ->
                                error = "This person is already a group member."
                            MembershipMutationResult.ALREADY_INVITED ->
                                error = "An invitation has already been sent to this person."
                            MembershipMutationResult.NOT_ALLOWED ->
                                error = "You do not have permission to invite members."
                            else -> error = "The invitation could not be sent."
                            }
                        }
                    }
                }
            )
        }
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        SectionTitle("Invite debtor")
        Spacer(modifier = Modifier.height(52.dp))
        ScreenMessage("Invite someone to ${group?.name ?: "your group"}.")
        Spacer(modifier = Modifier.height(34.dp))
        FormField(
            label = "Friend's email",
            value = email,
            onValueChange = { email = it },
            placeholder = "name@example.com",
            modifier = Modifier.testTag("group_invitation_email")
        )
        error?.let { message ->
            Spacer(modifier = Modifier.height(12.dp))
            androidx.compose.material3.Text(
                text = message,
                color = androidx.compose.material3.MaterialTheme.colorScheme.error,
                style = androidx.compose.material3.MaterialTheme.typography.bodySmall
            )
        }
        Spacer(modifier = Modifier.height(36.dp))
        Card(color = SoftGreen, contentPadding = 18.dp) {
            androidx.compose.material3.Text(
                text = email.substringBefore('@')
                    .split('.', ' ')
                    .joinToString(" ") { it.replaceFirstChar { character -> character.uppercase() } }
                    .ifBlank { "New member" },
                style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                color = ForestGreen
            )
            Spacer(modifier = Modifier.height(10.dp))
            androidx.compose.material3.Text(
                text = "They will receive an invitation to join your group. Existing expenses will not be changed.",
                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                color = MutedGreen
            )
        }
    }
}
