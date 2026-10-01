package game.ludora.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import game.ludora.core.designsystem.theme.DeepSlate
import game.ludora.core.designsystem.theme.LudoraTheme
import game.ludora.core.designsystem.theme.TextPrimary
import game.ludora.core.designsystem.theme.toColor
import game.ludora.core.model.PlayerColor

/**
 * Geometric shape symbols guaranteeing color-blind accessibility per docs/14_DESIGN_SYSTEM.md.
 */
enum class PlayerAccessibilitySymbol(val glyph: String) {
    CIRCLE("●"),
    TRIANGLE("▲"),
    DIAMOND("◆"),
    SQUARE("■");

    companion object {
        fun from(color: PlayerColor): PlayerAccessibilitySymbol = when (color) {
            PlayerColor.RED -> CIRCLE
            PlayerColor.GREEN -> TRIANGLE
            PlayerColor.YELLOW -> DIAMOND
            PlayerColor.BLUE -> SQUARE
        }
    }
}

/**
 * High-contrast circular player badge displaying avatar initial and accessibility shape symbol.
 */
@Composable
fun PlayerAvatarBadge(
    color: PlayerColor,
    displayName: String,
    modifier: Modifier = Modifier,
    size: Dp = 44.dp,
    isActiveTurn: Boolean = false
) {
    val playerColor = color.toColor()
    val symbol = PlayerAccessibilitySymbol.from(color)
    val initial = displayName.take(1).uppercase().ifEmpty { "?" }

    val borderWidth = if (isActiveTurn) 3.dp else 1.5.dp
    val borderColor = if (isActiveTurn) LudoraTheme.colors.secondary else playerColor

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(DeepSlate)
            .border(borderWidth, borderColor, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = initial,
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(1.dp))
            Text(
                text = symbol.glyph,
                color = playerColor,
                fontSize = 10.sp
            )
        }
    }
}
