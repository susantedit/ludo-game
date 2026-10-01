package game.ludora.core.model

import kotlinx.serialization.Serializable

/**
 * Color-blind perception modes adjusting player colors and tile indicators.
 */
@Serializable
enum class ColorBlindMode {
    STANDARD,      // Default high-contrast Obsidian palette
    DEUTERANOPIA,  // Red-green adjustment with cobalt blue and goldenrod
    PROTANOPIA,    // Red-blind adjustment with cyan and bright gold
    TRITANOPIA     // Blue-yellow adjustment with crimson and teal
}

/**
 * Global accessibility preferences configuration.
 */
@Serializable
data class AccessibilityConfig(
    val colorBlindMode: ColorBlindMode = ColorBlindMode.STANDARD,
    val showShapeOverlays: Boolean = true,
    val reducedMotion: Boolean = false,
    val soundEnabled: Boolean = true,
    val soundVolume: Float = 0.8f,
    val hapticIntensity: String = "MEDIUM" // OFF, LIGHT, MEDIUM, STRONG
)
