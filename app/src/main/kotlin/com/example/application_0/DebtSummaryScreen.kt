package com.example.application_0

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.util.Locale

@Composable
fun DebtSummaryScreen(
    onNavigate: (AppRoute) -> Unit,
    onBack: () -> Unit,
    backEnabled: Boolean,
    groupsViewModel: GroupsViewModel
) {
    val debtViewModel: DebtSummaryViewModel = viewModel()
    val groupsState by groupsViewModel.uiState.collectAsState()
    val debtState by debtViewModel.uiState.collectAsState()
    val activeGroupId by ActiveGroup.groupId.collectAsState()
    val group = groupsState.selectedGroupOrFirst(activeGroupId)
    val userId = Authentication.currentUser.collectAsState().value?.userId
    var reminderMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(group?.id, userId) {
        if (group != null && userId != null) {
            ActiveGroup.select(group.id)
            debtViewModel.calculateDebts(
                listOf(
                    DebtRequest(DebtType.GROUP_OWED, userId, group.id),
                    DebtRequest(DebtType.GROUP_OWING, userId, group.id)
                ) + group.members
                    .filter { it.userId != userId }
                    .flatMap { member ->
                        listOf(
                            DebtRequest(DebtType.OWED_BY_USER, userId, group.id, member.userId),
                            DebtRequest(DebtType.OWING_TO_USER, userId, group.id, member.userId)
                        )
                    }
            )
        }
    }

    ScreenLayout(
        title = "Debt summary",
        showBottomBar = true,
        bottomBarDestination = AppRoute.Groups,
        onNavigate = onNavigate,
        onBack = onBack,
        backEnabled = backEnabled,
        bottomContent = {
            ActionButton("Back to group", secondary = true, onClick = onBack)
        }
    ) {
        ScreenDataStatus(groupsState.isLoading || debtState.isLoading, groupsState.error ?: debtState.error)
        if (group != null) {
            Spacer(modifier = Modifier.height(112.dp))
            val amountOwed = debtState.debtResults.firstOrNull {
                it.debtType == DebtType.GROUP_OWING
            }?.result ?: 0.0
            val amountToPay = debtState.debtResults.firstOrNull {
                it.debtType == DebtType.GROUP_OWED
            }?.result ?: 0.0
            val netBalance = amountOwed - amountToPay
            val owing = netBalance < 0.0
            BalanceCard(
                label = if (owing) "You owe · ${group.currency}" else "You are owed · ${group.currency}",
                amount = (if (netBalance > 0.0) "+" else if (owing) "-" else "") +
                    String.format(Locale.UK, "%,.0f", kotlin.math.abs(netBalance)),
                detail = "",
                action = "",
                currency = "",
                owing = owing,
                onClick = {}
            )
            Spacer(modifier = Modifier.height(28.dp))
            Text(
                text = if (owing) "PAYMENTS YOU OWE" else "PAYMENTS TO YOU",
                style = MaterialTheme.typography.labelSmall,
                color = MutedGreen
            )
            Spacer(modifier = Modifier.height(12.dp))
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                group.members
                    .filter { it.userId != userId }
                    .forEach { member ->
                        val amount = debtState.debtResults.firstOrNull {
                            it.debtType ==
                                (if (owing) DebtType.OWING_TO_USER else DebtType.OWED_BY_USER) &&
                                it.otherUserId == member.userId
                        }?.result ?: 0.0
                        Card(contentPadding = 14.dp) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (owing) "You → ${member.name.substringBefore(' ')}"
                                        else "${member.name.substringBefore(' ')} → You",
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Medium,
                                        color = ForestGreen
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = group.name,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MutedGreen
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = String.format(Locale.UK, "%,.0f", amount),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = ForestGreen
                                    )
                                    Text(
                                        text = group.currency,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MutedGreen
                                    )
                                    if (!owing && amount > 0.0) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Send reminder",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.SemiBold,
                                            color = ForestGreen,
                                            modifier = Modifier
                                                .testTag("send_reminder_${member.userId}")
                                                .clickable {
                                                    debtViewModel.sendReminder(group.id, member.userId) { _, message ->
                                                        reminderMessage = message
                                                    }
                                                }
                                        )
                                    }
                                }
                            }
                        }
                    }
            }
            reminderMessage?.let { msg ->
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = msg,
                    style = MaterialTheme.typography.bodySmall,
                    color = ForestGreen,
                    modifier = Modifier.testTag("reminder_feedback_message")
                )
            }
        } else if (!groupsState.isLoading && groupsState.error == null) {
            ScreenMessage("You are not a member of any groups.")
        }
    }
}
