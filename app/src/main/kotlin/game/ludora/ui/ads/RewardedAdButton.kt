package game.ludora.ui.ads

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import game.ludora.core.designsystem.theme.DeepSlate
import game.ludora.core.designsystem.theme.PlayerGreen
import game.ludora.core.designsystem.theme.SlateBorder
import game.ludora.core.designsystem.theme.SlateCard
import game.ludora.core.designsystem.theme.TextPrimary
import game.ludora.core.designsystem.theme.TextSecondary
import game.ludora.core.designsystem.theme.TextTertiary
import game.ludora.core.designsystem.theme.WarmAmberGold

/**
 * Opt-in button for rewarded video ads.
 * Displays reward value, daily watch quota, and offline status badge.
 */
@Composable
fun RewardedAdButton(
    rewardTitle: String,
    remainingToday: Int,
    isOnline: Boolean,
    onWatchAdClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isEnabled = isOnline && remainingToday > 0

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isEnabled) DeepSlate else SlateCard.copy(alpha = 0.6f))
            .border(
                width = 1.dp,
                color = if (isEnabled) PlayerGreen.copy(alpha = 0.5f) else SlateBorder,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(enabled = isEnabled) { onWatchAdClicked() }
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "🎬", fontSize = 24.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Watch Ad",
                            color = if (isEnabled) TextPrimary else TextTertiary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (isEnabled) WarmAmberGold.copy(alpha = 0.2f) else Color(0xFF334155))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = rewardTitle,
                                color = if (isEnabled) WarmAmberGold else TextTertiary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Text(
                        text = if (!isOnline) {
                            "Requires active internet connection"
                        } else if (remainingToday <= 0) {
                            "Daily limit reached (resets in 24h)"
                        } else {
                            "$remainingToday / 5 remaining today"
                        },
                        color = if (!isOnline) Color(0xFFEF4444) else TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isEnabled) PlayerGreen else Color(0xFF334155))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = if (isEnabled) "Claim" else if (!isOnline) "Offline" else "Done",
                    color = if (isEnabled) Color.Black else TextTertiary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
