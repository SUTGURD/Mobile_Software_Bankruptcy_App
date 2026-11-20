package com.example.application_0

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.Dispatchers
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun AddExpenseScreen(
    onNavigate: (AppRoute) -> Unit,
    onBack: () -> Unit,
    backEnabled: Boolean,
    expenseFlowViewModel: ExpenseFlowViewModel,
    groupsViewModel: GroupsViewModel
) {
    val groupsState by groupsViewModel.uiState.collectAsState()
    val activeGroupId by ActiveGroup.groupId.collectAsState()
    val group = groupsState.selectedGroupOrFirst(activeGroupId)
    val profileViewModel: UserProfileViewModel = viewModel()
    val profileState by profileViewModel.uiState.collectAsState()
    val draft by expenseFlowViewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val amount = draft.amount
    val description = draft.description
    val canSave = group != null &&
        group.memberCount > 0 &&
        !draft.isSaving &&
        draft.savedExpenseId == null

    ScreenLayout(
        title = "Add expense",
        onNavigate = onNavigate,
        bottomBarDestination = AppRoute.Groups,
        onBack = onBack,
        backEnabled = backEnabled,
        bottomContent = {
            ActionButton(
                if (draft.isSaving) "Saving..." else "Save expense",
                enabled = canSave,
                onClick = {
                    group?.let { selectedGroup ->
                        coroutineScope.launch {
                            if (expenseFlowViewModel.saveExpense(selectedGroup.id)) {
                                withContext(Dispatchers.Main.immediate) {
                                    onNavigate(AppRoute.ExpenseSaved)
                                }
                            }
                        }
                    }
                }
            )
        }
    ) {
        ScreenDataStatus(groupsState.isLoading, groupsState.error)
        if (group == null && !groupsState.isLoading && groupsState.error == null) {
            ScreenMessage("Join a group before adding an expense.")
        }
        Text(
            group?.name?.uppercase(Locale.UK) ?: "GROUP",
            style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
            color = MutedGreen
        )
        Spacer(modifier = Modifier.height(8.dp))
        Card(color = SoftGreen) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "TOTAL AMOUNT",
                        style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
                        color = MutedGreen
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    BasicTextField(
                        value = amount,
                        onValueChange = expenseFlowViewModel::updateAmount,
                        singleLine = true,
                        textStyle = androidx.compose.material3.MaterialTheme.typography.headlineMedium.copy(
                            color = ForestGreen,
                            fontWeight = FontWeight.Medium
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        decorationBox = { innerTextField ->
                            if (amount.isEmpty()) {
                                Text(
                                    "480.00",
                                    style = androidx.compose.material3.MaterialTheme.typography.headlineMedium,
                                    color = ForestGreen
                                )
                            }
                            innerTextField()
                        }
                    )
                }
                Text(group?.currency ?: "DKK", style = androidx.compose.material3.MaterialTheme.typography.bodySmall, color = MutedGreen)
            }
        }
        Spacer(modifier = Modifier.height(18.dp))
        FormField(
            label = "Description",
            value = description,
            onValueChange = expenseFlowViewModel::updateDescription,
            placeholder = "",
            modifier = Modifier.testTag("expense_description")
        )
        Spacer(modifier = Modifier.height(12.dp))
        ExpenseValueCard("Paid by", "You · ${profileState.profile?.name ?: "Current user"}")
        Spacer(modifier = Modifier.height(10.dp))
        ExpenseValueCard(
            "Date",
            LocalDate.now().format(DateTimeFormatter.ofPattern("d MMM yyyy", Locale.UK))
        )
        Spacer(modifier = Modifier.height(10.dp))
        val memberCount = group?.memberCount ?: 0
        val amountPerMember = amount.toDoubleOrNull()?.let { value ->
            if (memberCount > 0) formatCompactAmount(value / memberCount) else "0"
        } ?: "0"
        Card(
            modifier = Modifier.clickable(
                role = Role.Button,
                onClick = { onNavigate(AppRoute.SplitExpense) }
            )
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Split equally", color = ForestGreen)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "$memberCount people · $amountPerMember ${group?.currency ?: "DKK"} each",
                        style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                        color = MutedGreen
                    )
                }
                Icon(
                    painter = painterResource(R.drawable.ic_chevron_right),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = ForestGreen
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Card(
            modifier = Modifier.clickable(
                role = Role.Button,
                onClick = { onNavigate(AppRoute.UploadReceipt) }
            )
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Attach a receipt", modifier = Modifier.weight(1f), color = ForestGreen)
                if (draft.receiptAttached) {
                    androidx.compose.material3.Surface(
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
                        color = PaleGreen
                    ) {
                        Text(
                            text = "✓  Receipt attached",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                            style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
                            color = ForestGreen
                        )
                    }
                } else {
                    Icon(
                        painter = painterResource(R.drawable.ic_add),
                        contentDescription = "Add a receipt",
                        modifier = Modifier.size(20.dp),
                        tint = ForestGreen
                    )
                }
            }
        }
        draft.saveError?.let { message ->
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = message,
                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                color = androidx.compose.material3.MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
private fun ExpenseValueCard(label: String, value: String) {
    Column {
        Text(label, style = androidx.compose.material3.MaterialTheme.typography.labelMedium, color = MutedGreen)
        Spacer(modifier = Modifier.height(6.dp))
        Card {
            Text(value, color = ForestGreen)
        }
    }
}

private fun formatCompactAmount(amount: Double): String =
    java.lang.String.format(Locale.UK, "%,.0f", amount)
