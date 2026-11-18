package com.example.application_0

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ProfileSettingsScreen(
    onNavigate: (AppRoute) -> Unit,
    onBack: () -> Unit,
    backEnabled: Boolean,
    profile: UserProfile?
) {
    var name by rememberSaveable(profile?.id) { mutableStateOf(profile?.name.orEmpty()) }
    var email by rememberSaveable(profile?.id) { mutableStateOf(profile?.email.orEmpty()) }
    var phoneNumber by rememberSaveable(profile?.id) {
        mutableStateOf(profile?.phoneNumber.orEmpty())
    }
    var selectedImageUri by remember(profile?.id) {
        mutableStateOf(profile?.profileImageUri)
    }
    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) selectedImageUri = uri.toString()
    }

    ScreenLayout(
        title = "Profile settings",
        showBottomBar = true,
        bottomBarDestination = AppRoute.Profile,
        onNavigate = onNavigate,
        onBack = onBack,
        backEnabled = backEnabled,
        bottomContent = {
            ActionButton("Done", onClick = onBack)
        }
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        if (profile == null) {
            ScreenMessage("Profile details are unavailable.")
        } else {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ProfilePortrait(profile.copy(name = name, profileImageUri = selectedImageUri))
                Spacer(modifier = Modifier.height(14.dp))
                ActionButton(
                    label = "Change profile picture",
                    secondary = true,
                    onClick = {
                        photoPicker.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
                )
            }
            Spacer(modifier = Modifier.height(28.dp))
            SectionTitle("Contact information")
            Spacer(modifier = Modifier.height(14.dp))
            FormField(
                label = "Name",
                value = name,
                onValueChange = { name = it },
                placeholder = "Your name",
                modifier = Modifier.semantics { contentDescription = "Profile name" }
            )
            Spacer(modifier = Modifier.height(14.dp))
            FormField(
                label = "Email address",
                value = email,
                onValueChange = { email = it },
                placeholder = "name@example.com",
                modifier = Modifier.semantics { contentDescription = "Profile email address" }
            )
            Spacer(modifier = Modifier.height(14.dp))
            FormField(
                label = "Phone number",
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                placeholder = "Add a phone number",
                modifier = Modifier.semantics { contentDescription = "Profile phone number" }
            )
            Spacer(modifier = Modifier.height(18.dp))
            Caption("Changes are not stored while the app uses dummy data.")
        }
    }
}

@Composable
fun NotificationsSettingsScreen(
    onNavigate: (AppRoute) -> Unit,
    onBack: () -> Unit,
    backEnabled: Boolean,
    profile: UserProfile?,
    selectedMenu: AppRoute
) {
    var notificationsEnabled by rememberSaveable(profile?.id) {
        mutableStateOf(profile?.notificationsEnabled ?: true)
    }
    var expensesEnabled by rememberSaveable(profile?.id) {
        mutableStateOf(profile?.notificationSettings?.expenses ?: true)
    }
    var paymentsEnabled by rememberSaveable(profile?.id) {
        mutableStateOf(profile?.notificationSettings?.payments ?: true)
    }
    var remindersEnabled by rememberSaveable(profile?.id) {
        mutableStateOf(profile?.notificationSettings?.reminders ?: true)
    }
    var groupActivityEnabled by rememberSaveable(profile?.id) {
        mutableStateOf(profile?.notificationSettings?.groupActivity ?: true)
    }

    ScreenLayout(
        title = "Notification settings",
        showBottomBar = true,
        bottomBarDestination = selectedMenu,
        onNavigate = onNavigate,
        onBack = onBack,
        backEnabled = backEnabled
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "Choose which updates you receive.",
            style = MaterialTheme.typography.bodyMedium,
            color = MutedGreen
        )
        Spacer(modifier = Modifier.height(22.dp))
        Card(contentPadding = 16.dp) {
            SettingSwitchRow(
                title = "Notifications",
                detail = "Enable or disable all notifications",
                checked = notificationsEnabled,
                onCheckedChange = { notificationsEnabled = it },
                testTag = "notifications_enabled"
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        SectionTitle("Notification types")
        Spacer(modifier = Modifier.height(12.dp))
        Card(contentPadding = 16.dp) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                SettingSwitchRow(
                    title = "Expenses",
                    detail = "New expenses and changes",
                    checked = expensesEnabled,
                    enabled = notificationsEnabled,
                    onCheckedChange = { expensesEnabled = it },
                    testTag = "notification_expenses"
                )
                SettingSwitchRow(
                    title = "Payments",
                    detail = "Payments made to you or by you",
                    checked = paymentsEnabled,
                    enabled = notificationsEnabled,
                    onCheckedChange = { paymentsEnabled = it },
                    testTag = "notification_payments"
                )
                SettingSwitchRow(
                    title = "Reminders",
                    detail = "Payment reminders",
                    checked = remindersEnabled,
                    enabled = notificationsEnabled,
                    onCheckedChange = { remindersEnabled = it },
                    testTag = "notification_reminders"
                )
                SettingSwitchRow(
                    title = "Group activity",
                    detail = "Invitations and membership changes",
                    checked = groupActivityEnabled,
                    enabled = notificationsEnabled,
                    onCheckedChange = { groupActivityEnabled = it },
                    testTag = "notification_group_activity"
                )
            }
        }
        Spacer(modifier = Modifier.height(18.dp))
        Caption("Notification preferences are not stored while the app uses dummy data.")
    }
}

@Composable
private fun SettingSwitchRow(
    title: String,
    detail: String,
    checked: Boolean,
    enabled: Boolean = true,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                enabled = enabled,
                role = Role.Switch,
                onClick = { onCheckedChange(!checked) }
            )
            .semantics(mergeDescendants = true) {
                contentDescription = "$title notifications"
            }
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = ForestGreen,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = detail,
                style = MaterialTheme.typography.bodySmall,
                color = MutedGreen
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled,
            modifier = Modifier
                .size(width = 52.dp, height = 36.dp)
                .testTag(testTag)
                .semantics { contentDescription = testTag },
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = ForestGreen,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = MutedGreen
            )
        )
    }
}
