/**
 * Sakura Tunes Project (C) 2026
 * Crafted by ㅤᏞᴀᴢʏㅤᎷꫝʏᴀɴк まやんく (@xboyrex)
 * Licensed under GPL-3.0
 */

package com.sakuratunes.music.ui.screens.settings

import androidx.compose.ui.draw.alpha

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sakuratunes.music.BuildConfig
import com.sakuratunes.music.LocalPlayerAwareWindowInsets
import com.sakuratunes.music.R
import com.sakuratunes.music.constants.CheckForUpdatesKey
import com.sakuratunes.music.constants.UpdateNotificationsEnabledKey
import com.sakuratunes.music.ui.component.IconButton
import com.sakuratunes.music.ui.component.Material3SettingsGroup
import com.sakuratunes.music.ui.component.Material3SettingsItem
import com.sakuratunes.music.ui.glass.SakuraPetalShape
import com.sakuratunes.music.ui.theme.RoundedHeavy
import com.sakuratunes.music.ui.utils.backToMain
import com.sakuratunes.music.utils.Updater
import com.sakuratunes.music.utils.rememberPreference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdaterScreen(
    navController: NavController,
) {
    val (checkForUpdates, onCheckForUpdatesChange) = rememberPreference(CheckForUpdatesKey, true)
    val (updateNotifications, onUpdateNotificationsChange) = rememberPreference(UpdateNotificationsEnabledKey, true)

    val uriHandler = LocalUriHandler.current
    val c = MaterialTheme.colorScheme
    var isChecking by remember { mutableStateOf(false) }
    var updateAvailable by remember { mutableStateOf(false) }
    var latestVersion by remember { mutableStateOf<String?>(BuildConfig.BASE_VERSION_NAME) }
    var downloadUrl by remember { mutableStateOf<String?>(Updater.GITHUB_RELEASES_URL) }
    var showChangelog by remember { mutableStateOf(true) }
    var changelogContent by remember {
        mutableStateOf<String?>(
            "• Real-Time Liquid Glass Shader Engine (Blur, Lens & Chromatic Dispersion)\n" +
                "• Floating Liquid Capsule Navigation Bar with Damped Drag Physics\n" +
                "• Liquid Glass Studio & Quick Storage Analyzer in Settings\n" +
                "• Redesigned Liquid Glass Now Playing Player & Controls\n" +
                "• Crafted by ㅤᏞᴀᴢʏㅤᎷꫝʏᴀɴк まやんく (@xboyrex)"
        )
    }

    val coroutineScope = rememberCoroutineScope()

    fun performManualCheck() {
        coroutineScope.launch {
            isChecking = true
            withContext(Dispatchers.IO) {
                Updater
                    .checkForUpdate(forceRefresh = true)
                    .onSuccess { (releaseInfo, hasUpdate) ->
                        if (releaseInfo != null) {
                            latestVersion = releaseInfo.versionName
                            updateAvailable = hasUpdate
                            downloadUrl = Updater.getDownloadUrlForCurrentVariant(releaseInfo)
                            if (releaseInfo.description.isNotBlank()) {
                                changelogContent = releaseInfo.description
                            }
                        }
                    }
            }
            isChecking = false
        }
    }

    LaunchedEffect(Unit) {
        performManualCheck()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(
                LocalPlayerAwareWindowInsets.current.only(
                    WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom,
                ),
            )
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(
            Modifier.windowInsetsPadding(
                LocalPlayerAwareWindowInsets.current.only(
                    WindowInsetsSides.Top,
                ),
            ),
        )

        Spacer(Modifier.height(8.dp))

        // Liquid Glass Hero Status Card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(
                    Brush.linearGradient(
                        listOf(
                            c.primaryContainer.copy(alpha = 0.55f),
                            c.surfaceContainerHigh,
                        )
                    )
                )
                .border(1.dp, c.primary.copy(alpha = 0.30f), RoundedCornerShape(28.dp))
                .padding(22.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(SakuraPetalShape)
                        .background(c.primary.copy(alpha = 0.20f))
                        .border(1.dp, c.primary.copy(alpha = 0.4f), SakuraPetalShape),
                    contentAlignment = Alignment.Center,
                ) {
                    if (isChecking) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(26.dp),
                            strokeWidth = 2.5.dp,
                            color = c.primary,
                        )
                    } else {
                        Icon(
                            painter = painterResource(if (updateAvailable) R.drawable.download else R.drawable.check),
                            contentDescription = null,
                            tint = c.primary,
                            modifier = Modifier.size(26.dp),
                        )
                    }
                }
                Spacer(Modifier.width(16.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        text = when {
                            isChecking -> stringResource(R.string.checking_for_updates)
                            updateAvailable -> "Update Available · v${latestVersion ?: BuildConfig.BASE_VERSION_NAME}"
                            else -> "Sakura Tunes is Up to Date"
                        },
                        fontFamily = RoundedHeavy,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp,
                        color = c.onSurface,
                    )
                    Spacer(Modifier.height(4.dp))
                    val variant = if (BuildConfig.CAST_AVAILABLE) "GMS (Google Cast)" else "FOSS"
                    Text(
                        text = "Installed: v${BuildConfig.VERSION_NAME} · ${BuildConfig.ARCHITECTURE.uppercase()} · $variant",
                        fontSize = 12.sp,
                        color = c.onSurfaceVariant,
                    )
                }
            }

            Spacer(Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Button(
                    onClick = { if (!isChecking) performManualCheck() },
                    modifier = Modifier.weight(1f).height(46.dp),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = c.primary,
                        contentColor = c.onPrimary,
                    ),
                ) {
                    Icon(painterResource(R.drawable.refresh), contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Check Now", fontWeight = FontWeight.Bold)
                }

                FilledTonalButton(
                    onClick = {
                        uriHandler.openUri(downloadUrl ?: Updater.GITHUB_RELEASES_URL)
                    },
                    modifier = Modifier.weight(1f).height(46.dp),
                    shape = CircleShape,
                ) {
                    Icon(painterResource(R.drawable.download), contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(if (updateAvailable) "Download APK" else "GitHub Releases", fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Material3SettingsGroup(
            title = stringResource(R.string.update_settings),
            items = buildList {
                add(
                    Material3SettingsItem(
                        title = { Text(stringResource(R.string.check_for_updates)) },
                        description = { Text("Automatically check GitHub Releases on startup") },
                        icon = painterResource(R.drawable.update),
                        trailingContent = {
                            Switch(
                                checked = checkForUpdates,
                                onCheckedChange = onCheckForUpdatesChange,
                                colors = SwitchDefaults.colors(checkedTrackColor = c.primary),
                            )
                        },
                        onClick = { onCheckForUpdatesChange(!checkForUpdates) },
                    ),
                )

                if (checkForUpdates) {
                    add(
                        Material3SettingsItem(
                            title = { Text(stringResource(R.string.update_notifications)) },
                            description = { Text("Show badge when a new Sakura Tunes version is published") },
                            icon = painterResource(R.drawable.notification),
                            trailingContent = {
                                Switch(
                                    checked = updateNotifications,
                                    onCheckedChange = onUpdateNotificationsChange,
                                    colors = SwitchDefaults.colors(checkedTrackColor = c.primary),
                                )
                            },
                            onClick = { onUpdateNotificationsChange(!updateNotifications) },
                        ),
                    )
                }
            },
        )

        Spacer(Modifier.height(16.dp))

        // Release Notes / Changelog Card
        Material3SettingsGroup(
            title = "Release Notes (v${latestVersion ?: BuildConfig.BASE_VERSION_NAME})",
            items = listOf(
                Material3SettingsItem(
                    icon = painterResource(R.drawable.newspaper),
                    title = {
                        Text(
                            if (showChangelog) stringResource(R.string.hide_changelog)
                            else stringResource(R.string.view_changelog)
                        )
                    },
                    description = {
                        if (showChangelog && !changelogContent.isNullOrBlank()) {
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = changelogContent!!,
                                style = MaterialTheme.typography.bodySmall,
                                color = c.onSurfaceVariant,
                                lineHeight = 18.sp,
                            )
                        } else {
                            Text("Tap to view what's new in this release")
                        }
                    },
                    onClick = { showChangelog = !showChangelog },
                ),
            ),
        )

        Spacer(Modifier.height(32.dp))
    }

    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.updater),
                fontFamily = RoundedHeavy,
                fontWeight = FontWeight.Bold,
            )
        },
        navigationIcon = {
            IconButton(
                onClick = navController::navigateUp,
                onLongClick = navController::backToMain,
            ) {
                Icon(
                    painter = painterResource(R.drawable.arrow_back),
                    contentDescription = null,
                )
            }
        },
    )
}
