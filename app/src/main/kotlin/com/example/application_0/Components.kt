package com.example.application_0

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.Icon
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp

internal val ForestGreen = Color(0xFF183F35)
internal val PageBackground = Color(0xFFFAFAF4)
internal val MutedGreen = Color(0xFF687C75)
internal val PaleGreen = Color(0xFFD8EFA0)
internal val SoftGreen = Color(0xFFEEF1E8)
internal val WarmRed = Color(0xFFAA5038)
internal val DarkRed = Color(0xFF7F241E)
internal val BrightOrange = Color(0xFFFFB45C)

@Composable
internal fun ScreenLayout(
    title: String? = null,
    subtitle: String? = null,
    showBottomBar: Boolean = true,
    showNotifications: Boolean = showBottomBar,
    bottomBarDestination: AppRoute = AppRoute.Home,
    onNavigate: ((AppRoute) -> Unit)? = null,
    onBack: (() -> Unit)? = null,
    backEnabled: Boolean = true,
    bottomContent: (@Composable () -> Unit)? = null,
    scrollContent: Boolean = true,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PageBackground)
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        if (showBottomBar || title != null || onBack != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (onBack != null) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .clickable(
                                    enabled = backEnabled,
                                    role = Role.Button,
                                    onClick = onBack
                                )
                                .semantics { contentDescription = "Back" },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_arrow_back),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = ForestGreen
                            )
                        }
                    }
                    if (title != null) {
                        Text(
                            text = title,
                            color = ForestGreen,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                if (showNotifications) {
                    Surface(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .clickable(
                                enabled = onNavigate != null,
                                role = Role.Button,
                                onClick = { onNavigate?.invoke(AppRoute.Notifications) }
                            )
                            .semantics { contentDescription = "Notifications" },
                        shape = CircleShape,
                        color = Color.White
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                painter = painterResource(R.drawable.ic_notifications),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = ForestGreen
                            )
                        }
                    }
                }
            }
        }

        val bodyModifier = if (scrollContent) {
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        } else {
            Modifier
                .weight(1f)
                .fillMaxWidth()
        }
        Column(
            modifier = bodyModifier
                .padding(horizontal = 20.dp)
                .padding(top = 12.dp, bottom = 24.dp)
        ) {
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    color = MutedGreen,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(18.dp))
            }
            content()
        }

        if (bottomContent != null) {
            val bottomContentInsets = if (showBottomBar) {
                Modifier
            } else {
                Modifier.windowInsetsPadding(WindowInsets.navigationBars)
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PageBackground)
                    .then(bottomContentInsets)
                    .padding(horizontal = 20.dp)
                    .padding(top = 8.dp, bottom = 12.dp)
            ) {
                bottomContent()
            }
        }

        if (showBottomBar) {
            BottomNavigationBar(bottomBarDestination, onNavigate)
        }
    }
}

@Composable
internal fun BrandMark(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(42.dp)
            .background(ForestGreen, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "B",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleLarge
        )
    }
}

@Composable
internal fun Card(
    modifier: Modifier = Modifier,
    color: Color = Color.White,
    contentPadding: androidx.compose.ui.unit.Dp = 20.dp,
    content: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(18.dp)
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = shape,
        color = color
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(contentPadding)
        ) {
            content()
        }
    }
}

@Composable
internal fun ActionButton(
    label: String,
    modifier: Modifier = Modifier,
    secondary: Boolean = false,
    enabled: Boolean = true,
    leadingIcon: Int? = null,
    danger: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp)
            .clip(RoundedCornerShape(28.dp))
            .clickable(
                enabled = enabled && onClick != null,
                role = Role.Button,
                onClick = { onClick?.invoke() }
            ),
        shape = RoundedCornerShape(28.dp),
        color = when {
            danger -> WarmRed
            secondary -> Color.White
            else -> ForestGreen
        }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (leadingIcon != null) {
                Icon(
                    painter = painterResource(leadingIcon),
                    contentDescription = null,
                    tint = if (secondary && !danger) ForestGreen else Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.size(6.dp))
            }
            Text(
                text = label,
                style = MaterialTheme.typography.titleSmall,
                color = if (secondary && !danger) ForestGreen else Color.White,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
internal fun SectionTitle(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.SemiBold,
        color = ForestGreen
    )
}

@Composable
internal fun InfoRow(
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
                role = if (onClick == null) null else Role.Button,
                onClick = { onClick?.invoke() }
            ),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = ForestGreen
            )
            if (detail.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = detail,
                    style = MaterialTheme.typography.bodySmall,
                    color = MutedGreen
                )
            }
        }
        if (trailing != null) {
            Text(
                text = trailing,
                style = MaterialTheme.typography.labelLarge,
                color = ForestGreen,
                fontWeight = if (accent) FontWeight.Bold else FontWeight.SemiBold
            )
        }
    }
}

@Composable
internal fun AmountCard(
    label: String,
    amount: String,
    modifier: Modifier = Modifier,
    positive: Boolean = false,
    currency: String? = null
) {
    Card(modifier = modifier) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = MutedGreen)
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = buildAnnotatedString {
                append(amount)
                if (currency != null) {
                    append(" ")
                    withStyle(
                        SpanStyle(
                            fontSize = MaterialTheme.typography.labelSmall.fontSize,
                            color = MutedGreen
                        )
                    ) {
                        append(currency)
                    }
                }
            },
            style = MaterialTheme.typography.titleLarge,
            color = if (positive) WarmRed else ForestGreen,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
internal fun BalanceCard(
    label: String,
    amount: String,
    detail: String,
    action: String,
    currency: String = "DKK",
    modifier: Modifier = Modifier,
    owing: Boolean = false,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        color = if (owing) DarkRed else ForestGreen
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = if (owing) BrightOrange else PaleGreen,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(14.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = amount,
                    style = MaterialTheme.typography.displaySmall,
                    color = Color.White,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = currency,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )
            }
            if (detail.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = detail, style = MaterialTheme.typography.bodySmall, color = Color.White)
            }
            if (action.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(role = Role.Button, onClick = onClick)
                        .padding(vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = action, color = PaleGreen, style = MaterialTheme.typography.labelLarge)
                    Spacer(modifier = Modifier.size(4.dp))
                    Icon(
                        painter = painterResource(R.drawable.ic_arrow_forward),
                        contentDescription = null,
                        tint = PaleGreen,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
internal fun Avatar(name: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(44.dp)
            .background(SoftGreen, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = name.split(' ').take(2).mapNotNull { it.firstOrNull() }
                .joinToString("").uppercase(),
            color = ForestGreen,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
internal fun Caption(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier.fillMaxWidth(),
        style = MaterialTheme.typography.bodySmall,
        color = MutedGreen,
        textAlign = TextAlign.Center
    )
}

@Composable
internal fun FormField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = MutedGreen)
        Spacer(modifier = Modifier.size(6.dp))
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth().then(modifier),
            placeholder = if (placeholder == null) null else {
                { Text(placeholder, color = MutedGreen) }
            },
            singleLine = true,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            shape = RoundedCornerShape(14.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            )
        )
    }
}

@Composable
private fun BottomNavigationBar(
    selectedDestination: AppRoute,
    onNavigate: ((AppRoute) -> Unit)?
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(horizontal = 16.dp, vertical = 7.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            listOf(
                AppRoute.Home,
                AppRoute.Groups,
                AppRoute.Debt,
                AppRoute.Profile
            ).forEach { destination ->
                val isSelected = selectedDestination == destination
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(20.dp))
                        .testTag("bottom_nav_${destination.route}")
                        .clickable(
                            enabled = onNavigate != null,
                            role = Role.Button,
                            onClick = { onNavigate?.invoke(destination) }
                        )
                        .semantics { selected = isSelected }
                        .padding(vertical = 2.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(18.dp))
                            .background(if (isSelected) PaleGreen else Color.Transparent)
                            .padding(horizontal = 12.dp, vertical = 3.dp)
                    ) {
                        NavigationIcon(destination, ForestGreen)
                    }
                    Text(
                        text = destination.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isSelected) ForestGreen else MutedGreen,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

@Composable
private fun NavigationIconResource(destination: AppRoute): Int = when (destination) {
    AppRoute.Home -> R.drawable.ic_home
    AppRoute.Groups -> R.drawable.ic_group
    AppRoute.Debt -> R.drawable.ic_credit_card
    AppRoute.Profile -> R.drawable.ic_person
    else -> R.drawable.ic_home
}

@Composable
private fun NavigationIcon(destination: AppRoute, colour: Color) {
    Icon(
        painter = painterResource(NavigationIconResource(destination)),
        contentDescription = null,
        tint = colour,
        modifier = Modifier.size(24.dp)
    )
}
