/**
 * Sakura Tunes Project (C) 2026
 * Crafted by ㅤᏞᴀᴢʏㅤᎷꫝʏᴀɴк まやんく (@xboyrex)
 * Licensed under GPL-3.0
 */

package com.sakuratunes.music.ui.glass

import androidx.compose.ui.draw.alpha

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.HapticFeedbackConstants
import android.view.View
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastCoerceIn
import androidx.compose.ui.util.fastRoundToInt
import androidx.compose.ui.util.lerp
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.effects.vibrancy
import com.kyant.backdrop.highlight.Highlight
import com.kyant.backdrop.shadow.InnerShadow
import com.kyant.backdrop.shadow.Shadow
import com.kyant.shapes.Capsule
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.sign

private fun View.navHaptic(durationMs: Long = 12L, amplitude: Int = 120) {
    val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        context.getSystemService(VibratorManager::class.java)?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }
    val done = vibrator?.hasVibrator() == true && runCatching {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(durationMs, amplitude.coerceIn(1, 255)))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(durationMs)
        }
    }.isSuccess
    if (!done) performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
}

internal val LocalLiquidBottomTabScale = staticCompositionLocalOf { { 1f } }

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RowScope.LiquidBottomTab(
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    val scale = LocalLiquidBottomTabScale.current
    val view = LocalView.current
    val clickMod = if (onLongClick != null) {
        Modifier.combinedClickable(
            interactionSource = null,
            indication = null,
            role = Role.Tab,
            onLongClick = {
                view.navHaptic(22L, 180)
                onLongClick()
            },
            onClick = {
                view.navHaptic(12L, 130)
                onClick()
            },
        )
    } else {
        Modifier.clickable(
            interactionSource = null,
            indication = null,
            role = Role.Tab,
            onClick = {
                view.navHaptic(12L, 130)
                onClick()
            },
        )
    }

    Column(
        modifier
            .clip(Capsule())
            .then(clickMod)
            .fillMaxHeight()
            .weight(1f)
            .graphicsLayer {
                val s = scale()
                scaleX = s
                scaleY = s
            },
        verticalArrangement = Arrangement.spacedBy(2f.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
        content = content,
    )
}

@Composable
fun LiquidBottomTabs(
    selectedTabIndex: () -> Int,
    onTabSelected: (index: Int) -> Unit,
    backdrop: Backdrop?,
    tabsCount: Int,
    pureBlack: Boolean = false,
    slimNav: Boolean = false,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    val isDark = isSystemInDarkTheme() || pureBlack
    val isLightTheme = !isDark
    val glassSupported = GlassQuality.effectsSupported && backdrop != null
    val glassEnabled = GlassSettings.enabled
    val barHeight = if (slimNav) 56.dp else 64.dp
    val pillHeight = if (slimNav) 48.dp else 56.dp

    val solidContainer = if (pureBlack) Color.Black else MaterialTheme.colorScheme.surfaceContainer
    val containerColor = when {
        !glassEnabled -> solidContainer
        pureBlack -> Color(0xFF060408).copy(0.50f)
        isLightTheme -> Color(0xFFFFF8FA).copy(0.45f)
        else -> Color(0xFF141016).copy(0.45f)
    }

    BoxWithConstraints(
        modifier,
        contentAlignment = Alignment.CenterStart,
    ) {
        val density = LocalDensity.current
        val tabWidth = with(density) {
            (constraints.maxWidth.toFloat() - 8f.dp.toPx()) / tabsCount.coerceAtLeast(1)
        }

        val offsetAnimation = remember { Animatable(0f) }
        val panelOffset by remember(density) {
            derivedStateOf {
                val fraction = (offsetAnimation.value / constraints.maxWidth.coerceAtLeast(1)).fastCoerceIn(-1f, 1f)
                with(density) {
                    4f.dp.toPx() * fraction.sign * EaseOut.transform(abs(fraction))
                }
            }
        }

        val isLtr = LocalLayoutDirection.current == LayoutDirection.Ltr
        val animationScope = rememberCoroutineScope()
        val view = LocalView.current
        var currentIndex by remember {
            mutableIntStateOf(selectedTabIndex().coerceIn(0, (tabsCount - 1).coerceAtLeast(0)))
        }

        val dampedDragAnimation = remember(animationScope, tabsCount) {
            var totalDragX = 0f
            DampedDragAnimation(
                animationScope = animationScope,
                initialValue = selectedTabIndex().coerceIn(0, (tabsCount - 1).coerceAtLeast(0)).toFloat(),
                valueRange = 0f..(tabsCount - 1).coerceAtLeast(0).toFloat(),
                visibilityThreshold = 0.001f,
                initialScale = 1f,
                pressedScale = 78f / 56f,
                onDragStarted = {
                    totalDragX = 0f
                },
                onDragStopped = {
                    // Only commit drag selection if the user actually dragged horizontally across tabs.
                    // For single taps, LiquidBottomTab's onClick handles selection directly without conflict!
                    if (totalDragX > tabWidth * 0.18f) {
                        val targetIndex = targetValue.fastRoundToInt().fastCoerceIn(0, (tabsCount - 1).coerceAtLeast(0))
                        view.navHaptic(20L, 190)
                        currentIndex = targetIndex
                        animateToValue(targetIndex.toFloat())
                        onTabSelected(targetIndex)
                    } else {
                        animateToValue(currentIndex.toFloat())
                    }
                    animationScope.launch {
                        offsetAnimation.animateTo(
                            0f,
                            spring(1f, 300f, 0.5f),
                        )
                    }
                },
                onDrag = { _, dragAmount ->
                    totalDragX += abs(dragAmount.x)
                    if (totalDragX > 6f) {
                        val before = targetValue.fastRoundToInt()
                        updateValue(
                            (targetValue + dragAmount.x / tabWidth.coerceAtLeast(1f) * if (isLtr) 1f else -1f)
                                .fastCoerceIn(0f, (tabsCount - 1).coerceAtLeast(0).toFloat()),
                        )
                        if (targetValue.fastRoundToInt() != before) {
                            view.navHaptic(16L, 170)
                        }
                        animationScope.launch {
                            offsetAnimation.snapTo(offsetAnimation.value + dragAmount.x)
                        }
                    }
                },
            )
        }

        LaunchedEffect(selectedTabIndex) {
            snapshotFlow { selectedTabIndex() }
                .collectLatest { index ->
                    val safeIdx = index.coerceIn(0, (tabsCount - 1).coerceAtLeast(0))
                    currentIndex = safeIdx
                    if (dampedDragAnimation.targetValue != safeIdx.toFloat()) {
                        dampedDragAnimation.animateToValue(safeIdx.toFloat())
                    }
                }
        }

        val interactiveHighlight = remember(animationScope) {
            InteractiveHighlight(
                animationScope = animationScope,
                position = { size, _ ->
                    Offset(
                        if (isLtr) (dampedDragAnimation.value + 0.5f) * tabWidth + panelOffset
                        else size.width - (dampedDragAnimation.value + 0.5f) * tabWidth + panelOffset,
                        size.height / 2f,
                    )
                },
            )
        }

        // 1. Bottom navigation bar Liquid Glass background capsule
        Box(
            Modifier
                .graphicsLayer {
                    translationX = panelOffset
                }
                .then(
                    if (glassSupported) {
                        Modifier.drawBackdrop(
                            backdrop = backdrop,
                            shape = { Capsule() },
                            effects = {
                                vibrancy()
                                blur(GlassSettings.blurStrength.dp.toPx() * 0.6f)
                                lens(
                                    24f.dp.toPx(),
                                    GlassSettings.refractionStrength.dp.toPx(),
                                    chromaticAberration = GlassSettings.chromaticAberration,
                                )
                            },
                            layerBlock = {
                                val progress = dampedDragAnimation.pressProgress
                                val scale = lerp(1f, 1f + 16f.dp.toPx() / size.width.coerceAtLeast(1f), progress)
                                scaleX = scale
                                scaleY = scale
                            },
                            onDrawSurface = { drawRect(containerColor) },
                        )
                    } else if (glassEnabled) {
                        Modifier
                            .clip(Capsule())
                            .background(containerColor)
                            .drawBehind {
                                drawRect(
                                    Brush.verticalGradient(
                                        0f to Color.White.copy(alpha = 0.16f),
                                        0.55f to Color.Transparent,
                                        1f to Color.Black.copy(alpha = 0.06f),
                                    ),
                                )
                            }
                            .border(
                                1.dp,
                                Color.White.copy(alpha = 0.18f),
                                Capsule(),
                            )
                    } else {
                        Modifier
                            .clip(Capsule())
                            .background(containerColor)
                            .border(
                                1.dp,
                                MaterialTheme.colorScheme.outlineVariant,
                                Capsule(),
                            )
                    },
                )
                .then(interactiveHighlight.modifier)
                .height(barHeight)
                .fillMaxWidth(),
        )

        // 2. Moving active tab indicator pill behind tab icons/labels
        Box(
            Modifier
                .padding(horizontal = 4f.dp)
                .graphicsLayer {
                    translationX =
                        if (isLtr) dampedDragAnimation.value * tabWidth + panelOffset
                        else size.width - (dampedDragAnimation.value + 1f) * tabWidth + panelOffset
                }
                .then(
                    if (glassSupported) {
                        Modifier.drawBackdrop(
                            backdrop = backdrop,
                            shape = { Capsule() },
                            effects = {
                                val progress = dampedDragAnimation.pressProgress
                                vibrancy()
                                if (progress < 0.999f) blur(8f.dp.toPx() * (1f - progress))
                                lens(
                                    10f.dp.toPx() * progress,
                                    14f.dp.toPx() * progress,
                                    chromaticAberration = GlassSettings.chromaticAberration,
                                )
                            },
                            highlight = {
                                val progress = dampedDragAnimation.pressProgress
                                Highlight.Default.copy(alpha = progress)
                            },
                            shadow = {
                                val progress = dampedDragAnimation.pressProgress
                                Shadow(alpha = progress)
                            },
                            innerShadow = {
                                val progress = dampedDragAnimation.pressProgress
                                InnerShadow(
                                    radius = 8f.dp * progress,
                                    alpha = progress,
                                )
                            },
                            layerBlock = {
                                scaleX = dampedDragAnimation.scaleX
                                scaleY = dampedDragAnimation.scaleY
                                val velocity = dampedDragAnimation.velocity / 10f
                                scaleX /= 1f - (velocity * 0.75f).fastCoerceIn(-0.2f, 0.2f)
                                scaleY *= 1f - (velocity * 0.25f).fastCoerceIn(-0.2f, 0.2f)
                            },
                            onDrawSurface = {
                                val progress = dampedDragAnimation.pressProgress
                                drawRect(
                                    if (isLightTheme) Color(0xFF833761).copy(0.16f)
                                    else Color(0xFFFF85B3).copy(0.18f),
                                    alpha = 1f - progress,
                                )
                                drawRect(Color.Black.copy(alpha = 0.03f * progress))
                            },
                        )
                    } else {
                        Modifier
                            .clip(Capsule())
                            .background(
                                if (glassEnabled) {
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.22f)
                                } else {
                                    MaterialTheme.colorScheme.surfaceContainerHighest
                                }
                            )
                    },
                )
                .height(pillHeight)
                .fillMaxWidth(1f / tabsCount.coerceAtLeast(1)),
        )

        // 3. Tab icons and labels on top with drag gesture support
        CompositionLocalProvider(
            LocalLiquidBottomTabScale provides {
                lerp(1f, 1.15f, dampedDragAnimation.pressProgress)
            },
        ) {
            Row(
                Modifier
                    .graphicsLayer {
                        translationX = panelOffset
                    }
                    .then(interactiveHighlight.gestureModifier)
                    .then(dampedDragAnimation.modifier)
                    .height(barHeight)
                    .fillMaxWidth()
                    .padding(4f.dp),
                verticalAlignment = Alignment.CenterVertically,
                content = content,
            )
        }
    }
}

@Composable
fun RowScope.LiquidNavTabItem(
    icon: Painter,
    label: String,
    selected: Boolean,
    pureBlack: Boolean = false,
    slimNav: Boolean = false,
    onLongClick: (() -> Unit)? = null,
    onClick: () -> Unit,
) {
    val isDark = isSystemInDarkTheme() || pureBlack
    val contentColor = if (selected) {
        if (isDark) Color.White else MaterialTheme.colorScheme.primary
    } else {
        if (isDark) Color.White.copy(alpha = 0.72f) else Color.Black.copy(alpha = 0.72f)
    }

    LiquidBottomTab(onClick = onClick, onLongClick = onLongClick) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(if (slimNav) 24.dp else 26.dp),
        ) {
            Icon(
                painter = icon,
                contentDescription = label,
                tint = contentColor,
                modifier = Modifier.size(22.dp),
            )
        }

        if (!slimNav) {
            Text(
                text = label,
                modifier = Modifier.fillMaxWidth(),
                style = TextStyle(
                    color = contentColor,
                    fontSize = 11.sp,
                    letterSpacing = 0.sp,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                    textAlign = TextAlign.Center,
                ),
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
