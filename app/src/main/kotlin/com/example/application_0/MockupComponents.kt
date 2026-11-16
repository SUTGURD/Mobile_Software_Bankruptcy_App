package com.example.application_0

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

internal val BrandBlue = Color(0xFF2859D9)
private val Ink = Color(0xFF17233C)
private val MutedInk = Color(0xFF69758B)
private val PageBackground = Color(0xFFF5F7FC)
private val CardBorder = Color(0xFFE2E8F2)
private val PaleBlue = Color(0xFFEAF0FF)

@Composable
internal fun MockupScreenLayout(
    title: String,
    subtitle: String? = null,
    showBottomBar: Boolean = false,
    bottomBarDestination: AppRoute = AppRoute.Home,
    onNavigate: ((AppRoute) -> Unit)? = null,
    onBack: (() -> Unit)? = null,
    backEnabled: Boolean = true,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PageBackground)
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (onBack != null) {
                Text(
                    text = "Back",
                    modifier = Modifier
                        .clickable(
                            enabled = backEnabled,
                            role = Role.Button,
                            onClick = onBack
                        )
                        .padding(end = 14.dp),
                    style = MaterialTheme.typography.labelLarge,
                    color = BrandBlue
                )
            }
            BrandMark()
            Spacer(modifier = Modifier.size(10.dp))
            Text(
                text = "Pennywise",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Ink
            )
            Spacer(modifier = Modifier.weight(1f))
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 22.dp)
                .padding(bottom = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Ink
            )
            if (subtitle != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MutedInk
                )
            }
            Spacer(modifier = Modifier.height(22.dp))
            content()
        }

        if (showBottomBar) {
            StaticBottomBar(bottomBarDestination, onNavigate)
        }
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
        )
    }
}

@Composable
internal fun BrandMark(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(34.dp)
            .background(BrandBlue, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "P",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
internal fun MockupCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            content()
        }
    }
}

@Composable
internal fun MockupField(label: String, placeholder: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = Ink,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(7.dp))
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
        ) {
            Box(
                modifier = Modifier.padding(horizontal = 14.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MutedInk
                )
            }
        }
    }
}

@Composable
internal fun MockupAction(
    label: String,
    secondary: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .clickable(
                enabled = onClick != null,
                role = Role.Button,
                onClick = { onClick?.invoke() }
            ),
        shape = RoundedCornerShape(14.dp),
        color = if (secondary) Color.White else BrandBlue,
        border = if (secondary) androidx.compose.foundation.BorderStroke(1.dp, CardBorder) else null
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = if (secondary) Ink else Color.White,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
internal fun MockupSectionTitle(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = Ink
    )
}

@Composable
internal fun MockupInfoRow(
    title: String,
    detail: String,
    trailing: String? = null,
    accent: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                enabled = onClick != null,
                role = Role.Button,
                onClick = { onClick?.invoke() }
            ),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .background(if (accent) PaleBlue else PageBackground, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = title.take(1).uppercase(),
                color = BrandBlue,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = Ink
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = detail,
                style = MaterialTheme.typography.bodySmall,
                color = MutedInk
            )
        }
        if (trailing != null) {
            Text(
                text = trailing,
                style = MaterialTheme.typography.labelLarge,
                color = if (accent) BrandBlue else Ink,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
internal fun MockupAmountCard(
    label: String,
    amount: String,
    positive: Boolean = false,
    modifier: Modifier = Modifier
) {
    MockupCard(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MutedInk
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = amount,
            style = MaterialTheme.typography.titleLarge,
            color = if (positive) BrandBlue else Ink,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun StaticBottomBar(
    selectedDestination: AppRoute,
    onNavigate: ((AppRoute) -> Unit)?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .border(width = 1.dp, color = CardBorder)
            .padding(horizontal = 18.dp, vertical = 13.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        listOf(
            AppRoute.Home,
            AppRoute.Groups,
            AppRoute.Debt,
            AppRoute.Profile
        ).forEach { destination ->
            Text(
                text = destination.label,
                modifier = Modifier.clickable(
                    enabled = onNavigate != null,
                    role = Role.Button,
                    onClick = { onNavigate?.invoke(destination) }
                ),
                style = MaterialTheme.typography.labelSmall,
                color = if (destination == selectedDestination) BrandBlue else MutedInk,
                fontWeight = if (destination == selectedDestination) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

@Composable
internal fun MockupCaption(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier.fillMaxWidth(),
        style = MaterialTheme.typography.bodySmall,
        color = MutedInk,
        textAlign = TextAlign.Center
    )
}
