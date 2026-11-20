package com.example.application_0

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.util.Locale

@Composable
fun SplitExpenseScreen(
    onNavigate: (AppRoute) -> Unit,
    onBack: () -> Unit,
    backEnabled: Boolean,
    expenseFlowViewModel: ExpenseFlowViewModel,
    groupsViewModel: GroupsViewModel
) {
    val groupsState by groupsViewModel.uiState.collectAsState()
    val draft by expenseFlowViewModel.uiState.collectAsState()
    val activeGroupId by ActiveGroup.groupId.collectAsState()
    val group = groupsState.selectedGroupOrFirst(activeGroupId)
    val userId = Authentication.currentUser.collectAsState().value?.userId
    val amount = draft.amount.toDoubleOrNull() ?: 0.0
    val memberCount = group?.members?.size ?: 0
    val share = if (memberCount == 0) 0.0 else amount / memberCount

    ScreenLayout(
        title = "Split expense",
        showBottomBar = true,
        bottomBarDestination = AppRoute.Groups,
        onNavigate = onNavigate,
        onBack = onBack,
        backEnabled = backEnabled,
        bottomContent = {
            ActionButton("Done", onClick = onBack)
        }
    ) {
        Spacer(modifier = Modifier.height(112.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(PaleGreen, CircleShape)
                .padding(horizontal = 16.dp, vertical = 13.dp)
        ) {
            Text(
                text = "✓  Split equally between $memberCount people",
                style = MaterialTheme.typography.bodyMedium,
                color = ForestGreen
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "${draft.description} · ${formatCompactAmount(amount)} ${group?.currency ?: "DKK"}",
            style = MaterialTheme.typography.bodyMedium,
            color = MutedGreen
        )
        Spacer(modifier = Modifier.height(18.dp))
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            group?.members.orEmpty().forEach { member ->
                Card(contentPadding = 14.dp) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (member.userId == userId) "${member.name} (you)" else member.name,
                            style = MaterialTheme.typography.bodyMedium,
                            color = ForestGreen
                        )
                        Text(
                            text = "${formatCompactAmount(share)} ${group?.currency ?: "DKK"}",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = ForestGreen
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(28.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Total allocated", style = MaterialTheme.typography.bodySmall, color = MutedGreen)
            Text(
                "${formatCompactAmount(share * memberCount)} of ${formatCompactAmount(amount)} ${group?.currency ?: "DKK"}",
                style = MaterialTheme.typography.labelLarge,
                color = ForestGreen,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun UploadReceiptScreen(
    onNavigate: (AppRoute) -> Unit,
    onBack: () -> Unit,
    backEnabled: Boolean,
    expenseFlowViewModel: ExpenseFlowViewModel,
    groupsViewModel: GroupsViewModel
) {
    val groupsState by groupsViewModel.uiState.collectAsState()
    val activeGroupId by ActiveGroup.groupId.collectAsState()
    val group = groupsState.selectedGroupOrFirst(activeGroupId)
    val expenseFlowState by expenseFlowViewModel.uiState.collectAsState()

    ScreenLayout(
        title = "Upload receipt",
        showBottomBar = true,
        bottomBarDestination = AppRoute.Groups,
        onNavigate = onNavigate,
        onBack = onBack,
        backEnabled = backEnabled,
        bottomContent = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                ActionButton(
                    if (expenseFlowState.receiptAttached) "Receipt attached" else "Attach sample receipt",
                    enabled = !expenseFlowState.receiptAttached,
                    onClick = {
                        expenseFlowViewModel.attachSampleReceipt()
                        onBack()
                    }
                )
                ActionButton(
                    "Continue without receipt",
                    secondary = true,
                    onClick = {
                        expenseFlowViewModel.clearReceipt()
                        onBack()
                    }
                )
            }
        }
    ) {
        Spacer(modifier = Modifier.height(112.dp))
        ReceiptPreview(
            merchant = "ÆDEHUS",
            description = expenseFlowState.description,
            amount = expenseFlowState.amount.toDoubleOrNull() ?: 480.0,
            currency = group?.currency ?: "DKK"
        )
    }
}

@Composable
fun ViewReceiptScreen(
    onNavigate: (AppRoute) -> Unit,
    onBack: () -> Unit,
    backEnabled: Boolean,
    expenseFlowViewModel: ExpenseFlowViewModel,
    groupsViewModel: GroupsViewModel
) {
    val expenseFlowState by expenseFlowViewModel.uiState.collectAsState()
    val groupsState by groupsViewModel.uiState.collectAsState()
    val activeGroupId by ActiveGroup.groupId.collectAsState()
    val group = groupsState.selectedGroupOrFirst(activeGroupId)
    val expense = expenseFlowState.selectedExpense

    ScreenLayout(
        title = "View receipt",
        showBottomBar = true,
        bottomBarDestination = AppRoute.Groups,
        onNavigate = onNavigate,
        onBack = onBack,
        backEnabled = backEnabled
    ) {
        Spacer(modifier = Modifier.height(112.dp))
        if (expense != null) {
            ReceiptPreview(
                merchant = "ÆDEHUS",
                description = expense.description ?: "Expense",
                amount = expense.amount,
                currency = group?.currency ?: "DKK",
                date = formatDate(expense.createdAt)
            )
        } else {
            ScreenMessage("No receipt is selected.")
        }
    }
}

@Composable
fun ExpenseSavedScreen(
    onNavigate: (AppRoute) -> Unit,
    onBack: () -> Unit,
    backEnabled: Boolean,
    expenseFlowViewModel: ExpenseFlowViewModel,
    groupsViewModel: GroupsViewModel
) {
    val groupsState by groupsViewModel.uiState.collectAsState()
    val activeGroupId by ActiveGroup.groupId.collectAsState()
    val group = groupsState.selectedGroupOrFirst(activeGroupId)
    val draft by expenseFlowViewModel.uiState.collectAsState()
    val amount = draft.amount.toDoubleOrNull() ?: 0.0
    val memberCount = group?.memberCount ?: 0
    val share = if (memberCount == 0) 0.0 else amount / memberCount
    val amountOwed = amount - share

    ScreenLayout(
        title = "Expense saved",
        showBottomBar = true,
        bottomBarDestination = AppRoute.Groups,
        onNavigate = onNavigate,
        onBack = onBack,
        backEnabled = backEnabled,
        bottomContent = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                ActionButton(
                    "View group expenses",
                    onClick = { onNavigate(AppRoute.Expenses) }
                )
                ActionButton(
                    "Back to group",
                    secondary = true,
                    onClick = { onNavigate(AppRoute.GroupDetails) }
                )
            }
        }
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .background(PaleGreen, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "✓",
                    style = MaterialTheme.typography.headlineLarge,
                    color = ForestGreen
                )
            }
        }
        Spacer(modifier = Modifier.height(112.dp))
        Text(
            text = "${draft.description} has been added to ${group?.name ?: "your group"}.",
            style = MaterialTheme.typography.bodyLarge,
            color = MutedGreen
        )
        Spacer(modifier = Modifier.height(32.dp))
        Card(contentPadding = 18.dp) {
            Text(
                text = "${formatCompactAmount(amount)} ${group?.currency ?: "DKK"}",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                color = ForestGreen
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Paid by you · Split equally",
                style = MaterialTheme.typography.bodyMedium,
                color = MutedGreen
            )
            Spacer(modifier = Modifier.height(18.dp))
            Text(
                text = "Your share ${formatCompactAmount(share)} ${group?.currency ?: "DKK"} · You are owed ${formatCompactAmount(amountOwed)} ${group?.currency ?: "DKK"}",
                style = MaterialTheme.typography.bodySmall,
                color = ForestGreen
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun ReceiptPreview(
    merchant: String,
    description: String,
    amount: Double,
    currency: String,
    date: String = "16 Nov 2026"
) {
    val firstItem = amount * 5.0 / 6.0
    val secondItem = amount - firstItem
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .background(Color.White, MaterialTheme.shapes.medium)
            .padding(horizontal = 20.dp, vertical = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = merchant,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            color = ForestGreen
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "COPENHAGEN · ${date.uppercase(Locale.UK)}",
            style = MaterialTheme.typography.labelSmall,
            color = MutedGreen
        )
        Spacer(modifier = Modifier.height(20.dp))
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0xFFE1E7DE)))
        Spacer(modifier = Modifier.height(20.dp))
        ReceiptLine(description, firstItem, currency)
        Spacer(modifier = Modifier.height(18.dp))
        ReceiptLine("Drinks", secondItem, currency)
        Spacer(modifier = Modifier.height(20.dp))
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0xFFE1E7DE)))
        Spacer(modifier = Modifier.height(20.dp))
        ReceiptLine("TOTAL", amount, currency, emphasized = true)
        Spacer(modifier = Modifier.height(22.dp))
        Text(
            text = "Paid by card · Thank you!",
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.labelSmall,
            color = MutedGreen
        )
    }
}

@Composable
private fun ReceiptLine(
    label: String,
    amount: Double,
    currency: String,
    emphasized: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = if (emphasized) MaterialTheme.typography.titleSmall else MaterialTheme.typography.bodySmall,
            fontWeight = if (emphasized) FontWeight.SemiBold else FontWeight.Normal,
            color = ForestGreen
        )
        Text(
            text = "${formatCompactAmount(amount)} $currency",
            style = if (emphasized) MaterialTheme.typography.titleSmall else MaterialTheme.typography.bodySmall,
            fontWeight = if (emphasized) FontWeight.SemiBold else FontWeight.Normal,
            color = ForestGreen
        )
    }
}

private fun formatCompactAmount(amount: Double): String =
    String.format(Locale.UK, "%,.0f", amount)
