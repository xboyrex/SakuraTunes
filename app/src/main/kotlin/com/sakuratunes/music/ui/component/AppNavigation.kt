/**
 * Sakura Tunes Project (C) 2026
 * Crafted by ㅤᏞᴀᴢʏㅤᎷꫝʏᴀɴк まやんく (@xboyrex)
 * Licensed under GPL-3.0
 */

package com.sakuratunes.music.ui.component

import androidx.compose.ui.draw.alpha

import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kyant.backdrop.Backdrop
import com.sakuratunes.music.ui.glass.GlassSettings
import com.sakuratunes.music.ui.glass.LiquidBottomTabs
import com.sakuratunes.music.ui.glass.LiquidNavTabItem
import com.sakuratunes.music.ui.glass.LocalPageGlassBackdrop
import com.sakuratunes.music.ui.screens.Screens
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

@Stable
fun isRouteSelected(currentRoute: String?, screenRoute: String, navigationItems: List<Screens>): Boolean {
    if (currentRoute == null) return false
    if (currentRoute == screenRoute) return true
    if (navigationItems.any { it.route == screenRoute } &&
        currentRoute.startsWith("$screenRoute/")
    ) return true

    if (screenRoute == "search_input" &&
        (currentRoute.startsWith("search/") || currentRoute == "search/{query}")
    ) return true

    return false
}

@Composable
fun AppNavigationRail(
    navigationItems: List<Screens>,
    currentRoute: String?,
    onItemClick: (Screens, Boolean) -> Unit,
    modifier: Modifier = Modifier,
    pureBlack: Boolean = false,
    onSearchLongClick: (() -> Unit)? = null,
) {
    val isDark = isSystemInDarkTheme() || pureBlack
    val containerColor = when {
        !GlassSettings.enabled -> if (pureBlack) Color.Black else MaterialTheme.colorScheme.surfaceContainer
        pureBlack -> Color(0xFF060408).copy(alpha = 0.65f)
        isDark -> Color(0xFF141016).copy(alpha = 0.65f)
        else -> Color(0xFFFFF8FA).copy(alpha = 0.72f)
    }
    val haptics = LocalHapticFeedback.current
    val viewConfiguration = LocalViewConfiguration.current

    NavigationRail(
        modifier = modifier
            .padding(vertical = 12.dp, horizontal = 6.dp)
            .clip(RoundedCornerShape(28.dp))
            .border(1.dp, Color.White.copy(alpha = 0.14f), RoundedCornerShape(28.dp)),
        containerColor = containerColor,
    ) {
        Spacer(modifier = Modifier.weight(1f))

        navigationItems.forEach { screen ->
            val isSelected = remember(currentRoute, screen.route) {
                isRouteSelected(currentRoute, screen.route, navigationItems)
            }
            val currentIsSelected by rememberUpdatedState(isSelected)
            val iconRes = remember(isSelected, screen) {
                if (isSelected) screen.iconIdActive else screen.iconIdInactive
            }

            val isSearchItem = screen == Screens.Search && onSearchLongClick != null
            val interactionSource = remember { MutableInteractionSource() }

            if (isSearchItem) {
                LaunchedEffect(interactionSource) {
                    var isLongClick = false
                    interactionSource.interactions.collectLatest { interaction ->
                        when (interaction) {
                            is PressInteraction.Press -> {
                                isLongClick = false
                                delay(viewConfiguration.longPressTimeoutMillis)
                                isLongClick = true
                                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                onSearchLongClick.invoke()
                            }
                            is PressInteraction.Release -> {
                                if (!isLongClick) {
                                    onItemClick(screen, currentIsSelected)
                                }
                            }
                            is PressInteraction.Cancel -> {
                                isLongClick = false
                            }
                        }
                    }
                }
            }

            NavigationRailItem(
                selected = isSelected,
                onClick = {
                    if (!isSearchItem) {
                        onItemClick(screen, currentIsSelected)
                    }
                },
                interactionSource = interactionSource,
                icon = {
                    Icon(
                        painter = painterResource(id = iconRes),
                        contentDescription = stringResource(screen.titleId),
                    )
                },
            )
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
fun AppNavigationBar(
    navigationItems: List<Screens>,
    currentRoute: String?,
    onItemClick: (Screens, Boolean) -> Unit,
    modifier: Modifier = Modifier,
    pureBlack: Boolean = false,
    slimNav: Boolean = false,
    backdrop: Backdrop? = LocalPageGlassBackdrop.current,
    onSearchLongClick: (() -> Unit)? = null,
) {
    val selectedIndex = remember(currentRoute, navigationItems) {
        navigationItems.indexOfFirst { screen ->
            isRouteSelected(currentRoute, screen.route, navigationItems)
        }.coerceAtLeast(0)
    }
    val selectedIndexState by rememberUpdatedState(selectedIndex)
    val currentRouteState by rememberUpdatedState(currentRoute)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = if (GlassSettings.floatingNavBar) 16.dp else 6.dp,
                vertical = if (GlassSettings.floatingNavBar) 6.dp else 2.dp,
            ),
        contentAlignment = Alignment.TopCenter,
    ) {
        LiquidBottomTabs(
            selectedTabIndex = { selectedIndexState },
            onTabSelected = { idx ->
                val target = navigationItems.getOrNull(idx) ?: return@LiquidBottomTabs
                val isSelected = isRouteSelected(currentRouteState, target.route, navigationItems)
                onItemClick(target, isSelected)
            },
            backdrop = backdrop,
            tabsCount = navigationItems.size,
            pureBlack = pureBlack,
            slimNav = slimNav,
            modifier = Modifier
                .widthIn(max = 388.dp)
                .fillMaxWidth(),
        ) {
            navigationItems.forEach { screen ->
                val isSelected = remember(currentRoute, screen.route) {
                    isRouteSelected(currentRoute, screen.route, navigationItems)
                }
                val currentIsSelected by rememberUpdatedState(isSelected)
                val iconRes = remember(isSelected, screen) {
                    if (isSelected) screen.iconIdActive else screen.iconIdInactive
                }
                val isSearchItem = screen == Screens.Search && onSearchLongClick != null

                LiquidNavTabItem(
                    icon = painterResource(id = iconRes),
                    label = stringResource(screen.titleId),
                    selected = isSelected,
                    pureBlack = pureBlack,
                    slimNav = slimNav,
                    onLongClick = if (isSearchItem) onSearchLongClick else null,
                    onClick = { onItemClick(screen, currentIsSelected) },
                )
            }
        }
    }
}
