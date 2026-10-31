/**
 * Sakura Tunes Project (C) 2026
 * Licensed under GPL-3.0
 */

package com.sakuratunes.music.ui.glass

import androidx.compose.ui.draw.alpha

import android.app.ActivityManager
import android.content.Context
import android.content.SharedPreferences
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.effects.vibrancy
import com.kyant.backdrop.highlight.Highlight
import com.kyant.backdrop.isRuntimeShaderSupported
import com.kyant.backdrop.shadow.InnerShadow
import com.kyant.backdrop.shadow.Shadow

/**
 * Central engine to decide whether the refractive Liquid Glass shaders can run.
 * Older Android versions and low-RAM devices gracefully use a frosted translucent
 * fallback with specular sheen and hairline border.
 */
object GlassQuality {
    @Volatile
    private var deviceSupports = isRuntimeShaderSupported()

    var effectsSupported by mutableStateOf(deviceSupports)
        private set

    fun init(context: Context) {
        val am = context.getSystemService(ActivityManager::class.java)
        deviceSupports = isRuntimeShaderSupported() && am?.isLowRamDevice != true
        effectsSupported = deviceSupports && GlassSettings.enabled
    }

    fun setEnabled(enabled: Boolean) {
        effectsSupported = deviceSupports && enabled
    }
}

/**
 * User-facing Liquid Glass & Sakura UI customization preferences.
 */
object GlassSettings {
    private const val PREFS = "sakura_appearance"
    private const val KEY_GLASS = "liquid_glass_enabled"
    private const val KEY_BLUR = "liquid_glass_blur"
    private const val KEY_REFRACTION = "liquid_glass_refraction"
    private const val KEY_CHROMATIC = "liquid_glass_chromatic"
    private const val KEY_NAV_PILL = "liquid_glass_floating_nav"
    private const val KEY_ROUNDED_FONT = "sakura_rounded_font"
    private const val KEY_SAKURA_ACCENT = "sakura_accent_seed"

    private var prefs: SharedPreferences? = null

    var enabled by mutableStateOf(true)
        private set

    var blurStrength by mutableFloatStateOf(18f)
        private set

    var refractionStrength by mutableFloatStateOf(22f)
        private set

    var chromaticAberration by mutableStateOf(true)
        private set

    var floatingNavBar by mutableStateOf(true)
        private set

    var useRoundedFont by mutableStateOf(true)
        private set

    var sakuraSeedHex by mutableStateOf<String?>(null)
        private set

    fun init(context: Context) {
        val p = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).also { prefs = it }
        enabled = p.getBoolean(KEY_GLASS, true)
        blurStrength = p.getFloat(KEY_BLUR, 18f)
        refractionStrength = p.getFloat(KEY_REFRACTION, 22f)
        chromaticAberration = p.getBoolean(KEY_CHROMATIC, true)
        floatingNavBar = p.getBoolean(KEY_NAV_PILL, true)
        useRoundedFont = p.getBoolean(KEY_ROUNDED_FONT, true)
        sakuraSeedHex = p.getString(KEY_SAKURA_ACCENT, null)
        GlassQuality.setEnabled(enabled)
    }

    fun updateEnabled(value: Boolean) {
        enabled = value
        prefs?.edit()?.putBoolean(KEY_GLASS, value)?.apply()
        GlassQuality.setEnabled(value)
    }

    fun updateBlurStrength(value: Float) {
        blurStrength = value.coerceIn(4f, 36f)
        prefs?.edit()?.putFloat(KEY_BLUR, blurStrength)?.apply()
    }

    fun updateRefractionStrength(value: Float) {
        refractionStrength = value.coerceIn(0f, 40f)
        prefs?.edit()?.putFloat(KEY_REFRACTION, refractionStrength)?.apply()
    }

    fun updateChromaticAberration(value: Boolean) {
        chromaticAberration = value
        prefs?.edit()?.putBoolean(KEY_CHROMATIC, value)?.apply()
    }

    fun updateFloatingNavBar(value: Boolean) {
        floatingNavBar = value
        prefs?.edit()?.putBoolean(KEY_NAV_PILL, value)?.apply()
    }

    fun updateUseRoundedFont(value: Boolean) {
        useRoundedFont = value
        prefs?.edit()?.putBoolean(KEY_ROUNDED_FONT, value)?.apply()
    }

    fun updateSakuraSeedHex(value: String?) {
        sakuraSeedHex = value
        prefs?.edit()?.putString(KEY_SAKURA_ACCENT, value)?.apply()
    }
}

@Composable
fun glassFill(alpha: Float = 1f, dark: Boolean = isSystemInDarkTheme()): Color {
    if (!GlassQuality.effectsSupported && !GlassSettings.enabled) {
        return MaterialTheme.colorScheme.surfaceContainerHigh
    }
    return if (dark) {
        Color(0xFF141016).copy(alpha = (0.42f * alpha).coerceIn(0f, 1f))
    } else {
        Color(0xFFFFF8FA).copy(alpha = (0.40f * alpha).coerceIn(0f, 1f))
    }
}

@Composable
fun glassOverlayFill(alpha: Float = 1f): Color =
    if (!GlassQuality.effectsSupported && !GlassSettings.enabled) {
        Color(0xFF1C161E)
    } else {
        glassFill(alpha)
    }

/** Global backdrop captured behind the page and player surfaces for refractive Liquid Glass controls. */
val LocalGlassBackdrop = staticCompositionLocalOf<Backdrop?> { null }
val LocalPageGlassBackdrop = staticCompositionLocalOf<Backdrop?> { null }

@Immutable
data class GlassSpec(
    val shape: Shape,
    val tint: Color,
    val blur: Dp,
    val refractionHeight: Dp,
    val refractionAmount: Dp,
    val shadow: Shadow?,
    val highlight: Highlight?,
    val innerShadow: InnerShadow?,
    val chromaticAberration: Boolean,
)

@Composable
fun glassSpec(
    shape: Shape = RoundedCornerShape(26.dp),
    tint: Color = glassFill(),
    blur: Dp = GlassSettings.blurStrength.dp,
    refractionHeight: Dp = 14.dp,
    refractionAmount: Dp = GlassSettings.refractionStrength.dp,
    shadow: Shadow? = Shadow.Default,
    highlight: Highlight? = Highlight.Default,
    innerShadow: InnerShadow? = InnerShadow.Default.copy(radius = 10.dp, alpha = 0.5f),
    chromaticAberration: Boolean = GlassSettings.chromaticAberration,
): GlassSpec = GlassSpec(
    shape = shape,
    tint = if (GlassQuality.effectsSupported || GlassSettings.enabled) tint else tint.copy(alpha = 1f),
    blur = blur,
    refractionHeight = refractionHeight,
    refractionAmount = refractionAmount,
    shadow = shadow,
    highlight = highlight,
    innerShadow = innerShadow,
    chromaticAberration = chromaticAberration,
)

fun Modifier.liquidGlass(backdrop: Backdrop?, spec: GlassSpec): Modifier =
    if (GlassQuality.effectsSupported && backdrop != null) {
        drawBackdrop(
            backdrop = backdrop,
            shape = { spec.shape },
            effects = {
                vibrancy()
                blur(spec.blur.toPx())
                lens(
                    spec.refractionHeight.toPx(),
                    spec.refractionAmount.toPx(),
                    chromaticAberration = spec.chromaticAberration,
                )
            },
            highlight = spec.highlight?.let { h -> { h } },
            shadow = spec.shadow?.let { s -> { s } },
            innerShadow = spec.innerShadow?.let { s -> { s } },
            onDrawSurface = {
                if (spec.tint.isSpecified) drawRect(spec.tint)
            },
        )
    } else {
        fallbackGlass(spec)
    }

fun Modifier.fallbackGlass(spec: GlassSpec): Modifier =
    if (!GlassSettings.enabled) {
        this
            .clip(spec.shape)
            .background(spec.tint.copy(alpha = 1f))
    } else {
        this
            .clip(spec.shape)
            .background(spec.tint)
            .drawBehind {
                drawRect(
                    Brush.verticalGradient(
                        0f to Color.White.copy(alpha = 0.16f),
                        0.55f to Color.Transparent,
                        1f to Color.Black.copy(alpha = 0.06f),
                    ),
                )
            }
            .border(1.dp, Color.White.copy(alpha = 0.18f), spec.shape)
    }
