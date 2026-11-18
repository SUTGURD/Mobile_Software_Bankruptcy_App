package com.example.application_0

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

private val dateFormatter = DateTimeFormatter.ofPattern("d MMM", Locale.UK)

internal fun formatAmount(amount: Double, currency: String): String =
    "$currency ${String.format(Locale.UK, "%.2f", amount)}"

internal fun formatDate(date: LocalDateTime): String = date.format(dateFormatter)

@Composable
internal fun ScreenMessage(text: String) {
    Text(text = text, style = MaterialTheme.typography.bodyMedium)
}

@Composable
internal fun ScreenDataStatus(isLoading: Boolean, error: String?) {
    when {
        isLoading -> ScreenMessage("Loading...")
        error != null -> ScreenMessage(error)
    }
}
