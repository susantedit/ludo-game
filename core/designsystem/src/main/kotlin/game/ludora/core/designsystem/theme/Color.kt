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
