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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import game.ludora.core.designsystem.theme.LudoraTheme
import game.ludora.core.designsystem.theme.TextPrimary
import game.ludora.core.designsystem.theme.toColor
import game.ludora.core.model.PlayerColor

@Composable
fun TurnStatusPill(
    text: String,
    modifier: Modifier = Modifier,
    activeColor: PlayerColor? = null,
    isPulsing: Boolean = false
) {
    val highlightColor = activeColor?.toColor() ?: LudoraTheme.colors.secondary
    val borderColor = if (isPulsing) highlightColor else LudoraTheme.colors.border

    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(LudoraTheme.colors.surfaceElevated)
            .border(1.5.dp, borderColor, CircleShape)
            .padding(horizontal = 16.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (activeColor != null) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(highlightColor)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                color = TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
