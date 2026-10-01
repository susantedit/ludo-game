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
import androidx.compose.foundation.verticalScroll
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
import game.ludora.core.designsystem.component.LudoraCard
import game.ludora.core.designsystem.component.LudoraPrimaryButton
import game.ludora.core.designsystem.component.LudoraSecondaryButton
import game.ludora.core.designsystem.component.TurnStatusPill
import game.ludora.core.designsystem.mascot.LudoraMascotView
import game.ludora.core.designsystem.mascot.MascotCatalog
import game.ludora.core.designsystem.mascot.MascotDefinition
import game.ludora.core.designsystem.mascot.MascotSelectorSheet
import game.ludora.core.designsystem.theme.LudoraTheme
import game.ludora.core.designsystem.theme.SlateCard
import game.ludora.core.designsystem.theme.TextPrimary
import game.ludora.core.designsystem.theme.TextSecondary
import game.ludora.core.designsystem.theme.WarmAmberGold
import game.ludora.core.model.PlayerColor

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
                    LudoraDashboardScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun LudoraDashboardScreen(modifier: Modifier = Modifier) {
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
        Spacer(modifier = Modifier.height(24.dp))

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

            TurnStatusPill(
                text = "Offline Ready",
                activeColor = PlayerColor.GREEN
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Interactive Profile & Mascot Stage Card
        LudoraCard(
            modifier = Modifier.fillMaxWidth(),
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
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(SlateCard)
                        .border(2.dp, WarmAmberGold, CircleShape)
                        .clickable { isMascotSheetVisible = true },
                    contentAlignment = Alignment.Center
                ) {
                    LudoraMascotView(
                        mascot = equippedMascot,
                        size = 88.dp,
                        onMascotClick = { /* boop feedback */ }
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Player Profile",
                        style = LudoraTheme.typography.titleMedium,
                        color = TextPrimary
                    )
                    Text(
                        text = "Mascot: ${equippedMascot.name}",
                        style = LudoraTheme.typography.bodyMedium,
                        color = WarmAmberGold
                    )
                    Text(
                        text = "Tap mascot to boop or customize",
                        style = LudoraTheme.typography.bodyMedium,
                        color = TextSecondary,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    LudoraSecondaryButton(
                        text = "Change Mascot (56)",
                        onClick = { isMascotSheetVisible = true },
                        modifier = Modifier.height(38.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Game Modes Title
        Text(
            text = "Game Modes",
            style = LudoraTheme.typography.titleLarge,
            color = TextPrimary,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        )

        // Ludo Mode Card
        GameModeCard(
            title = "Classic Ludo",
            tagline = "2 to 4 Players • Strategy & Captures",
            accentColor = PlayerColor.RED,
            onPlayClick = { /* Navigate to Ludo */ }
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Snake & Ladder Mode Card
        GameModeCard(
            title = "Snake & Ladder",
            tagline = "100 Tiles • Ladders & Snakes Race",
            accentColor = PlayerColor.GREEN,
            onPlayClick = { /* Navigate to Snake */ }
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Remix Mode Card
        GameModeCard(
            title = "Ludora Remix",
            tagline = "Hazards, Power Cards & Chaos Modifiers",
            accentColor = PlayerColor.YELLOW,
            onPlayClick = { /* Navigate to Remix */ }
        )

        Spacer(modifier = Modifier.height(32.dp))
    }

    // Modal Bottom Sheet for 56 Mascot selector
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
    accentColor: PlayerColor,
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
