/**
 * Sakura Tunes Project (C) 2026
 * Licensed under GPL-3.0
 */

package com.sakuratunes.music.ui.component

import androidx.compose.ui.draw.alpha

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sakuratunes.music.R
import com.sakuratunes.music.ui.glass.GlassSettings
import com.sakuratunes.music.ui.glass.SakuraPetalShape
import com.sakuratunes.music.ui.glass.pressable

/**
 * Sakura Tunes Liquid Glass Expressive settings group component
 */
@Composable
fun Material3SettingsGroup(
    title: String? = null,
    items: List<Material3SettingsItem>,
    useLowContrast: Boolean = false,
) {
    val c = MaterialTheme.colorScheme
    val glassEnabled = GlassSettings.enabled

    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        title?.let {
            Text(
                text = it.uppercase(),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp,
                color = c.primary.copy(alpha = 0.88f),
                modifier = Modifier.padding(start = 6.dp, bottom = 8.dp, top = 12.dp),
            )
        }

        val groupShape = RoundedCornerShape(22.dp)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize()
                .clip(groupShape)
                .background(
                    if (glassEnabled) {
                        c.surfaceContainerHigh.copy(alpha = if (useLowContrast) 0.55f else 0.72f)
                    } else {
                        if (useLowContrast) c.surfaceContainerLow else c.surfaceContainerHigh
                    }
                )
                .then(
                    if (glassEnabled) {
                        Modifier
                            .drawBehind {
                                drawRect(
                                    Brush.verticalGradient(
                                        0f to Color.White.copy(alpha = 0.10f),
                                        0.5f to Color.Transparent,
                                        1f to Color.Black.copy(alpha = 0.04f),
                                    )
                                )
                            }
                            .border(1.dp, c.onSurface.copy(alpha = 0.09f), groupShape)
                    } else {
                        Modifier
                    }
                ),
        ) {
            items.forEachIndexed { index, item ->
                Material3SettingsItemRow(item = item)
                if (index < items.lastIndex) {
                    HorizontalDivider(
                        color = c.onSurface.copy(alpha = 0.06f),
                        modifier = Modifier.padding(horizontal = 18.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun Material3SettingsItemRow(
    item: Material3SettingsItem,
) {
    val c = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (item.enabled && item.onClick != null) {
                    Modifier.pressable(pressedScale = 0.98f, onClick = { item.onClick.invoke() })
                } else {
                    Modifier
                }
            )
            .padding(horizontal = 16.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (item.leadingContent != null) {
            item.leadingContent.invoke()
            Spacer(modifier = Modifier.width(14.dp))
        } else if (item.icon != null) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(if (item.isHighlighted) SakuraPetalShape else CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(
                                c.primary.copy(alpha = if (item.isHighlighted) 0.24f else 0.14f),
                                c.tertiary.copy(alpha = 0.08f),
                            )
                        )
                    )
                    .border(
                        width = 0.8.dp,
                        color = c.primary.copy(alpha = 0.22f),
                        shape = if (item.isHighlighted) SakuraPetalShape else CircleShape,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                if (item.showBadge) {
                    BadgedBox(
                        badge = {
                            Badge(containerColor = c.error)
                        },
                    ) {
                        Icon(
                            painter = item.icon,
                            contentDescription = null,
                            tint = if (!item.enabled) c.onSurface.copy(alpha = 0.38f) else c.primary,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                } else {
                    Icon(
                        painter = item.icon,
                        contentDescription = null,
                        tint = if (!item.enabled) c.onSurface.copy(alpha = 0.38f) else c.primary,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))
        }

        Column(
            modifier = Modifier.weight(1f),
        ) {
            ProvideTextStyle(
                MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    color = if (!item.enabled) c.onSurface.copy(alpha = 0.38f) else c.onSurface,
                ),
            ) {
                item.title()
            }

            item.description?.let { desc ->
                Spacer(modifier = Modifier.height(2.dp))
                ProvideTextStyle(
                    MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        color = if (!item.enabled) c.onSurface.copy(alpha = 0.38f) else c.onSurfaceVariant.copy(alpha = 0.85f),
                    ),
                ) {
                    desc()
                }
            }
        }

        if (item.trailingContent != null) {
            Spacer(modifier = Modifier.width(10.dp))
            item.trailingContent.invoke()
        } else if (item.onClick != null && item.enabled) {
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                painter = painterResource(R.drawable.navigate_next),
                contentDescription = null,
                tint = c.onSurfaceVariant.copy(alpha = 0.55f),
                modifier = Modifier.size(18.dp),
            )
        }
    }
}

data class Material3SettingsItem(
    val icon: Painter? = null,
    val leadingContent: (@Composable () -> Unit)? = null,
    val title: @Composable () -> Unit,
    val description: (@Composable () -> Unit)? = null,
    val trailingContent: (@Composable () -> Unit)? = null,
    val showBadge: Boolean = false,
    val isHighlighted: Boolean = false,
    val enabled: Boolean = true,
    val onClick: (() -> Unit)? = null,
)
