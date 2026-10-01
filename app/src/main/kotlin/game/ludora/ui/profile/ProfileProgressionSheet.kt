package game.ludora.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import game.ludora.core.common.progression.DailyQuestEngine
import game.ludora.core.common.progression.ProgressionEngine
import game.ludora.core.designsystem.component.LudoraCard
import game.ludora.core.designsystem.component.LudoraPrimaryButton
import game.ludora.core.designsystem.component.LudoraSecondaryButton
import game.ludora.core.designsystem.theme.LudoraTheme
import game.ludora.core.designsystem.theme.SlateCard
import game.ludora.core.designsystem.theme.TextPrimary
import game.ludora.core.designsystem.theme.TextSecondary
import game.ludora.core.designsystem.theme.WarmAmberGold
import game.ludora.core.model.CosmeticCategory
import game.ludora.core.model.CosmeticItem
import game.ludora.core.model.DailyQuest
import game.ludora.core.model.LocalProfile

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileProgressionSheet(
    profile: LocalProfile,
    onProfileUpdated: (LocalProfile) -> Unit,
    onDismissRequest: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Stats, 1 = Cosmetics, 2 = Quests

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = LudoraTheme.colors.surfaceElevated,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .padding(20.dp)
        ) {
            // Header: Level Badge, Coins, Close
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(WarmAmberGold),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "L${profile.level}",
                            color = LudoraTheme.colors.background,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = profile.displayName,
                            style = LudoraTheme.typography.titleMedium,
                            color = TextPrimary
                        )
                        Text(
                            text = "Level ${profile.level} Master",
                            style = LudoraTheme.typography.bodyMedium,
                            color = WarmAmberGold,
                            fontSize = 12.sp
                        )
                    }
                }

                // Coin Counter Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(SlateCard)
                        .border(1.dp, WarmAmberGold.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "🪙 ${profile.coins}",
                        color = WarmAmberGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // XP Progress Bar
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "XP Progress",
                        style = LudoraTheme.typography.bodyMedium,
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "${profile.currentLevelXpProgress} / ${profile.xpForNextLevel} XP",
                        style = LudoraTheme.typography.bodyMedium,
                        color = WarmAmberGold,
                        fontSize = 12.sp
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { profile.levelProgressFraction },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = WarmAmberGold,
                    trackColor = SlateCard
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Navigation Tabs (Stats, Cosmetics, Daily Quests)
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = SlateCard,
                contentColor = WarmAmberGold
            ) {
                listOf("Stats", "Cosmetics", "Quests").forEachIndexed { idx, title ->
                    Tab(
                        selected = selectedTab == idx,
                        onClick = { selectedTab = idx },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTab == idx) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 14.sp
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (selectedTab) {
                0 -> StatsTabContent(profile)
                1 -> CosmeticsTabContent(profile, onProfileUpdated)
                2 -> QuestsTabContent(profile, onProfileUpdated)
            }
        }
    }
}

@Composable
private fun StatsTabContent(profile: LocalProfile) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        StatRow(label = "Matches Played", value = "${profile.statsMatchesPlayed}")
        StatRow(label = "Matches Won", value = "${profile.statsMatchesWon}")
        StatRow(label = "Win Rate", value = "${String.format("%.1f", profile.winRate)}%")
        StatRow(label = "Tokens Captured", value = "${profile.statsTokensCaptured}")
        StatRow(label = "Total Cumulative XP", value = "${profile.experiencePoints} XP")
    }
}

@Composable
private fun StatRow(label: String, value: String) {
    LudoraCard(
        modifier = Modifier.fillMaxWidth(),
        containerColor = SlateCard,
        borderColor = LudoraTheme.colors.border
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = label, style = LudoraTheme.typography.bodyMedium, color = TextSecondary)
            Text(text = value, style = LudoraTheme.typography.titleSmall, color = TextPrimary, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun CosmeticsTabContent(
    profile: LocalProfile,
    onProfileUpdated: (LocalProfile) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(CosmeticItem.ALL_COSMETICS) { item ->
            val isUnlocked = profile.unlockedCosmeticIds.contains(item.id)
            val isEquipped = when (item.category) {
                CosmeticCategory.DICE_SKIN -> profile.equippedDiceSkinId == item.id
                CosmeticCategory.TOKEN_STYLE -> profile.equippedTokenStyleId == item.id
                CosmeticCategory.BOARD_THEME -> profile.equippedBoardThemeId == item.id
            }

            LudoraCard(
                modifier = Modifier.fillMaxWidth(),
                containerColor = SlateCard,
                borderColor = if (isEquipped) WarmAmberGold else LudoraTheme.colors.border
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(Color(android.graphics.Color.parseColor(item.previewHex)))
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(text = item.name, style = LudoraTheme.typography.titleSmall, color = TextPrimary)
                            Text(text = item.description, style = LudoraTheme.typography.bodyMedium, color = TextSecondary, fontSize = 11.sp)
                        }
                    }

                    if (isEquipped) {
                        Text(
                            text = "EQUIPPED",
                            color = WarmAmberGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    } else if (isUnlocked) {
                        LudoraPrimaryButton(
                            text = "Equip",
                            onClick = {
                                val updated = ProgressionEngine.equipCosmetic(profile, item)
                                onProfileUpdated(updated)
                            },
                            modifier = Modifier.height(34.dp)
                        )
                    } else {
                        LudoraSecondaryButton(
                            text = if (profile.level < item.unlockLevel) "Lvl ${item.unlockLevel}" else "${item.coinPrice} 🪙",
                            onClick = {
                                val updated = ProgressionEngine.purchaseCosmetic(profile, item)
                                onProfileUpdated(updated)
                            },
                            enabled = profile.coins >= item.coinPrice && profile.level >= item.unlockLevel,
                            modifier = Modifier.height(34.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun QuestsTabContent(
    profile: LocalProfile,
    onProfileUpdated: (LocalProfile) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(profile.activeQuests) { quest ->
            LudoraCard(
                modifier = Modifier.fillMaxWidth(),
                containerColor = SlateCard,
                borderColor = if (quest.isCompleted && !quest.isClaimed) WarmAmberGold else LudoraTheme.colors.border
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = quest.title, style = LudoraTheme.typography.titleSmall, color = TextPrimary)
                            Text(text = quest.description, style = LudoraTheme.typography.bodyMedium, color = TextSecondary, fontSize = 11.sp)
                        }

                        if (quest.isClaimed) {
                            Text(text = "Claimed ✓", color = TextSecondary, fontSize = 12.sp)
                        } else if (quest.isCompleted) {
                            LudoraPrimaryButton(
                                text = "Claim",
                                onClick = {
                                    val (updated, _) = DailyQuestEngine.claimQuest(profile, quest.id)
                                    onProfileUpdated(updated)
                                },
                                modifier = Modifier.height(34.dp)
                            )
                        } else {
                            Text(
                                text = "${quest.currentProgress}/${quest.targetProgress}",
                                color = WarmAmberGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    LinearProgressIndicator(
                        progress = { quest.progressFraction },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = if (quest.isCompleted) WarmAmberGold else Color(0xFF38BDF8),
                        trackColor = Color(0xFF1E293B)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row {
                        Text(text = "Reward: +${quest.xpReward} XP", color = TextSecondary, fontSize = 11.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = "+${quest.coinReward} Coins 🪙", color = WarmAmberGold, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}
