package game.ludora

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import game.ludora.core.common.progression.DailyQuestEngine
import game.ludora.core.common.progression.ProgressionEngine
import game.ludora.core.designsystem.component.LudoraCard
import game.ludora.core.designsystem.component.LudoraPrimaryButton
import game.ludora.core.designsystem.component.LudoraSecondaryButton
import game.ludora.core.designsystem.component.TurnStatusPill
import game.ludora.core.designsystem.mascot.LudoraMascotView
import game.ludora.core.designsystem.mascot.MascotCatalog
import game.ludora.core.designsystem.mascot.MascotSelectorSheet
import game.ludora.core.designsystem.theme.LudoraTheme
import game.ludora.core.designsystem.theme.SlateCard
import game.ludora.core.designsystem.theme.TextPrimary
import game.ludora.core.designsystem.theme.TextSecondary
import game.ludora.core.designsystem.theme.WarmAmberGold
import game.ludora.core.model.LocalProfile
import game.ludora.core.model.MatchReward
import game.ludora.core.model.PlayerColor
import game.ludora.engine.ai.model.AiDifficulty
import game.ludora.ui.common.MatchOptions
import game.ludora.ui.common.MatchRewardDialog
import game.ludora.ui.common.MatchSetupDialog
import game.ludora.ui.ludo.LudoGameScreen
import game.ludora.ui.profile.ProfileProgressionSheet
import game.ludora.ui.remix.RemixGameScreen
import game.ludora.ui.snake.SnakeGameScreen

enum class AppScreen {
    DASHBOARD,
    LUDO_GAME,
    SNAKE_GAME,
    REMIX_GAME
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LudoraTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = LudoraTheme.colors.background
                ) { innerPadding ->
                    LudoraRootNavigator(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun LudoraRootNavigator(modifier: Modifier = Modifier) {
    var currentScreen by remember { mutableStateOf(AppScreen.DASHBOARD) }
    var userProfile by remember {
        mutableStateOf(
            LocalProfile(
                profileId = "player_local",
                displayName = "Susant",
                coins = 200L,
                level = 1,
                experiencePoints = 0L
            )
        )
    }
    var activeMatchOptions by remember {
        mutableStateOf(MatchOptions(playerCount = 2, isVsAi = true, aiDifficulty = AiDifficulty.MEDIUM))
    }
    var pendingSetupMode by remember { mutableStateOf<String?>(null) }
    var activeMatchReward by remember { mutableStateOf<MatchReward?>(null) }
    var isProfileSheetVisible by remember { mutableStateOf(false) }

    val handleMatchCompletion: (placement: Int, captures: Int, isWin: Boolean, sixesRolled: Int) -> Unit = { placement, captures, isWin, sixesRolled ->
        val (updatedProfile, reward) = ProgressionEngine.applyMatchOutcome(
            profile = userProfile,
            placement = placement,
            tokensCaptured = captures,
            isWin = isWin
        )
        val updatedQuests = DailyQuestEngine.recordMatchEvents(
            quests = updatedProfile.activeQuests,
            isWin = isWin,
            tokensCaptured = captures,
            sixesRolled = sixesRolled
        )
        userProfile = updatedProfile.copy(activeQuests = updatedQuests)
        activeMatchReward = reward
        currentScreen = AppScreen.DASHBOARD
    }

    when (currentScreen) {
        AppScreen.DASHBOARD -> {
            LudoraDashboardScreen(
                modifier = modifier,
                profile = userProfile,
                onOpenProfileProgression = { isProfileSheetVisible = true },
                onLaunchMode = { mode ->
                    pendingSetupMode = mode
                }
            )

            pendingSetupMode?.let { mode ->
                MatchSetupDialog(
                    gameTitle = mode,
                    onDismiss = { pendingSetupMode = null },
                    onStartMatch = { options ->
                        activeMatchOptions = options
                        currentScreen = when (mode) {
                            "Classic Ludo" -> AppScreen.LUDO_GAME
                            "Snake & Ladder" -> AppScreen.SNAKE_GAME
                            "Ludora Remix" -> AppScreen.REMIX_GAME
                            else -> AppScreen.LUDO_GAME
                        }
                        pendingSetupMode = null
                    }
                )
            }

            if (isProfileSheetVisible) {
                ProfileProgressionSheet(
                    profile = userProfile,
                    onProfileUpdated = { updated -> userProfile = updated },
                    onDismissRequest = { isProfileSheetVisible = false }
                )
            }

            activeMatchReward?.let { reward ->
                MatchRewardDialog(
                    reward = reward,
                    onContinue = { activeMatchReward = null }
                )
            }
        }

        AppScreen.LUDO_GAME -> {
            LudoGameScreen(
                options = activeMatchOptions,
                onBackToMenu = { currentScreen = AppScreen.DASHBOARD },
                onMatchFinished = handleMatchCompletion
            )
        }

        AppScreen.SNAKE_GAME -> {
            SnakeGameScreen(
                options = activeMatchOptions,
                onBackToMenu = { currentScreen = AppScreen.DASHBOARD },
                onMatchFinished = handleMatchCompletion
            )
        }

        AppScreen.REMIX_GAME -> {
            RemixGameScreen(
                options = activeMatchOptions,
                onBackToMenu = { currentScreen = AppScreen.DASHBOARD },
                onMatchFinished = handleMatchCompletion
            )
        }
    }
}

@Composable
fun LudoraDashboardScreen(
    modifier: Modifier = Modifier,
    profile: LocalProfile,
    onOpenProfileProgression: () -> Unit,
    onLaunchMode: (String) -> Unit
) {
    var equippedMascot by remember { mutableStateOf(MascotCatalog.DEFAULT) }
    var isMascotSheetVisible by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(LudoraTheme.colors.background)
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        // App Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "LUDORA",
                    style = LudoraTheme.typography.displayLarge,
                    color = TextPrimary,
                    letterSpacing = 1.5.sp
                )
                Text(
                    text = "Where every roll matters.",
                    style = LudoraTheme.typography.bodyMedium,
                    color = WarmAmberGold
                )
            }

            // Coin counter pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(SlateCard)
                    .border(1.dp, WarmAmberGold.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                    .clickable(onClick = onOpenProfileProgression)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "🪙 ${profile.coins}",
                    color = WarmAmberGold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Interactive Profile & Mascot Stage Card
        LudoraCard(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onOpenProfileProgression),
            containerColor = LudoraTheme.colors.surfaceElevated,
            borderColor = LudoraTheme.colors.border
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Interactive Mascot with boop and gaze tracking
                Box(
                    modifier = Modifier
                        .size(92.dp)
                        .clip(CircleShape)
                        .background(SlateCard)
                        .border(2.dp, WarmAmberGold, CircleShape)
                        .clickable { isMascotSheetVisible = true },
                    contentAlignment = Alignment.Center
                ) {
                    LudoraMascotView(
                        mascot = equippedMascot,
                        size = 80.dp,
                        onMascotClick = { /* boop feedback */ }
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = profile.displayName,
                            style = LudoraTheme.typography.titleMedium,
                            color = TextPrimary
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(WarmAmberGold)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "Lvl ${profile.level}",
                                color = LudoraTheme.colors.background,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // XP Progress bar
                    LinearProgressIndicator(
                        progress = { profile.levelProgressFraction },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = WarmAmberGold,
                        trackColor = SlateCard
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "${profile.currentLevelXpProgress} / ${profile.xpForNextLevel} XP",
                        style = LudoraTheme.typography.bodyMedium,
                        color = TextSecondary,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        LudoraSecondaryButton(
                            text = "Mascot (56)",
                            onClick = { isMascotSheetVisible = true },
                            modifier = Modifier
                                .weight(1f)
                                .height(32.dp)
                        )
                        LudoraPrimaryButton(
                            text = "Quests & Shop",
                            onClick = onOpenProfileProgression,
                            modifier = Modifier
                                .weight(1f)
                                .height(32.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        // Game Modes Title
        Text(
            text = "Game Modes",
            style = LudoraTheme.typography.titleLarge,
            color = TextPrimary,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp)
        )

        // Ludo Mode Card
        GameModeCard(
            title = "Classic Ludo",
            tagline = "2 to 4 Players • Strategy & Captures",
            onPlayClick = { onLaunchMode("Classic Ludo") }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Snake & Ladder Mode Card
        GameModeCard(
            title = "Snake & Ladder",
            tagline = "100 Tiles • Ladders & Snakes Race",
            onPlayClick = { onLaunchMode("Snake & Ladder") }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Remix Mode Card
        GameModeCard(
            title = "Ludora Remix",
            tagline = "Power-ups, Hybrid Hazards & Chaos Modifiers",
            onPlayClick = { onLaunchMode("Ludora Remix") }
        )

        Spacer(modifier = Modifier.height(28.dp))
    }

    if (isMascotSheetVisible) {
        MascotSelectorSheet(
            selectedMascotId = equippedMascot.id,
            onMascotSelected = { selected ->
                equippedMascot = selected
            },
            onDismissRequest = { isMascotSheetVisible = false }
        )
    }
}

@Composable
private fun GameModeCard(
    title: String,
    tagline: String,
    onPlayClick: () -> Unit
) {
    LudoraCard(
        modifier = Modifier.fillMaxWidth(),
        containerColor = LudoraTheme.colors.surfaceElevated,
        borderColor = LudoraTheme.colors.border
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = LudoraTheme.typography.titleMedium,
                    color = TextPrimary
                )
                Text(
                    text = tagline,
                    style = LudoraTheme.typography.bodyMedium,
                    color = TextSecondary,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            LudoraPrimaryButton(
                text = "Play",
                onClick = onPlayClick
            )
        }
    }
}
