package com.example.application_0

import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.Locale

@Composable
fun ProfileScreen(onNavigate: (AppRoute) -> Unit, onBack: (() -> Unit)?, backEnabled: Boolean) {
    val viewModel: UserProfileViewModel = viewModel()
    val state by viewModel.uiState.collectAsState()
    val profile = state.profile
    ScreenLayout(
        title = "Profile",
        showBottomBar = true,
        bottomBarDestination = AppRoute.Profile,
        onNavigate = onNavigate,
        onBack = onBack,
        backEnabled = backEnabled,
        bottomContent = {
            if (profile != null) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .clickable {
                            Authentication.logout()
                            ActiveGroup.clear()
                            onNavigate(AppRoute.Entry)
                        }
                        .semantics { contentDescription = "Log out" },
                    shape = RoundedCornerShape(24.dp),
                    color = Color.White
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "Log out",
                            style = MaterialTheme.typography.titleSmall,
                            color = ForestGreen,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    ) {
        ScreenDataStatus(state.isLoading, state.error)
        if (profile != null) {
            Spacer(modifier = Modifier.height(24.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ProfilePortrait(profile)
                Spacer(modifier = Modifier.height(26.dp))
                Text(
                    text = profile.name,
                    style = MaterialTheme.typography.headlineSmall,
                    color = ForestGreen,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(profile.email, style = MaterialTheme.typography.bodyMedium, color = MutedGreen)
                Spacer(modifier = Modifier.height(18.dp))
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = PaleGreen
                ) {
                    Text(
                        text = "SHARING SINCE ${profile.memberSince.uppercase(Locale.UK)}",
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = ForestGreen,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            Spacer(modifier = Modifier.height(38.dp))
            Card(contentPadding = 16.dp) {
                Text(
                    text = "Account details",
                    style = MaterialTheme.typography.titleMedium,
                    color = ForestGreen,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(14.dp))
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    AccountDetailRow("Default currency", "DKK")
                    AccountDetailRow("Email", "Verified")
                    AccountDetailRow("Member since", profile.memberSince)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Card(contentPadding = 16.dp) {
                InfoRow(
                    title = "Profile settings",
                    detail = "Name, contact information and profile picture",
                    trailing = "Edit",
                    onClick = { onNavigate(AppRoute.ProfileSettings) }
                )
                Spacer(modifier = Modifier.height(18.dp))
                InfoRow(
                    title = "Notification settings",
                    detail = "Choose which notifications you receive",
                    trailing = "Edit",
                    onClick = { onNavigate(AppRoute.NotificationsSettings) }
                )
            }
        }
    }
}

@Composable
internal fun ProfilePortrait(profile: UserProfile) {
    val context = LocalContext.current
    val portrait by produceState<ImageBitmap?>(
        initialValue = null,
        profile.profileImageUri
    ) {
        val imageUri = profile.profileImageUri
        value = if (imageUri == null) {
            null
        } else {
            withContext(Dispatchers.IO) {
                try {
                    val stream = context.contentResolver.openInputStream(Uri.parse(imageUri))
                    if (stream == null) {
                        Log.w("ProfileScreen", "Unable to open profile image.")
                        null
                    } else {
                        stream.use { input ->
                            BitmapFactory.decodeStream(input)?.asImageBitmap()
                                ?: run {
                                    Log.w("ProfileScreen", "Unable to decode profile image.")
                                    null
                                }
                        }
                    }
                } catch (exception: IOException) {
                    Log.w("ProfileScreen", "Unable to load profile image.", exception)
                    null
                } catch (exception: SecurityException) {
                    Log.w("ProfileScreen", "Profile image access was denied.", exception)
                    null
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .size(width = 120.dp, height = 68.dp)
            .clip(RoundedCornerShape(34.dp))
            .background(SoftGreen)
            .semantics { contentDescription = "Profile photo for ${profile.name}" },
        contentAlignment = Alignment.Center
    ) {
        val loadedPortrait = portrait
        if (loadedPortrait != null) {
            Image(
                bitmap = loadedPortrait,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Text(
                text = profile.name.split(' ').take(2).mapNotNull { it.firstOrNull() }
                    .joinToString("").uppercase(Locale.UK),
                color = ForestGreen,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun AccountDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = MutedGreen)
        Text(value, style = MaterialTheme.typography.bodySmall, color = ForestGreen)
    }
}
