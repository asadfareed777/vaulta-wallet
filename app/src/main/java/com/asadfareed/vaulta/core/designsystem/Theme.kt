package com.asadfareed.vaulta.core.designsystem

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

object VaultaColors {
    val Ink = Color(0xFF0A0E1A)
    val Surface = Color(0xFF121829)
    val SurfaceHigh = Color(0xFF1A2236)
    val Outline = Color(0xFF263049)
    val Mint = Color(0xFF3DDC97)
    val Violet = Color(0xFF7C5CFF)
    val Coral = Color(0xFFFF6B6B)
    val Amber = Color(0xFFFFB547)
    val Sky = Color(0xFF4CC9F0)
    val Pink = Color(0xFFF472B6)
    val TextPrimary = Color(0xFFF4F6FB)
    val TextSecondary = Color(0xFF8A93A8)

    val CardGradient = Brush.linearGradient(listOf(Color(0xFF7C5CFF), Color(0xFF4F46E5), Color(0xFF1E3A8A)))
    val VirtualCardGradient = Brush.linearGradient(listOf(Color(0xFF0F766E), Color(0xFF3DDC97)))
    val FrozenGradient = Brush.linearGradient(listOf(Color(0xFF334155), Color(0xFF64748B)))
}

private val VaultaColorScheme = darkColorScheme(
    primary = VaultaColors.Mint,
    onPrimary = VaultaColors.Ink,
    secondary = VaultaColors.Violet,
    onSecondary = Color.White,
    background = VaultaColors.Ink,
    onBackground = VaultaColors.TextPrimary,
    surface = VaultaColors.Surface,
    onSurface = VaultaColors.TextPrimary,
    surfaceVariant = VaultaColors.SurfaceHigh,
    onSurfaceVariant = VaultaColors.TextSecondary,
    outline = VaultaColors.Outline,
    error = VaultaColors.Coral,
)

private val Sans = FontFamily.SansSerif

private val VaultaTypography = Typography(
    displaySmall = TextStyle(fontFamily = Sans, fontWeight = FontWeight.Bold, fontSize = 36.sp, letterSpacing = (-0.5).sp),
    headlineSmall = TextStyle(fontFamily = Sans, fontWeight = FontWeight.SemiBold, fontSize = 22.sp),
    titleLarge = TextStyle(fontFamily = Sans, fontWeight = FontWeight.SemiBold, fontSize = 20.sp),
    titleMedium = TextStyle(fontFamily = Sans, fontWeight = FontWeight.SemiBold, fontSize = 16.sp),
    bodyLarge = TextStyle(fontFamily = Sans, fontWeight = FontWeight.Normal, fontSize = 16.sp),
    bodyMedium = TextStyle(fontFamily = Sans, fontWeight = FontWeight.Normal, fontSize = 14.sp),
    labelLarge = TextStyle(fontFamily = Sans, fontWeight = FontWeight.Medium, fontSize = 14.sp),
    labelMedium = TextStyle(fontFamily = Sans, fontWeight = FontWeight.Medium, fontSize = 12.sp),
)

@Composable
fun VaultaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = VaultaColorScheme,
        typography = VaultaTypography,
        content = content,
    )
}
