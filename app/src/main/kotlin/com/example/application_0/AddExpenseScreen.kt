package com.example.application_0

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AddExpenseScreen(onBack: () -> Unit, backEnabled: Boolean) {
    MockupScreenLayout(
        title = "Add expense",
        subtitle = "Copenhagen Weekend",
        onBack = onBack,
        backEnabled = backEnabled
    ) {
        MockupField("Description", "What was the expense for?")
        Spacer(modifier = Modifier.height(14.dp))
        MockupField("Amount", "DKK 0.00")
        Spacer(modifier = Modifier.height(14.dp))
        MockupField("Paid by", "Choose a group member")
        Spacer(modifier = Modifier.height(14.dp))
        MockupField("Date", "Select a date")
        Spacer(modifier = Modifier.height(14.dp))
        MockupField("Split between", "All group members")
        Spacer(modifier = Modifier.height(22.dp))
        MockupAction("Save expense")
    }
}
