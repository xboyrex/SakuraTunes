/**
 * Sakura Tunes Project (C) 2026
 * Licensed under GPL-3.0
 */

package com.sakuratunes.music.ui.component

import androidx.compose.ui.draw.alpha

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import kotlin.math.abs

private val SakuraAvatarPalettes = listOf(
    listOf(Color(0xFFFF6BA0), Color(0xFF833761)),
    listOf(Color(0xFFD4C0FF), Color(0xFF6C2BD9)),
    listOf(Color(0xFFC5B1F0), Color(0xFF7454CE)),
    listOf(Color(0xFFFF85B3), Color(0xFFD6005C)),
    listOf(Color(0xFFE97A3A), Color(0xFFBC5628)),
    listOf(Color(0xFF80CBC4), Color(0xFF00796B)),
)

/**
 * Deterministic Sakura Glass avatar for a username, rendered natively in Compose.
 */
@Composable
fun PeekAvatar(
    name: String,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
) {
    val hash = remember(name) { abs(name.hashCode()) }
    val palette = remember(hash) { SakuraAvatarPalettes[hash % SakuraAvatarPalettes.size] }
    val eyeStyle = remember(hash) { (hash / 7) % 3 }
    val smileCurve = remember(hash) { ((hash / 13) % 3) - 1 }

    Canvas(
        modifier = modifier
            .clip(CircleShape)
            .then(
                if (contentDescription != null) {
                    Modifier.semantics { this.contentDescription = contentDescription }
                } else {
                    Modifier
                }
            )
    ) {
        val w = size.width
        val h = size.height

        drawRect(
            brush = Brush.linearGradient(
                colors = palette,
                start = Offset.Zero,
                end = Offset(w, h)
            )
        )

        // Subtle glass highlight arc at top
        drawCircle(
            color = Color.White.copy(alpha = 0.18f),
            radius = w * 0.45f,
            center = Offset(w * 0.35f, h * 0.25f)
        )

        // Eyes
        val eyeY = h * 0.42f
        val leftX = w * 0.35f
        val rightX = w * 0.65f
        val eyeRadius = w * 0.065f

        when (eyeStyle) {
            0 -> {
                drawCircle(Color.White, radius = eyeRadius, center = Offset(leftX, eyeY))
                drawCircle(Color.White, radius = eyeRadius, center = Offset(rightX, eyeY))
            }
            1 -> {
                drawArc(
                    color = Color.White,
                    startAngle = 180f,
                    sweepAngle = 180f,
                    useCenter = false,
                    topLeft = Offset(leftX - eyeRadius, eyeY - eyeRadius * 0.6f),
                    size = Size(eyeRadius * 2f, eyeRadius * 1.4f),
                )
                drawArc(
                    color = Color.White,
                    startAngle = 180f,
                    sweepAngle = 180f,
                    useCenter = false,
                    topLeft = Offset(rightX - eyeRadius, eyeY - eyeRadius * 0.6f),
                    size = Size(eyeRadius * 2f, eyeRadius * 1.4f),
                )
            }
            else -> {
                drawCircle(Color.White, radius = eyeRadius * 1.1f, center = Offset(leftX, eyeY))
                drawCircle(Color.White, radius = eyeRadius * 0.85f, center = Offset(rightX, eyeY))
            }
        }

        // Blush cheeks
        drawCircle(
            color = Color(0xFFFFB6C1).copy(alpha = 0.45f),
            radius = w * 0.08f,
            center = Offset(w * 0.24f, h * 0.54f)
        )
        drawCircle(
            color = Color(0xFFFFB6C1).copy(alpha = 0.45f),
            radius = w * 0.08f,
            center = Offset(w * 0.76f, h * 0.54f)
        )

        // Mouth
        val mouthWidth = w * 0.24f
        val mouthHeight = h * (0.12f + smileCurve * 0.02f)
        drawArc(
            color = Color.White,
            startAngle = 15f,
            sweepAngle = 150f,
            useCenter = false,
            topLeft = Offset((w - mouthWidth) / 2f, h * 0.52f),
            size = Size(mouthWidth, mouthHeight),
        )
    }
}
