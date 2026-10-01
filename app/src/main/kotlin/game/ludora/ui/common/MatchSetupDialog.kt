package game.ludora.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import game.ludora.core.designsystem.component.LudoraCard
import game.ludora.core.designsystem.component.LudoraPrimaryButton
import game.ludora.core.designsystem.component.LudoraSecondaryButton
import game.ludora.core.designsystem.theme.LudoraTheme
import game.ludora.core.designsystem.theme.SlateCard
import game.ludora.core.designsystem.theme.TextPrimary
import game.ludora.core.designsystem.theme.TextSecondary
import game.ludora.core.designsystem.theme.WarmAmberGold
import game.ludora.engine.ai.model.AiDifficulty

data class MatchOptions(
    val playerCount: Int,
    val isVsAi: Boolean,
    val aiDifficulty: AiDifficulty
)

@Composable
fun MatchSetupDialog(
    gameTitle: String,
    onDismiss: () -> Unit,
    onStartMatch: (MatchOptions) -> Unit
) {
    var playerCount by remember { mutableIntStateOf(2) }
    var isVsAi by remember { mutableStateOf(true) }
    var difficulty by remember { mutableStateOf(AiDifficulty.MEDIUM) }

    Dialog(onDismissRequest = onDismiss) {
        LudoraCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            containerColor = LudoraTheme.colors.surfaceElevated,
            borderColor = LudoraTheme.colors.border
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Match Setup",
                    style = LudoraTheme.typography.titleLarge,
                    color = TextPrimary
                )
                Text(
                    text = gameTitle,
                    style = LudoraTheme.typography.bodyMedium,
                    color = WarmAmberGold
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Mode: Vs AI or Local Pass & Play
                Text(
                    text = "Game Mode",
                    style = LudoraTheme.typography.titleSmall,
                    color = TextSecondary,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SelectablePill(
                        text = "Vs Computer",
                        isSelected = isVsAi,
                        modifier = Modifier.weight(1f),
                        onClick = { isVsAi = true }
                    )
                    SelectablePill(
                        text = "Pass & Play",
                        isSelected = !isVsAi,
                        modifier = Modifier.weight(1f),
                        onClick = { isVsAi = false }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Player Count
                Text(
                    text = "Player Count",
                    style = LudoraTheme.typography.titleSmall,
                    color = TextSecondary,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(2, 3, 4).forEach { count ->
                        SelectablePill(
                            text = "$count Players",
                            isSelected = playerCount == count,
                            modifier = Modifier.weight(1f),
                            onClick = { playerCount = count }
                        )
                    }
                }

                if (isVsAi) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "AI Difficulty",
                        style = LudoraTheme.typography.titleSmall,
                        color = TextSecondary,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AiDifficulty.entries.forEach { diff ->
                            SelectablePill(
                                text = diff.name.lowercase().replaceFirstChar { it.uppercase() },
                                isSelected = difficulty == diff,
                                modifier = Modifier.weight(1f),
                                onClick = { difficulty = diff }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    LudoraSecondaryButton(
                        text = "Cancel",
                        onClick = onDismiss
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    LudoraPrimaryButton(
                        text = "Start",
                        onClick = {
                            onStartMatch(MatchOptions(playerCount, isVsAi, difficulty))
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SelectablePill(
    text: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val bg = if (isSelected) WarmAmberGold else SlateCard
    val textColor = if (isSelected) LudoraTheme.colors.background else TextPrimary

    Box(
        modifier = modifier
            .height(40.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(bg)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}
