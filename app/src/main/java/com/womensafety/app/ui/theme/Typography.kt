package com.womensafety.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// ============================================
// AURA TYPOGRAPHY - "Grace & Clarity"
// Elegant Serifs + Clean Sans-Serif
// ============================================

// Font Families (Replace with actual Google Fonts in production)
// Display: Gilda Display or Cormorant (elegant serifs)
// Body: Inter or Manrope (clean sans-serif)
val AuraDisplayFont = FontFamily.Serif    // Replace with Gilda Display
val AuraBodyFont = FontFamily.SansSerif   // Replace with Inter

// Complete Material 3 Typography Scale
val AuraTypography = Typography(
    // DISPLAY - Hero text (Welcome screens)
    displayLarge = TextStyle(
        fontFamily = AuraDisplayFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 48.sp,
        lineHeight = 56.sp,
        letterSpacing = 0.sp
    ),
    displayMedium = TextStyle(
        fontFamily = AuraDisplayFont,
        fontWeight = FontWeight.Medium,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = (-0.5).sp
    ),
    displaySmall = TextStyle(
        fontFamily = AuraDisplayFont,
        fontWeight = FontWeight.Medium,
        fontSize = 30.sp,
        lineHeight = 38.sp,
        letterSpacing = 0.sp
    ),
    
    // HEADLINE - Section headers
    headlineLarge = TextStyle(
        fontFamily = AuraDisplayFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = AuraBodyFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = AuraBodyFont,
        fontWeight = FontWeight.Medium,
        fontSize = 20.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    
    // TITLE - Card headers
    titleLarge = TextStyle(
        fontFamily = AuraBodyFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = AuraBodyFont,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    titleSmall = TextStyle(
        fontFamily = AuraBodyFont,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    
    // BODY - Main content
    bodyLarge = TextStyle(
        fontFamily = AuraBodyFont,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 26.sp,  // 1.6x for readability
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = AuraBodyFont,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 22.sp,  // 1.57x
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontFamily = AuraBodyFont,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 18.sp,  // 1.5x
        letterSpacing = 0.4.sp
    ),
    
    // LABEL - Buttons, labels
    labelLarge = TextStyle(
        fontFamily = AuraBodyFont,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 1.sp  // Wide spacing for labels
    ),
    labelMedium = TextStyle(
        fontFamily = AuraBodyFont,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = AuraBodyFont,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)

// ============================================
// CUSTOM AURA TEXT STYLES
// ============================================

// Welcome Hero Text
val AuraWelcomeStyle = TextStyle(
    fontFamily = AuraDisplayFont,
    fontWeight = FontWeight.Bold,
    fontSize = 48.sp,
    lineHeight = 56.sp,
    letterSpacing = 0.sp
)

// Tagline/Subtitle
val AuraTaglineStyle = TextStyle(
    fontFamily = AuraBodyFont,
    fontWeight = FontWeight.Normal,
    fontSize = 16.sp,
    lineHeight = 26.sp,
    letterSpacing = 0.5.sp
)

// Button Text (Primary actions)
val AuraButtonStyle = TextStyle(
    fontFamily = AuraBodyFont,
    fontWeight = FontWeight.Medium,
    fontSize = 16.sp,
    lineHeight = 24.sp,
    letterSpacing = 1.sp
)

// Status Text (Protection level, etc.)
val AuraStatusStyle = TextStyle(
    fontFamily = AuraBodyFont,
    fontWeight = FontWeight.Medium,
    fontSize = 14.sp,
    lineHeight = 20.sp,
    letterSpacing = 0.5.sp
)

// Relationship Label (Sister, Mom, etc.)
val AuraRelationshipStyle = TextStyle(
    fontFamily = AuraBodyFont,
    fontWeight = FontWeight.Normal,
    fontSize = 12.sp,
    lineHeight = 16.sp,
    letterSpacing = 0.4.sp
)

// SOS Button Text (Impactful but elegant)
val SOSButtonTextStyle = TextStyle(
    fontFamily = AuraBodyFont,
    fontWeight = FontWeight.ExtraBold,
    fontSize = 32.sp,
    lineHeight = 40.sp,
    letterSpacing = 2.sp
)
