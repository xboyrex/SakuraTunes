/**
 * Sakura Tunes Project (C) 2026
 * Crafted by ㅤᏞᴀᴢʏㅤᎷꫝʏᴀɴк まやんく (@xboyrex)
 * Licensed under GPL-3.0
 */

package com.sakuratunes.music.ui.screens.settings

import androidx.compose.ui.draw.alpha

import androidx.compose.ui.graphics.Color

import androidx.compose.foundation.Image
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.graphics.shapes.RoundedPolygon
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.sakuratunes.innertube.models.WatchEndpoint
import com.sakuratunes.music.BuildConfig
import com.sakuratunes.music.LocalPlayerAwareWindowInsets
import com.sakuratunes.music.LocalPlayerConnection
import com.sakuratunes.music.R
import com.sakuratunes.music.playback.PlayerConnection
import com.sakuratunes.music.playback.queues.YouTubeQueue
import com.sakuratunes.music.ui.component.IconButton
import com.sakuratunes.music.ui.component.Material3SettingsGroup
import com.sakuratunes.music.ui.component.Material3SettingsItem
import com.sakuratunes.music.ui.glass.SakuraPetalShape
import com.sakuratunes.music.ui.theme.RoundedHeavy
import com.sakuratunes.music.ui.utils.backToMain
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

private data class Contributor(
    val name: String,
    val roleRes: Int,
    val githubHandle: String,
    val avatarUrl: String = "https://github.com/$githubHandle.png",
    val githubUrl: String = "https://github.com/$githubHandle",
    val telegramUrl: String? = null,
    val polygon: RoundedPolygon? = null,
    val favoriteSongVideoId: String? = null,
)

private data class CommunityLink(
    val label: String,
    val subtitle: String,
    val iconRes: Int,
    val url: String,
)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
private val leadDeveloper = Contributor(
    name = "ㅤᏞᴀᴢʏㅤᎷꫝʏᴀɴк まやんく",
    roleRes = R.string.credits_lead_developer,
    githubHandle = "xboyrex",
    telegramUrl = "https://t.me/MayankDroid7",
    polygon = MaterialShapes.Cookie9Sided,
    favoriteSongVideoId = "Mh2JWGWvy_Y",
)

private val communityLinks = listOf(
    CommunityLink(
        label = "Telegram — MayankDroid7",
        subtitle = "https://t.me/MayankDroid7",
        iconRes = R.drawable.telegram,
        url = "https://t.me/MayankDroid7",
    ),
    CommunityLink(
        label = "Telegram — OnyxHub7",
        subtitle = "https://t.me/OnyxHub7",
        iconRes = R.drawable.telegram,
        url = "https://t.me/OnyxHub7",
    ),
    CommunityLink(
        label = "Instagram",
        subtitle = "@mayank.zx7",
        iconRes = R.drawable.share,
        url = "https://www.instagram.com/mayank.zx7",
    ),
    CommunityLink(
        label = "YouTube — Mayank Droid",
        subtitle = "youtube.com/@mayankdroid",
        iconRes = R.drawable.play,
        url = "https://youtube.com/@mayankdroid",
    ),
    CommunityLink(
        label = "YouTube — Onyx Droid",
        subtitle = "youtube.com/@onyxdroid89",
        iconRes = R.drawable.play,
        url = "https://youtube.com/@onyxdroid89",
    ),
    CommunityLink(
        label = "GitHub Profile & Repository",
        subtitle = "github.com/xboyrex/SakuraTunes",
        iconRes = R.drawable.github,
        url = "https://github.com/xboyrex/SakuraTunes",
    ),
    CommunityLink(
        label = "Developer Email",
        subtitle = "xboyrex496@gmail.com",
        iconRes = R.drawable.info,
        url = "mailto:xboyrex496@gmail.com",
    ),
    CommunityLink(
        label = "GNU General Public License v3.0",
        subtitle = "Free, open-source music streaming client",
        iconRes = R.drawable.info,
        url = "https://github.com/xboyrex/SakuraTunes/blob/main/LICENSE",
    ),
)

private fun handleEasterEggClick(
    clickCount: Int,
    favoriteSongVideoId: String?,
    coroutineScope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    playerConnection: PlayerConnection?,
    wannaPlayStr: String,
    yeahStr: String,
    onCountUpdate: (Int) -> Unit,
) {
    if (favoriteSongVideoId != null) {
        val newCount = clickCount + 1
        onCountUpdate(newCount)
        if (newCount >= 3) {
            onCountUpdate(0)
            coroutineScope.launch {
                val result = snackbarHostState.showSnackbar(
                    message = wannaPlayStr,
                    actionLabel = yeahStr,
                    duration = SnackbarDuration.Short,
                )
                if (result == SnackbarResult.ActionPerformed) {
                    playerConnection?.playQueue(YouTubeQueue(WatchEndpoint(videoId = favoriteSongVideoId)))
                }
            }
        }
    }
}

@Composable
private fun ContributorAvatar(
    avatarUrl: String,
    sizeDp: Int,
    modifier: Modifier = Modifier,
    shape: Shape = CircleShape,
    contentDescription: String? = null,
    onClick: (() -> Unit)? = null,
) {
    val fallback = painterResource(R.drawable.about_icon)
    Surface(
        onClick = onClick ?: {},
        enabled = onClick != null,
        modifier = modifier.size(sizeDp.dp),
        shape = shape,
        color = MaterialTheme.colorScheme.surfaceContainerHighest,
        tonalElevation = 4.dp,
    ) {
        AsyncImage(
            model = avatarUrl,
            contentDescription = contentDescription,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
            placeholder = fallback,
            fallback = fallback,
            error = fallback,
        )
    }
}

@Composable
private fun DeveloperSocials(
    uriHandler: androidx.compose.ui.platform.UriHandler,
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            FilledTonalButton(
                onClick = { uriHandler.openUri("https://t.me/MayankDroid7") },
                modifier = Modifier.weight(1f).height(46.dp),
            ) {
                Icon(painterResource(R.drawable.telegram), contentDescription = "Telegram", modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Telegram", fontWeight = FontWeight.Bold)
            }
            FilledTonalButton(
                onClick = { uriHandler.openUri("https://github.com/xboyrex") },
                modifier = Modifier.weight(1f).height(46.dp),
            ) {
                Icon(painterResource(R.drawable.github), contentDescription = "GitHub", modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("xboyrex", fontWeight = FontWeight.Bold)
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            FilledTonalButton(
                onClick = { uriHandler.openUri("https://www.instagram.com/mayank.zx7") },
                modifier = Modifier.weight(1f).height(46.dp),
            ) {
                Icon(painterResource(R.drawable.share), contentDescription = "Instagram", modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Instagram", fontWeight = FontWeight.Bold)
            }
            FilledTonalButton(
                onClick = { uriHandler.openUri("https://youtube.com/@mayankdroid") },
                modifier = Modifier.weight(1f).height(46.dp),
            ) {
                Icon(painterResource(R.drawable.play), contentDescription = "YouTube", modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("YouTube", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AboutScreen(
    navController: NavController,
) {
    val uriHandler = LocalUriHandler.current
    val playerConnection = LocalPlayerConnection.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val wannaPlayStr = stringResource(R.string.wanna_play_favorite_song)
    val yeahStr = stringResource(R.string.yeah)
    val c = MaterialTheme.colorScheme

    val windowInsets = LocalPlayerAwareWindowInsets.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(windowInsets.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom))
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(
            Modifier.windowInsetsPadding(
                windowInsets.only(WindowInsetsSides.Top)
            )
        )

        Spacer(Modifier.height(16.dp))

        // Sakura Tunes Hero Brand Card
        ElevatedCard(
            shape = RoundedCornerShape(32.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            listOf(
                                c.primaryContainer.copy(alpha = 0.55f),
                                c.surfaceContainerHigh,
                            )
                        )
                    )
                    .padding(24.dp),
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(84.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .background(Color.Black)
                        .border(1.5.dp, c.primary.copy(alpha = 0.45f), RoundedCornerShape(22.dp)),
                ) {
                    Image(
                        painter = painterResource(R.drawable.about_icon),
                        contentDescription = "Sakura Tunes",
                        modifier = Modifier.size(54.dp),
                    )
                }

                Spacer(Modifier.width(20.dp))

                Column {
                    Text(
                        text = "Sakura Tunes",
                        fontFamily = RoundedHeavy,
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Black,
                        color = c.onSurface,
                        letterSpacing = (-0.5).sp,
                    )

                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Liquid Glass Music Experience",
                        style = MaterialTheme.typography.bodySmall,
                        color = c.primary,
                        fontWeight = FontWeight.SemiBold,
                    )

                    Spacer(Modifier.height(10.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = c.primary.copy(alpha = 0.14f),
                        ) {
                            Text(
                                text = "v${BuildConfig.VERSION_NAME}",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = c.primary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = c.secondaryContainer.copy(alpha = 0.6f),
                        ) {
                            Text(
                                text = BuildConfig.ARCHITECTURE.uppercase(),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = c.onSecondaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            )
                        }

                        if (BuildConfig.DEBUG) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = c.tertiaryContainer.copy(alpha = 0.6f),
                            ) {
                                Text(
                                    text = "DEBUG",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = c.onTertiaryContainer,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // Lead Developer Hero Card (ㅤᏞᴀᴢʏㅤᎷꫝʏᴀɴк まやんく / xboyrex)
        ElevatedCard(
            shape = RoundedCornerShape(32.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    var leadClickCount by remember(leadDeveloper.name) { mutableIntStateOf(0) }

                    ContributorAvatar(
                        avatarUrl = leadDeveloper.avatarUrl,
                        sizeDp = 96,
                        shape = leadDeveloper.polygon?.toShape() ?: CircleShape,
                        contentDescription = leadDeveloper.name,
                        onClick = {
                            handleEasterEggClick(
                                clickCount = leadClickCount,
                                favoriteSongVideoId = leadDeveloper.favoriteSongVideoId,
                                coroutineScope = coroutineScope,
                                snackbarHostState = snackbarHostState,
                                playerConnection = playerConnection,
                                wannaPlayStr = wannaPlayStr,
                                yeahStr = yeahStr,
                                onCountUpdate = { leadClickCount = it },
                            )
                        },
                    )

                    Column(
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            text = leadDeveloper.name,
                            fontFamily = RoundedHeavy,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = c.onSurface,
                        )
                        Text(
                            text = "xboyrex · he/him",
                            style = MaterialTheme.typography.titleSmall,
                            color = c.primary,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Android modding - Web/ App developer",
                            style = MaterialTheme.typography.bodySmall,
                            color = c.onSurfaceVariant,
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))

                DeveloperSocials(uriHandler)

                Spacer(Modifier.height(12.dp))

                Button(
                    onClick = { uriHandler.openUri("https://github.com/xboyrex/SakuraTunes") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = c.primary,
                        contentColor = c.onPrimary,
                    ),
                ) {
                    Icon(painterResource(R.drawable.favorite), contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(10.dp))
                    Text("Star SakuraTunes on GitHub", fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // Community & Repository Links
        Material3SettingsGroup(
            title = stringResource(R.string.community_and_info),
            items = communityLinks.map { link ->
                Material3SettingsItem(
                    icon = painterResource(link.iconRes),
                    title = { Text(link.label, fontWeight = FontWeight.SemiBold) },
                    description = { Text(link.subtitle) },
                    onClick = { uriHandler.openUri(link.url) },
                )
            },
        )

        Spacer(Modifier.height(36.dp))

        Text(
            text = "Crafted with Sakura Liquid Glass by ㅤᏞᴀᴢʏㅤᎷꫝʏᴀɴк まやんく (@xboyrex)",
            fontFamily = RoundedHeavy,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = c.primary.copy(alpha = 0.85f),
        )

        Spacer(Modifier.height(48.dp))
    }

    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.about),
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
                    contentDescription = stringResource(R.string.cd_back),
                )
            }
        },
    )

    Box(Modifier.fillMaxSize()) {
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .windowInsetsPadding(windowInsets.only(WindowInsetsSides.Bottom + WindowInsetsSides.Horizontal)),
        )
    }
}
