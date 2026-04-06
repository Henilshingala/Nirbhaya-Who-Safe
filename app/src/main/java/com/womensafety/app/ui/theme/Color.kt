package com.womensafety.app.ui.theme

import androidx.compose.ui.graphics.Color

// ============================================
// AURA COLOR SYSTEM - "Lavender Dreams"
// Soft, Premium, Trust-Building
// ============================================

// PRIMARY - Ethereal Lavender (Main Brand)
val AuraLavender = Color(0xFFE0C3FC)                     // Primary
val AuraLavenderLight = Color(0xFFF3E5FE)                // Light variant
val AuraLavenderDark = Color(0xFFCE93D8)                 // Dark variant
val OnAuraLavender = Color(0xFF4A2C54)                   // Text on lavender

// BRAND PINK - Vibrant & Protective
val AuraPink = Color(0xFFE91E63)                         // Brand Primary
val AuraPinkLight = Color(0xFFFCE4EC)                    // Brand Light
val AuraPinkDark = Color(0xFFC2185B)                     // Brand Dark
val OnAuraPink = Color(0xFFFFFFFF)                       // Text on Pink

// SECONDARY - Soft Blush
val AuraBlush = Color(0xFFFFE5F1)                        // Secondary
val AuraBlushLight = Color(0xFFFFF0F7)                   // Light variant
val AuraBlushDark = Color(0xFFF8BBD0)                    // Dark variant
val OnAuraBlush = Color(0xFF5D3A4A)                      // Text on blush

// TERTIARY - Muted Lilac
val AuraLilac = Color(0xFFD4BFFF)                        // Tertiary/Accent
val AuraLilacLight = Color(0xFFEAD9FF)                   // Light variant
val AuraLilacDark = Color(0xFFB39DDB)                    // Dark variant
val OnAuraLilac = Color(0xFF4A2C54)                      // Text on lilac

// BACKGROUND - Warm Ivory
val AuraIvory = Color(0xFFFFF9F5)                        // Main background
val AuraPaper = Color(0xFFFFFFFF)                        // Pure white for cards
val AuraMist = Color(0xFFF5F3F7)                         // Subtle tint

// TEXT - Deep Aubergine
val AuraText = Color(0xFF4A2C54)                         // Primary text
val AuraTextSecondary = Color(0xFF6E6680)                // Secondary text
val AuraTextTertiary = Color(0xFF9E9AAB)                 // Tertiary text

// SUCCESS - Gentle Mint
val AuraMint = Color(0xFFE0F4F1)                         // Success background
val AuraMintDark = Color(0xFF80CBC4)                     // Success emphasis
val OnAuraMint = Color(0xFF004D40)                       // Text on mint

// ALERT - Peach Whisper (NOT red!)
val AuraPeach = Color(0xFFFFE8DD)                        // Alert background
val AuraPeachDark = Color(0xFFFFB74D)                    // Alert emphasis
val OnAuraPeach = Color(0xFF5D4037)                      // Text on peach

// INFO - Calm Sky
val AuraSky = Color(0xFFE3F0FF)                          // Info background
val AuraSkyDark = Color(0xFF90CAF9)                      // Info emphasis
val OnAuraSky = Color(0xFF0D47A1)                        // Text on sky

// EMPHASIS - Rose Dust
val AuraRose = Color(0xFFF8E8EE)                         // Emphasis background
val AuraRoseDark = Color(0xFFF48FB1)                     // Emphasis highlight
val OnAuraRose = Color(0xFF880E4F)                       // Text on rose

// BORDERS & DIVIDERS - Silver Mist
val AuraBorder = Color(0xFFE8E4EA)                       // Soft borders
val AuraDivider = Color(0xFFF0EDF2)                      // Subtle dividers
val AuraShadow = Color(0x1A4A2C54)                       // Soft shadow (10% opacity)

// SPECIAL - Gradients (defined as pairs for easy use)
object AuraGradients {
    val Primary = Pair(AuraLavender, AuraBlush)          // Main gradient
    val Trust = Pair(AuraLilac, AuraMint)                // Success gradient
    val Care = Pair(AuraPeach, AuraIvory)                // Warm gradient
    val Calm = Pair(AuraSky, AuraLavender)               // Info gradient
}

// DARK THEME (Soft, not harsh)
val AuraDarkBackground = Color(0xFF2C2231)               // Deep purple-grey
val AuraDarkSurface = Color(0xFF3A2F40)                  // Slightly lighter
val AuraDarkText = Color(0xFFF5F0F7)                     // Soft white
val AuraDarkBorder = Color(0xFF4A3F51)                   // Muted border
