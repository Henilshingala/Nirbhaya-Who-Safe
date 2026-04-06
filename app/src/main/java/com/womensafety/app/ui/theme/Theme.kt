package com.womensafety.app.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// ============================================
// AURA THEME - "Safe Haven" Design System
// ============================================

// Light Color Scheme - Primary theme
private val AuraLightColors = lightColorScheme(
    primary = AuraPink,
    onPrimary = OnAuraPink,
    primaryContainer = AuraPinkLight,
    onPrimaryContainer = AuraPinkDark,
    
    secondary = AuraBlush,
    onSecondary = OnAuraBlush,
    secondaryContainer = AuraBlushLight,
    onSecondaryContainer = AuraText,
    
    tertiary = AuraLilac,
    onTertiary = OnAuraLilac,
    tertiaryContainer = AuraLilacLight,
    onTertiaryContainer = AuraText,
    
    error = AuraPeachDark,  // Soft peach, not red!
    onError = AuraPaper,
    errorContainer = AuraPeach,
    onErrorContainer = OnAuraPeach,
    
    background = AuraIvory,
    onBackground = AuraText,
    
    surface = AuraPaper,
    onSurface = AuraText,
    surfaceVariant = AuraMist,
    onSurfaceVariant = AuraTextSecondary,
    
    outline = AuraBorder,
    outlineVariant = AuraDivider,
    
    scrim = AuraShadow
)

// Dark Color Scheme - Soft, not harsh
private val AuraDarkColors = darkColorScheme(
    primary = AuraLavenderDark,
    onPrimary = AuraDarkText,
    primaryContainer = AuraLilacDark,
    onPrimaryContainer = AuraDarkText,
    
    secondary = AuraBlushDark,
    onSecondary = AuraDarkText,
    secondaryContainer = AuraRoseDark,
    onSecondaryContainer = AuraDarkText,
    
    tertiary = AuraLilacDark,
    onTertiary = AuraDarkText,
    tertiaryContainer = AuraLilac,
    onTertiaryContainer = AuraDarkText,
    
    error = AuraPeachDark,
    onError = AuraDarkBackground,
    errorContainer = AuraPeach,
    onErrorContainer = AuraDarkText,
    
    background = AuraDarkBackground,
    onBackground = AuraDarkText,
    
    surface = AuraDarkSurface,
    onSurface = AuraDarkText,
    surfaceVariant = AuraDarkBorder,
    onSurfaceVariant = AuraDarkText,
    
    outline = AuraDarkBorder,
    outlineVariant = AuraDarkBorder,
    
    scrim = AuraShadow
)

// Shapes - Rounded & Soft
private val AuraShapesTheme = Shapes(
    extraSmall = RoundedCornerShape(AuraShapes.radiusSmall),
    small = RoundedCornerShape(AuraShapes.radiusMedium),
    medium = RoundedCornerShape(AuraShapes.radiusLarge),
    large = RoundedCornerShape(AuraShapes.radiusXLarge),
    extraLarge = RoundedCornerShape(AuraShapes.radiusXLarge)
)

/**
 * AURA App Theme
 * 
 * A completely new women's safety app design
 * Soft, elegant, trust-building aesthetic
 * NO aggressive colors or alarming visuals
 */
@Composable
fun AuraTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) AuraDarkColors else AuraLightColors
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // enableEdgeToEdge() is called in each Activity for Android 15 compliance.
            // Here we only control icon appearance for the status/navigation bars.
            val windowInsetsController = WindowCompat.getInsetsController(window, view)
            // Light icons on light theme, dark icons on dark theme
            windowInsetsController.isAppearanceLightStatusBars = !darkTheme
            windowInsetsController.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AuraTypography,
        shapes = AuraShapesTheme,
        content = content
    )
}

// Legacy theme name for compatibility (redirects to AuraTheme)
@Composable
fun WomenSafetyAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    AuraTheme(darkTheme = darkTheme, content = content)
}

/**
 * AURA Theme Extensions
 */
val MaterialTheme.alertColor: androidx.compose.ui.graphics.Color
    @Composable
    @ReadOnlyComposable
    get() = AuraPeachDark

