/**
 * Sakura Tunes Project (C) 2026
 * Licensed under GPL-3.0
 */

package com.sakuratunes.music.ui.screens.settings

import androidx.compose.ui.draw.alpha

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.navigation.NavController
import com.sakuratunes.music.BuildConfig
import com.sakuratunes.music.LocalPlayerAwareWindowInsets
import com.sakuratunes.music.R
import com.sakuratunes.music.constants.AccountChannelHandleKey
import com.sakuratunes.music.constants.AccountEmailKey
import com.sakuratunes.music.constants.AccountNameKey
import com.sakuratunes.music.ui.component.IconButton
import com.sakuratunes.music.ui.component.Material3SettingsGroup
import com.sakuratunes.music.ui.component.Material3SettingsItem
import com.sakuratunes.music.ui.component.PeekAvatar
import com.sakuratunes.music.ui.component.ReleaseNotesCard
import com.sakuratunes.music.ui.glass.GlassSettings
import com.sakuratunes.music.ui.glass.SakuraPetalShape
import com.sakuratunes.music.ui.glass.pressable
import com.sakuratunes.music.ui.theme.RoundedHeavy
import com.sakuratunes.music.ui.utils.backToMain
import com.sakuratunes.music.utils.Updater
import com.sakuratunes.music.utils.rememberPreference

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    latestVersionName: String,
) {
    val uriHandler = LocalUriHandler.current
    val context = LocalContext.current
    val c = MaterialTheme.colorScheme
    val isAndroid12OrLater = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    val hasAndroidAuto = remember {
        try {
            context.packageManager.getPackageInfo(
                "com.google.android.projection.gearhead", 0
            )
            true
        } catch (e: Exception) {
            false
        }
    }

    var activeModal by remember { mutableStateOf<SakuraFloatingModal?>(null) }
    val blurRadius by animateDpAsState(
        targetValue = if (activeModal != null && Build.VERSION.SDK_INT >= 31) 16.dp else 0.dp,
        animationSpec = tween(220),
        label = "settingsBlur",
    )

    val (accountName) = rememberPreference(AccountNameKey, "")
    val (accountEmail) = rememberPreference(AccountEmailKey, "")
    val (accountHandle) = rememberPreference(AccountChannelHandleKey, "")

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            Modifier
                .fillMaxSize()
                .then(if (blurRadius > 0.dp) Modifier.blur(blurRadius) else Modifier)
                .windowInsetsPadding(LocalPlayerAwareWindowInsets.current.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom))
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
        ) {
            Spacer(
                Modifier.windowInsetsPadding(
                    LocalPlayerAwareWindowInsets.current.only(
                        WindowInsetsSides.Top
                    )
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Sakura Tunes Hero Profile & Account Card
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(
                                c.primaryContainer.copy(alpha = 0.65f),
                                c.surfaceContainerHigh,
                            )
                        )
                    )
                    .border(1.dp, c.primary.copy(alpha = 0.25f), RoundedCornerShape(24.dp))
                    .pressable { navController.navigate("account") }
                    .padding(horizontal = 18.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (accountName.isNotBlank()) {
                    PeekAvatar(
                        name = accountName,
                        modifier = Modifier.size(48.dp),
                        contentDescription = accountName,
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(SakuraPetalShape)
                            .background(c.primary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.account),
                            contentDescription = null,
                            tint = c.primary,
                            modifier = Modifier.size(26.dp),
                        )
                    }
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = accountName.ifBlank { "Sakura Tunes Account" },
                        fontFamily = RoundedHeavy,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.5.sp,
                        color = c.onSurface,
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = accountHandle.ifBlank { accountEmail.ifBlank { "Sign in & sync YouTube Music library" } },
                        fontSize = 12.sp,
                        color = c.onSurfaceVariant,
                    )
                }
                Icon(
                    painter = painterResource(R.drawable.navigate_next),
                    contentDescription = null,
                    tint = c.primary,
                    modifier = Modifier.size(20.dp),
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Liquid Glass & Customization Section
            Material3SettingsGroup(
                title = "Liquid Glass & Customization",
                items = listOf(
                    Material3SettingsItem(
                        icon = painterResource(R.drawable.gradient),
                        isHighlighted = true,
                        title = { Text("Liquid Glass Studio") },
                        description = {
                            Text(
                                if (GlassSettings.enabled) {
                                    "Active · Blur ${GlassSettings.blurStrength.toInt()}dp · Lens ${GlassSettings.refractionStrength.toInt()}dp"
                                } else {
                                    "Disabled · Solid Material surfaces"
                                }
                            )
                        },
                        trailingContent = {
                            Switch(
                                checked = GlassSettings.enabled,
                                onCheckedChange = { GlassSettings.updateEnabled(it) },
                                colors = SwitchDefaults.colors(checkedTrackColor = c.primary),
                            )
                        },
                        onClick = { activeModal = SakuraFloatingModal.LIQUID_GLASS_STUDIO },
                    ),
                    Material3SettingsItem(
                        icon = painterResource(R.drawable.palette),
                        title = { Text(stringResource(R.string.appearance)) },
                        description = { Text("Theme mode, color palette, player & lyrics UI") },
                        onClick = { navController.navigate("settings/appearance") },
                    ),
                ),
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Player & Content Section
            Material3SettingsGroup(
                title = stringResource(R.string.settings_section_player_content),
                items = listOf(
                    Material3SettingsItem(
                        icon = painterResource(R.drawable.play),
                        title = { Text(stringResource(R.string.player_and_audio)) },
                        description = { Text("Audio quality, normalization, crossfade & sleep timer") },
                        onClick = { navController.navigate("settings/player") },
                    ),
                    Material3SettingsItem(
                        icon = painterResource(R.drawable.radio),
                        title = { Text(stringResource(R.string.stream_sources)) },
                        description = { Text("Configure client priority & playback engines") },
                        onClick = { navController.navigate("settings/stream_sources") },
                    ),
                    Material3SettingsItem(
                        icon = painterResource(R.drawable.language),
                        title = { Text(stringResource(R.string.content)) },
                        description = { Text("Lyrics providers, romanization, proxy & language") },
                        onClick = { navController.navigate("settings/content") },
                    ),
                    Material3SettingsItem(
                        icon = painterResource(R.drawable.translate),
                        title = { Text(stringResource(R.string.ai_lyrics_translation)) },
                        description = { Text("Real-time AI lyrics translation & transcription") },
                        onClick = { navController.navigate("settings/ai") },
                    ),
                    Material3SettingsItem(
                        icon = painterResource(R.drawable.integration),
                        title = { Text(stringResource(R.string.integrations)) },
                        description = { Text("Discord Rich Presence, Last.fm & Listen Together") },
                        onClick = { navController.navigate("settings/integrations") },
                    ),
                ),
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (hasAndroidAuto) {
                Material3SettingsGroup(
                    title = "Android Auto",
                    items = listOf(
                        Material3SettingsItem(
                            icon = painterResource(R.drawable.ic_android_auto),
                            title = { Text(stringResource(R.string.android_auto)) },
                            description = { Text("Customize car dashboard tabs & search limits") },
                            onClick = { navController.navigate("settings/android_auto") },
                        )
                    ),
                )

                Spacer(modifier = Modifier.height(12.dp))
            }

            // Storage & Privacy Section
            Material3SettingsGroup(
                title = stringResource(R.string.settings_section_storage),
                items = listOf(
                    Material3SettingsItem(
                        icon = painterResource(R.drawable.storage),
                        isHighlighted = true,
                        title = { Text("Quick Storage Analyzer") },
                        description = { Text("Inspect device memory & instant cache cleaner") },
                        onClick = { activeModal = SakuraFloatingModal.STORAGE_DASHBOARD },
                    ),
                    Material3SettingsItem(
                        icon = painterResource(R.drawable.cached),
                        title = { Text(stringResource(R.string.storage)) },
                        description = { Text("Configure song cache & image cache limits") },
                        onClick = { navController.navigate("settings/storage") },
                    ),
                    Material3SettingsItem(
                        icon = painterResource(R.drawable.restore),
                        title = { Text(stringResource(R.string.backup_restore)) },
                        description = { Text("Export & restore playlists, settings & database") },
                        onClick = { navController.navigate("settings/backup_restore") },
                    ),
                    Material3SettingsItem(
                        icon = painterResource(R.drawable.security),
                        title = { Text(stringResource(R.string.privacy)) },
                        description = { Text("Listen history, search history & screen security") },
                        onClick = { navController.navigate("settings/privacy") },
                    ),
                ),
            )

            Spacer(modifier = Modifier.height(12.dp))

            // System & About Section
            Material3SettingsGroup(
                title = stringResource(R.string.settings_section_system),
                items = buildList {
                    add(
                        Material3SettingsItem(
                            icon = painterResource(R.drawable.info),
                            title = { Text("Version") },
                            description = { Text("Sakura Tunes ${BuildConfig.VERSION_NAME}") },
                            trailingContent = {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFF132818))
                                        .padding(horizontal = 10.dp, vertical = 5.dp),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFF2ECC71))
                                        )
                                        Spacer(Modifier.width(5.dp))
                                        Text(
                                            text = "Up to date",
                                            color = Color(0xFF2ECC71),
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 11.sp,
                                        )
                                    }
                                }
                            },
                        )
                    )
                    if (isAndroid12OrLater) {
                        add(
                            Material3SettingsItem(
                                icon = painterResource(R.drawable.link),
                                title = { Text(stringResource(R.string.default_links)) },
                                description = { Text("Open YouTube Music & youtu.be links directly") },
                                onClick = {
                                    try {
                                        val intent = Intent(
                                            Settings.ACTION_APP_OPEN_BY_DEFAULT_SETTINGS,
                                            "package:${context.packageName}".toUri()
                                        )
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        context.startActivity(intent)
                                    } catch (e: Exception) {
                                        Toast.makeText(
                                            context,
                                            R.string.open_app_settings_error,
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                },
                            )
                        )
                    }
                    if (BuildConfig.UPDATER_AVAILABLE) {
                        add(
                            Material3SettingsItem(
                                icon = painterResource(R.drawable.update),
                                title = { Text(stringResource(R.string.updater)) },
                                description = { Text("Check for new GitHub releases") },
                                onClick = { navController.navigate("settings/updater") },
                            )
                        )
                    }
                    val showChangelog = com.sakuratunes.music.LocalChangelogState.current
                    add(
                        Material3SettingsItem(
                            icon = painterResource(R.drawable.newspaper),
                            title = { Text(stringResource(R.string.changelog)) },
                            description = { Text("What's new in Sakura Tunes ${BuildConfig.BASE_VERSION_NAME}") },
                            onClick = { showChangelog.value = true },
                        )
                    )
                    add(
                        Material3SettingsItem(
                            icon = painterResource(R.drawable.github),
                            title = { Text("Open Source Repository") },
                            description = { Text("github.com/xboyrex/SakuraTunes") },
                            onClick = { uriHandler.openUri("https://github.com/xboyrex/SakuraTunes") },
                        )
                    )
                    add(
                        Material3SettingsItem(
                            icon = painterResource(R.drawable.telegram),
                            title = { Text("Developer") },
                            description = { Text("ㅤᏞᴀᴢʏㅤᎷꫝʏᴀɴк まやんく (@xboyrex)") },
                            onClick = { uriHandler.openUri("https://t.me/MayankDroid7") },
                        )
                    )
                    add(
                        Material3SettingsItem(
                            icon = painterResource(R.drawable.about_icon),
                            title = { Text(stringResource(R.string.about)) },
                            description = { Text("Credits, architecture & license info") },
                            onClick = { navController.navigate("settings/about") },
                        )
                    )
                },
            )

            if (BuildConfig.UPDATER_AVAILABLE && Updater.isUpdateAvailable(BuildConfig.BASE_VERSION_NAME, latestVersionName)) {
                Spacer(modifier = Modifier.height(16.dp))
                ReleaseNotesCard()
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        TopAppBar(
            title = {
                Text(
                    text = stringResource(R.string.settings),
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
                        painterResource(R.drawable.arrow_back),
                        contentDescription = null,
                    )
                }
            },
        )

        SakuraFloatingDialogHost(
            activeModal = activeModal,
            onDismiss = { activeModal = null },
        )
    }
}
