package com.example.application_0

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.util.Locale
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun HomeScreen(onNavigate: (AppRoute) -> Unit, groupsViewModel: GroupsViewModel) {
    val profileViewModel: UserProfileViewModel = viewModel()
    val debtViewModel: DebtSummaryViewModel = viewModel()
    val profileState by profileViewModel.uiState.collectAsState()
    val groupsState by groupsViewModel.uiState.collectAsState()
    val debtState by debtViewModel.uiState.collectAsState()
    val groupIds = groupsState.groups.map { it.id }
    val debtError = debtState.error
    val groupsError = groupsState.error

    LaunchedEffect(profileState.profile?.id, groupIds) {
        val userId = profileState.profile?.id ?: return@LaunchedEffect
        debtViewModel.calculateDebts(
            listOf(
                DebtRequest(DebtType.TOTAL_OWED, userId),
                DebtRequest(DebtType.TOTAL_OWING, userId)
            ) + groupIds.flatMap { groupId ->
                listOf(
                    DebtRequest(DebtType.GROUP_OWED, userId, groupId),
                    DebtRequest(DebtType.GROUP_OWING, userId, groupId)
                )
            }
        )
    }

    ScreenLayout(
        bottomBarDestination = AppRoute.Home,
        onNavigate = onNavigate,
        bottomContent = {
            ActionButton(
                "Add an expense",
                leadingIcon = R.drawable.ic_add,
                onClick = { onNavigate(AppRoute.AddExpense) }
            )
        }
    ) {
        val profile = profileState.profile
        val currency = groupsState.groups.firstOrNull()?.currency ?: "DKK"
        val totalOwed = debtState.debtResults.firstOrNull { it.debtType == DebtType.TOTAL_OWED }?.result
        val totalOwing = debtState.debtResults.firstOrNull { it.debtType == DebtType.TOTAL_OWING }?.result
        val netBalance = (totalOwing ?: 0.0) - (totalOwed ?: 0.0)
        val sign = when {
            netBalance > 0.0 -> "+"
            netBalance < 0.0 -> "-"
            else -> ""
        }
        Column {
            Text(
                text = LocalDate.now()
                    .format(DateTimeFormatter.ofPattern("EEEE, d MMMM", Locale.ENGLISH))
                    .uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = MutedGreen
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Hey, ${profile?.name?.substringBefore(' ') ?: "there"}.",
                style = MaterialTheme.typography.headlineMedium,
                color = ForestGreen,
                fontWeight = FontWeight.SemiBold
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        ScreenDataStatus(profileState.isLoading || groupsState.isLoading, profileState.error ?: groupsError)
        if (profile != null && debtError != null) ScreenMessage(debtError)
        BalanceCard(
            label = "Your total balance",
            amount = sign + formatCompactAmount(kotlin.math.abs(netBalance)),
            detail = when {
                netBalance > 0.0 -> "You're owed more than you owe."
                netBalance < 0.0 -> "You owe more than you're owed."
                else -> "Your balances are settled."
            },
            action = "View balances",
            currency = currency,
            onClick = { onNavigate(AppRoute.Debt) }
        )
        Spacer(modifier = Modifier.height(14.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            AmountCard(
                "You are owed",
                totalOwing?.let { formatCompactAmount(it) } ?: "Loading",
                modifier = Modifier.weight(1f),
                currency = currency
            )
            AmountCard(
                "You owe",
                totalOwed?.let { formatCompactAmount(it) } ?: "Loading",
                modifier = Modifier.weight(1f),
                positive = true,
                currency = currency
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SectionTitle("Your groups")
            Row(
                modifier = Modifier.clickable { onNavigate(AppRoute.Groups) },
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "See all",
                    style = MaterialTheme.typography.labelLarge,
                    color = ForestGreen
                )
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_forward),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = ForestGreen
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        when {
            groupsState.isLoading -> ScreenMessage("Loading groups...")
            groupsError != null -> ScreenMessage(groupsError)
            groupsState.groups.isEmpty() -> ScreenMessage("You are not a member of any groups.")
            else -> groupsState.groups.forEach { group ->
                GroupHomeCard(
                    group = group,
                    amountOwed = debtState.debtResults.firstOrNull {
                        it.debtType == DebtType.GROUP_OWING && it.groupId == group.id
                    }?.result ?: 0.0,
                    amountToPay = debtState.debtResults.firstOrNull {
                        it.debtType == DebtType.GROUP_OWED && it.groupId == group.id
                    }?.result ?: 0.0,
                    onClick = {
                        ActiveGroup.select(group.id)
                        onNavigate(AppRoute.GroupDetails)
                    }
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
    }
}

@Composable
private fun GroupHomeCard(
    group: GroupSummary,
    amountOwed: Double,
    amountToPay: Double,
    onClick: () -> Unit
) {
    Card(modifier = Modifier.clickable(onClick = onClick)) {
        Text(group.name, style = MaterialTheme.typography.titleMedium, color = ForestGreen, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(4.dp))
        Text("${group.memberCount} people · ${group.description ?: "Shared costs"}", style = MaterialTheme.typography.bodySmall, color = MutedGreen)
        Spacer(modifier = Modifier.height(6.dp))
        val balance = when {
            amountOwed > 0.0 -> "You are owed ${formatAmount(amountOwed, group.currency)}"
            amountToPay > 0.0 -> "You owe ${formatAmount(amountToPay, group.currency)}"
            else -> "Your balance is settled"
        }
        Text(
            balance,
            style = MaterialTheme.typography.bodySmall,
            color = ForestGreen
        )
    }
}

private fun formatCompactAmount(amount: Double): String =
    String.format(Locale.UK, "%,.0f", amount)
