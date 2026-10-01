package game.ludora.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import game.ludora.core.model.PlayerColor

// Core Brand Tokens (Obsidian Dark palette)
val ObsidianBlack = Color(0xFF0B0F19)
val DeepSlate = Color(0xFF0F172A)
val SlateCard = Color(0xFF1E293B)
val SlateBorder = Color(0xFF334155)

val PrimaryIndigo = Color(0xFF3D5AFE)
val PrimaryIndigoLight = Color(0xFF7585FF)
val PrimaryContainer = Color(0xFF1E293B)

val WarmAmberGold = Color(0xFFFFB300)
val AmberGoldLight = Color(0xFFFFE57F)

val TextPrimary = Color(0xFFF8FAFC)
val TextSecondary = Color(0xFF94A3B8)
val TextTertiary = Color(0xFF64748B)

// High-Contrast Harmonized Player Palette
val PlayerRed = Color(0xFFE53935)
val PlayerRedDark = Color(0xFFB71C1C)
val PlayerRedSubtle = Color(0x33E53935)

val PlayerGreen = Color(0xFF00C853)
val PlayerGreenDark = Color(0xFF00701A)
val PlayerGreenSubtle = Color(0x3300C853)

val PlayerYellow = Color(0xFFFFD600)
val PlayerYellowDark = Color(0xFFF57F17)
val PlayerYellowSubtle = Color(0x33FFD600)

val PlayerBlue = Color(0xFF2979FF)
val PlayerBlueDark = Color(0xFF0D47A1)
val PlayerBlueSubtle = Color(0x332979FF)

// Safe square star highlight
val StarSquareGold = Color(0xFFFFC107)

@Immutable
data class LudoraColorTokens(
    val background: Color = ObsidianBlack,
    val surface: Color = DeepSlate,
    val surfaceElevated: Color = SlateCard,
    val border: Color = SlateBorder,
    val primary: Color = PrimaryIndigo,
    val primaryContainer: Color = PrimaryContainer,
    val secondary: Color = WarmAmberGold,
    val textPrimary: Color = TextPrimary,
    val textSecondary: Color = TextSecondary,
    val textTertiary: Color = TextTertiary,
    val playerRed: Color = PlayerRed,
    val playerGreen: Color = PlayerGreen,
    val playerYellow: Color = PlayerYellow,
    val playerBlue: Color = PlayerBlue
)

fun PlayerColor.toColor(): Color = when (this) {
    PlayerColor.RED -> PlayerRed
    PlayerColor.GREEN -> PlayerGreen
    PlayerColor.YELLOW -> PlayerYellow
    PlayerColor.BLUE -> PlayerBlue
}

fun PlayerColor.toAccessibleColor(mode: game.ludora.core.model.ColorBlindMode): Color = when (mode) {
    game.ludora.core.model.ColorBlindMode.STANDARD -> toColor()
    game.ludora.core.model.ColorBlindMode.DEUTERANOPIA -> when (this) {
        PlayerColor.RED -> Color(0xFFE66101) // High-contrast Vermilion
        PlayerColor.GREEN -> Color(0xFF009688) // Distinct Teal
        PlayerColor.YELLOW -> Color(0xFFFFC107) // Amber Gold
        PlayerColor.BLUE -> Color(0xFF5E3C99) // Deep Purple-Blue
    }
    game.ludora.core.model.ColorBlindMode.PROTANOPIA -> when (this) {
        PlayerColor.RED -> Color(0xFF0288D1) // Bright Sky Blue
        PlayerColor.GREEN -> Color(0xFF2E7D32) // Forest Pine
        PlayerColor.YELLOW -> Color(0xFFFFEB3B) // High-visibility Lemon
        PlayerColor.BLUE -> Color(0xFF673AB7) // Indigo Purple
    }
    game.ludora.core.model.ColorBlindMode.TRITANOPIA -> when (this) {
        PlayerColor.RED -> Color(0xFFD32F2F) // Crimson Red
        PlayerColor.GREEN -> Color(0xFF4CAF50) // Emerald Green
        PlayerColor.YELLOW -> Color(0xFFFF7043) // Coral Salmon
        PlayerColor.BLUE -> Color(0xFF00ACC1) // Deep Cyan
    }
}

fun PlayerColor.toDarkColor(): Color = when (this) {
    PlayerColor.RED -> PlayerRedDark
    PlayerColor.GREEN -> PlayerGreenDark
    PlayerColor.YELLOW -> PlayerYellowDark
    PlayerColor.BLUE -> PlayerBlueDark
}

fun PlayerColor.toSubtleColor(): Color = when (this) {
    PlayerColor.RED -> PlayerRedSubtle
    PlayerColor.GREEN -> PlayerGreenSubtle
    PlayerColor.YELLOW -> PlayerYellowSubtle
    PlayerColor.BLUE -> PlayerBlueSubtle
}
