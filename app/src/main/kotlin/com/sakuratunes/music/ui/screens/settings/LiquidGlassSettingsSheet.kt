/**
 * Sakura Tunes Project (C) 2026
 * Licensed under GPL-3.0
 */

package com.sakuratunes.music.ui.screens.settings

import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.alpha

import android.content.Context
import android.os.Environment
import android.os.StatFs
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.annotation.ExperimentalCoilApi
import coil3.imageLoader
import com.sakuratunes.music.R
import com.sakuratunes.music.constants.DarkModeKey
import com.sakuratunes.music.constants.DynamicThemeKey
import com.sakuratunes.music.constants.PureBlackKey
import com.sakuratunes.music.constants.SelectedThemeColorKey
import com.sakuratunes.music.ui.glass.GlassSettings
import com.sakuratunes.music.ui.glass.LocalPageGlassBackdrop
import com.sakuratunes.music.ui.glass.glassFill
import com.sakuratunes.music.ui.glass.glassSpec
import com.sakuratunes.music.ui.glass.liquidGlass
import com.sakuratunes.music.ui.glass.pressable
import com.sakuratunes.music.ui.theme.DefaultThemeColor
import com.sakuratunes.music.ui.theme.RoundedHeavy
import com.sakuratunes.music.ui.theme.SakuraLiquidPresets
import com.sakuratunes.music.utils.rememberEnumPreference
import com.sakuratunes.music.utils.rememberPreference
import java.io.File
import kotlin.math.roundToInt

enum class SakuraFloatingModal {
    LIQUID_GLASS_STUDIO,
    STORAGE_DASHBOARD,
}

@Composable
fun SakuraFloatingDialogHost(
    activeModal: SakuraFloatingModal?,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current

    if (activeModal != null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.34f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDismiss,
                ),
        )
    }

    AnimatedContent(
        targetState = activeModal,
        transitionSpec = {
            (scaleIn(spring(dampingRatio = 0.82f, stiffness = 420f), initialScale = 0.90f) + fadeIn(tween(180))) togetherWith
                (scaleOut(tween(130), targetScale = 0.92f) + fadeOut(tween(130)))
        },
        label = "sakuraFloatingModal",
    ) { modal ->
        when (modal) {
            SakuraFloatingModal.LIQUID_GLASS_STUDIO -> {
                SakuraFloatingDialogContainer(
                    title = "Liquid Glass Studio",
                    subtitle = "Refractive shader engine, frost depth & Sakura palettes",
                    onDismiss = onDismiss,
                ) {
                    LiquidGlassStudioContent()
                }
            }
            SakuraFloatingModal.STORAGE_DASHBOARD -> {
                SakuraFloatingDialogContainer(
                    title = "Storage & Memory",
                    subtitle = "Device storage analyzer & Sakura Tunes cache cleaner",
                    onDismiss = onDismiss,
                ) {
                    SakuraStorageDashboardContent(context = context)
                }
            }
            null -> {}
        }
    }
}

@Composable
fun SakuraFloatingDialogContainer(
    title: String,
    subtitle: String,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit,
) {
    val c = MaterialTheme.colorScheme
    val backdrop = LocalPageGlassBackdrop.current
    BackHandler(onBack = onDismiss)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onDismiss,
            ),
        contentAlignment = Alignment.Center,
    ) {
        val dialogShape = RoundedCornerShape(28.dp)
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 20.dp)
                .widthIn(max = 420.dp)
                .fillMaxWidth()
                .then(
                    if (GlassSettings.enabled && backdrop != null) {
                        Modifier.liquidGlass(
                            backdrop,
                            glassSpec(
                                shape = dialogShape,
                                tint = glassFill(0.92f),
                                blur = 26.dp,
                                refractionHeight = 16.dp,
                                refractionAmount = 24.dp,
                            ),
                        )
                    } else {
                        Modifier
                            .clip(dialogShape)
                            .background(c.surfaceContainerHighest)
                            .border(1.dp, c.onSurface.copy(alpha = 0.12f), dialogShape)
                    }
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) { }
                .padding(20.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(c.surfaceContainerHigh)
                        .pressable(onClick = onDismiss),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.arrow_back),
                        contentDescription = "Back",
                        tint = c.onSurface,
                        modifier = Modifier.size(18.dp),
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        text = title,
                        fontFamily = RoundedHeavy,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = c.onSurface,
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        fontSize = 11.5.sp,
                        color = c.onSurfaceVariant,
                    )
                }
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(c.surfaceContainerHigh)
                        .pressable(onClick = onDismiss),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.close),
                        contentDescription = "Close",
                        tint = c.onSurfaceVariant,
                        modifier = Modifier.size(17.dp),
                    )
                }
            }

            HorizontalDivider(
                color = c.onSurface.copy(alpha = 0.08f),
                modifier = Modifier.padding(bottom = 8.dp),
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 470.dp)
                    .verticalScroll(rememberScrollState()),
            ) {
                content()
            }
        }
    }
}

@Composable
fun LiquidGlassStudioContent() {
    val c = MaterialTheme.colorScheme
    val (darkMode, onDarkModeChange) = rememberEnumPreference(DarkModeKey, DarkMode.AUTO)
    val (pureBlack, onPureBlackChange) = rememberPreference(PureBlackKey, defaultValue = false)
    val (selectedThemeColorInt, onSelectedThemeColorChange) = rememberPreference(
        SelectedThemeColorKey,
        DefaultThemeColor.toArgb(),
    )
    val (_, onDynamicThemeChange) = rememberPreference(DynamicThemeKey, defaultValue = true)

    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        SakuraGlassSwitchRow(
            title = "Liquid Glass Engine",
            subtitle = if (GlassSettings.enabled) {
                "Refractive lens shaders, frosted depth & specular highlights active"
            } else {
                "Using solid Material 3 surfaces without translucency"
            },
            checked = GlassSettings.enabled,
            onCheckedChange = { GlassSettings.updateEnabled(it) },
        )

        if (GlassSettings.enabled) {
            HorizontalDivider(color = c.onSurface.copy(alpha = 0.08f), modifier = Modifier.padding(vertical = 6.dp))

            SakuraGlassSwitchRow(
                title = "Floating Capsule Navigation Bar",
                subtitle = "Detach bottom bar into a suspended Liquid Glass capsule",
                checked = GlassSettings.floatingNavBar,
                onCheckedChange = { GlassSettings.updateFloatingNavBar(it) },
            )

            SakuraGlassSwitchRow(
                title = "Chromatic Dispersion",
                subtitle = "Subtle prism color refraction along glass edges",
                checked = GlassSettings.chromaticAberration,
                onCheckedChange = { GlassSettings.updateChromaticAberration(it) },
            )

            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text("Frost Blur Radius", fontWeight = FontWeight.Medium, fontSize = 13.5.sp, color = c.onSurface)
                Text("${GlassSettings.blurStrength.roundToInt()} dp", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = c.primary)
            }
            Slider(
                value = GlassSettings.blurStrength,
                onValueChange = { GlassSettings.updateBlurStrength(it) },
                valueRange = 4f..36f,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text("Lens Refraction Depth", fontWeight = FontWeight.Medium, fontSize = 13.5.sp, color = c.onSurface)
                Text("${GlassSettings.refractionStrength.roundToInt()} dp", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = c.primary)
            }
            Slider(
                value = GlassSettings.refractionStrength,
                onValueChange = { GlassSettings.updateRefractionStrength(it) },
                valueRange = 0f..40f,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        HorizontalDivider(color = c.onSurface.copy(alpha = 0.08f), modifier = Modifier.padding(vertical = 6.dp))

        SakuraGlassSwitchRow(
            title = "Sakura Rounded Display Font",
            subtitle = "Use expressive rounded typography for headers and titles",
            checked = GlassSettings.useRoundedFont,
            onCheckedChange = { GlassSettings.updateUseRoundedFont(it) },
        )

        HorizontalDivider(color = c.onSurface.copy(alpha = 0.08f), modifier = Modifier.padding(vertical = 8.dp))

        Text("Theme Mode", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = c.onSurface)
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            listOf(
                Triple("System", DarkMode.AUTO, false),
                Triple("Dark", DarkMode.ON, false),
                Triple("Light", DarkMode.OFF, false),
                Triple("AMOLED", DarkMode.ON, true),
            ).forEach { (label, targetDark, targetAmoled) ->
                val isSelected = darkMode == targetDark && (targetDark != DarkMode.ON || pureBlack == targetAmoled)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isSelected) c.primary else c.surfaceContainerHigh)
                        .pressable {
                            onDarkModeChange(targetDark)
                            onPureBlackChange(targetAmoled)
                        }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = label,
                        color = if (isSelected) c.onPrimary else c.onSurfaceVariant,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 12.sp,
                    )
                }
            }
        }

        HorizontalDivider(color = c.onSurface.copy(alpha = 0.08f), modifier = Modifier.padding(vertical = 10.dp))

        Text("Sakura Color Palettes", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = c.onSurface)
        Text("Curated blossom & jewel accent themes", fontSize = 11.5.sp, color = c.onSurfaceVariant)
        Spacer(Modifier.height(10.dp))

        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            SakuraLiquidPresets.forEach { preset ->
                val isSelected = selectedThemeColorInt == preset.color.toArgb()
                Column(
                    modifier = Modifier
                        .width(68.dp)
                        .pressable {
                            onSelectedThemeColorChange(preset.color.toArgb())
                            onDynamicThemeChange(preset.color == DefaultThemeColor)
                            GlassSettings.updateSakuraSeedHex(preset.hex)
                        },
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) preset.color.copy(alpha = 0.28f) else Color.Transparent)
                            .padding(4.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(preset.color, preset.color.copy(alpha = 0.65f))
                                )
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (isSelected) {
                            Icon(
                                painter = painterResource(R.drawable.check),
                                contentDescription = "Selected",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp),
                            )
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = preset.name,
                        fontSize = 10.5.sp,
                        maxLines = 1,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) c.onSurface else c.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

private data class SakuraStorageSnapshot(
    val deviceTotal: Long,
    val deviceUsed: Long,
    val deviceFree: Long,
    val appApkSize: Long,
    val appCacheSize: Long,
    val appDataSize: Long,
    val appTotalSize: Long,
)

private fun getFolderSizeRecursive(dir: File?): Long {
    if (dir == null || !dir.exists()) return 0L
    var size = 0L
    dir.listFiles()?.forEach { file ->
        size += if (file.isDirectory) getFolderSizeRecursive(file) else file.length()
    }
    return size
}

private fun formatStorageSize(bytes: Long): String {
    if (bytes <= 0) return "0 B"
    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    val digitGroups = (Math.log10(bytes.toDouble()) / Math.log10(1024.0)).toInt().coerceIn(0, units.size - 1)
    val value = bytes / Math.pow(1024.0, digitGroups.toDouble())
    return if (digitGroups >= 3) {
        "%.1f %s".format(value, units[digitGroups])
    } else {
        "%.0f %s".format(value, units[digitGroups])
    }
}

private fun computeStorageSnapshot(context: Context): SakuraStorageSnapshot {
    return runCatching {
        val stat = StatFs(Environment.getDataDirectory().path)
        val total = stat.totalBytes
        val free = stat.availableBytes
        val used = (total - free).coerceAtLeast(0L)

        val appSize = runCatching { File(context.packageCodePath).length() }.getOrDefault(22_000_000L)
        val cacheSize = getFolderSizeRecursive(context.cacheDir) + (context.externalCacheDir?.let { getFolderSizeRecursive(it) } ?: 0L)
        val dataSize = getFolderSizeRecursive(context.filesDir)

        SakuraStorageSnapshot(
            deviceTotal = total,
            deviceUsed = used,
            deviceFree = free,
            appApkSize = appSize,
            appCacheSize = cacheSize,
            appDataSize = dataSize,
            appTotalSize = appSize + cacheSize + dataSize,
        )
    }.getOrDefault(
        SakuraStorageSnapshot(128_000_000_000L, 45_000_000_000L, 83_000_000_000L, 22_000_000L, 18_000_000L, 12_000_000L, 52_000_000L)
    )
}

@OptIn(ExperimentalCoilApi::class)
@Composable
fun SakuraStorageDashboardContent(context: Context) {
    val c = MaterialTheme.colorScheme
    var storageData by remember { mutableStateOf(computeStorageSnapshot(context)) }
    var clearedNotice by remember { mutableStateOf<String?>(null) }

    val usedGb = storageData.deviceUsed / 1_000_000_000.0
    val totalGb = storageData.deviceTotal / 1_000_000_000.0
    val freeGb = storageData.deviceFree / 1_000_000_000.0
    val progress = (storageData.deviceUsed.toFloat() / storageData.deviceTotal.toFloat().coerceAtLeast(1f)).coerceIn(0f, 1f)

    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(
            text = "Device Storage",
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            color = c.onSurface,
        )
        Spacer(Modifier.height(8.dp))

        var animatedTarget by remember { mutableFloatStateOf(0f) }
        LaunchedEffect(progress) {
            animatedTarget = progress
        }
        val animatedProgress by animateFloatAsState(
            targetValue = animatedTarget,
            animationSpec = spring(dampingRatio = 0.72f, stiffness = 260f),
            label = "storageProgress",
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(c.surfaceContainerHigh),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction = animatedProgress)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(6.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(c.primary, c.tertiary, c.secondary)
                        )
                    ),
            )
        }

        Spacer(Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = "${"%.1f".format(usedGb)} GB used of ${"%.1f".format(totalGb)} GB",
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Medium,
                color = c.onSurface,
            )
            Text(
                text = "${"%.1f".format(freeGb)} GB free",
                fontSize = 11.5.sp,
                color = c.onSurfaceVariant,
            )
        }

        HorizontalDivider(color = c.onSurface.copy(alpha = 0.08f), modifier = Modifier.padding(vertical = 12.dp))

        Text(
            text = "Used by Sakura Tunes",
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            color = c.onSurface,
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = "Total footprint: ${formatStorageSize(storageData.appTotalSize)}",
            fontSize = 12.sp,
            color = c.primary,
            fontWeight = FontWeight.SemiBold,
        )

        Spacer(Modifier.height(10.dp))

        SakuraStorageRow(label = "App & Core Audio Engine", sizeStr = formatStorageSize(storageData.appApkSize))
        SakuraStorageRow(label = "Stream & Artwork Cache", sizeStr = formatStorageSize(storageData.appCacheSize))
        SakuraStorageRow(label = "Database & Saved Data", sizeStr = formatStorageSize(storageData.appDataSize))

        Spacer(Modifier.height(14.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFF2E1517))
                .pressable {
                    runCatching {
                        context.imageLoader.diskCache?.clear()
                        context.cacheDir.deleteRecursively()
                        context.externalCacheDir?.deleteRecursively()
                    }
                    storageData = computeStorageSnapshot(context)
                    clearedNotice = "Sakura Tunes cache cleared!"
                }
                .padding(vertical = 10.dp),
            contentAlignment = Alignment.Center,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(R.drawable.delete),
                    contentDescription = null,
                    tint = Color(0xFFFF6B8B),
                    modifier = Modifier.size(16.dp),
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = clearedNotice ?: "Quick Clear Sakura Tunes Cache",
                    color = Color(0xFFFF6B8B),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                )
            }
        }
    }
}

@Composable
private fun SakuraStorageRow(label: String, sizeStr: String) {
    val c = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, fontSize = 12.sp, color = c.onSurfaceVariant)
        Text(sizeStr, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = c.onSurface)
    }
}

@Composable
fun SakuraGlassSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    val c = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .pressable { onCheckedChange(!checked) }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 12.dp),
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = c.onSurface,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = subtitle,
                color = c.onSurfaceVariant,
                fontSize = 11.5.sp,
                lineHeight = 15.sp,
            )
        }
        Spacer(Modifier.width(6.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedTrackColor = c.primary),
        )
    }
}
