package com.womensafety.app.ui.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// ============================================
// AURA SPACING - "Generous Breath"
// Luxury spacing for premium feel
// ============================================

object AuraSpacing {
    // Base spacing scale (multiples of 8dp for harmony)
    val space1: Dp = 4.dp    // Minimal
    val space2: Dp = 8.dp    // Tight
    val space3: Dp = 16.dp   // Default
    val space4: Dp = 24.dp   // Comfortable (most used)
    val space5: Dp = 32.dp   // Generous
    val space6: Dp = 48.dp   // Spacious
    val space7: Dp = 64.dp   // Luxurious
    val space8: Dp = 96.dp   // Extra luxurious
    
    // Semantic spacing
    val minimal = space1
    val tight = space2
    val default = space3
    val comfortable = space4
    val generous = space5
    val spacious = space6
    val luxurious = space7
    
    // Screen margins (generous!)
    val screenHorizontal: Dp = space5  // 32dp
    val screenVertical: Dp = space7    // 64dp
    val screenTop: Dp = space7         // 64dp
    val screenBottom: Dp = space6      // 48dp
    
    // Card spacing
    val cardPadding: Dp = space5       // 32dp (generous)
    val cardGap: Dp = space4           // 24dp between cards
    val cardRadius: Dp = 24.dp         // Large radius
    val cardRadiusLarge: Dp = 32.dp    // Extra large radius
    
    // Element spacing
    val elementGap: Dp = space4        // 24dp between elements
    val sectionGap: Dp = space6        // 48dp between sections
    val groupGap: Dp = space3          // 16dp within groups
}

// ============================================
// AURA SIZES - Component Dimensions
// ============================================

object AuraSizes {
    // Icons
    val iconSmall: Dp = 16.dp
    val iconMedium: Dp = 24.dp
    val iconLarge: Dp = 32.dp
    val iconXLarge: Dp = 48.dp
    val iconXXLarge: Dp = 64.dp
    
    // Avatars & Circles
    val avatarSmall: Dp = 40.dp
    val avatarMedium: Dp = 56.dp
    val avatarLarge: Dp = 72.dp
    val avatarXLarge: Dp = 96.dp
    
    // Buttons
    val buttonHeightSmall: Dp = 40.dp
    val buttonHeightMedium: Dp = 48.dp
    val buttonHeightLarge: Dp = 56.dp
    val buttonMinWidth: Dp = 120.dp
    
    // Aura Ring (Main visual element)
    val auraRingSmall: Dp = 120.dp
    val auraRingMedium: Dp = 160.dp
    val auraRingLarge: Dp = 200.dp
    val auraRingStroke: Dp = 8.dp      // Ring thickness
    
    // Floating Orb
    val floatingOrbSize: Dp = 56.dp
    val floatingOrbExpanded: Dp = 200.dp
    
    // Touch targets (Accessibility)
    val touchTargetMin: Dp = 48.dp
    
    // Cards
    val cardHeightMin: Dp = 80.dp
    val cardHeightMedium: Dp = 120.dp
    
    // Input fields
    val inputHeight: Dp = 56.dp
    val inputRadius: Dp = 16.dp
}

// ============================================
// AURA ELEVATION - Soft Shadows
// ============================================

object AuraElevation {
    val none: Dp = 0.dp
    val subtle: Dp = 1.dp
    val soft: Dp = 2.dp
    val medium: Dp = 4.dp
    val floating: Dp = 8.dp
    val elevated: Dp = 12.dp
}

// ==============================================
// AURA ANIMATION - Motion Timings
// ============================================

object AuraMotion {
    // Duration (milliseconds)
    const val instant = 0
    const val veryFast = 100
    const val fast = 200
    const val normal = 300
    const val moderate = 400
    const val slow = 500
    const val verySlow = 600
    const val calm = 800
    const val breathe = 2000  // For breathing animations
    
    // Specific animations
    const val fadeIn = normal
    const val fadeOut = fast
    const val slideIn = moderate
    const val slideOut = normal
    const val scaleUp = fast
    const val scaleDown = fast
    const val auraRingPulse = breathe
    const val gradientRotate = 10000
}

// ============================================
// AURA SHAPES - Border Radius
// ============================================

object AuraShapes {
    val radiusSmall: Dp = 12.dp
    val radiusMedium: Dp = 16.dp
    val radiusLarge: Dp = 24.dp
    val radiusXLarge: Dp = 32.dp
    val radiusFull: Dp = 9999.dp  // Fully rounded (pills/circles)
}

// ============================================
// AURA OPACITY - Transparency Levels
// ============================================

object AuraOpacity {
    const val invisible = 0f
    const val veryLight = 0.1f
    const val light = 0.2f
    const val medium = 0.4f
    const val semitransparent = 0.6f
    const val opaque = 0.8f
    const val full = 1f
    
    // Semantic
    const val disabled = medium
    const val hover = opaque
    const val pressed = semitransparent
}
