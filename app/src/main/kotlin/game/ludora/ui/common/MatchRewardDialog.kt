package game.ludora.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.window.Dialog
import game.ludora.core.designsystem.component.LudoraCard
import game.ludora.core.designsystem.component.LudoraPrimaryButton
import game.ludora.core.designsystem.theme.LudoraTheme
import game.ludora.core.designsystem.theme.SlateCard
import game.ludora.core.designsystem.theme.TextPrimary
import game.ludora.core.designsystem.theme.TextSecondary
import game.ludora.core.designsystem.theme.WarmAmberGold
import game.ludora.core.model.MatchReward

@Composable
fun MatchRewardDialog(
    reward: MatchReward,
    onContinue: () -> Unit
) {
    Dialog(onDismissRequest = onContinue) {
        LudoraCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            containerColor = LudoraTheme.colors.surfaceElevated,
            borderColor = WarmAmberGold
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (reward.placement == 1) "🏆 Victory!" else "Match Completed",
                    style = LudoraTheme.typography.headlineMedium,
                    color = WarmAmberGold
                )
                Text(
                    text = "Placement: #${reward.placement} • Captures: ${reward.captures}",
                    style = LudoraTheme.typography.bodyMedium,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Rewards breakdown
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(SlateCard)
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "XP Earned", color = TextSecondary, fontSize = 12.sp)
                            Text(
                                text = "+${reward.xpEarned} XP",
                                color = Color(0xFF38BDF8),
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(SlateCard)
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "Coins", color = TextSecondary, fontSize = 12.sp)
                            Text(
                                text = "+${reward.coinsEarned} 🪙",
                                color = WarmAmberGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }
                }

                // Level Up celebration pill if applicable
                if (reward.isLevelUp) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFF59E0B).copy(alpha = 0.2f))
                            .border(1.dp, WarmAmberGold, RoundedCornerShape(8.dp))
                            .padding(10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "🎉 LEVEL UP!",
                                color = WarmAmberGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "You reached Level ${reward.newLevel}!",
                                color = TextPrimary,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                if (reward.unlockedCosmetic != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "🎁 Unlocked: ${reward.unlockedCosmetic.name} (${reward.unlockedCosmetic.category.name.lowercase().replace('_', ' ')})",
                        color = Color(0xFF4ADE80),
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(22.dp))

                LudoraPrimaryButton(
                    text = "Continue",
                    onClick = onContinue,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
