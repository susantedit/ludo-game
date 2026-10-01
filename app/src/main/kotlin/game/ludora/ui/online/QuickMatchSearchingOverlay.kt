package game.ludora.ui.online

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import game.ludora.core.designsystem.component.LudoraCard
import game.ludora.core.designsystem.component.LudoraPrimaryButton
import game.ludora.core.designsystem.component.LudoraSecondaryButton
import game.ludora.core.designsystem.theme.LudoraTheme
import game.ludora.core.designsystem.theme.TextPrimary
import game.ludora.core.designsystem.theme.TextSecondary
import game.ludora.core.designsystem.theme.WarmAmberGold
import kotlinx.coroutines.delay

@Composable
fun QuickMatchSearchingOverlay(
    gameTypeLabel: String,
    onCancel: () -> Unit,
    onAutofillWithAi: () -> Unit
) {
    var elapsedSeconds by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            elapsedSeconds++
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "radar")
    val pulseRadius by infiniteTransition.animateFloat(
        initialValue = 20f,
        targetValue = 90f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radius"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "alpha"
    )

    Dialog(
        onDismissRequest = onCancel,
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = false)
    ) {
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
                    text = "Finding Match...",
                    style = LudoraTheme.typography.titleLarge,
                    color = TextPrimary
                )
                Text(
                    text = "$gameTypeLabel • Searching for opponents",
                    style = LudoraTheme.typography.bodyMedium,
                    color = WarmAmberGold
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Radar animation
                Box(
                    modifier = Modifier.size(160.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val center = Offset(size.width / 2f, size.height / 2f)

                        // Outer wave
                        drawCircle(
                            color = WarmAmberGold.copy(alpha = pulseAlpha),
                            radius = pulseRadius,
                            center = center,
                            style = Stroke(width = 3f)
                        )

                        // Center core
                        drawCircle(
                            color = WarmAmberGold,
                            radius = 18f,
                            center = center
                        )
                    }

                    Text(
                        text = "${elapsedSeconds}s",
                        color = Color.White,
                        fontSize = 12.sp,
                        style = LudoraTheme.typography.bodyMedium
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // If searched longer than 15s, offer AI autofill option!
                if (elapsedSeconds >= 10) {
                    LudoraPrimaryButton(
                        text = "Fill remaining slots with Bots",
                        onClick = onAutofillWithAi,
                        modifier = Modifier.fillMaxWidth().height(42.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                LudoraSecondaryButton(
                    text = "Cancel Search",
                    onClick = onCancel,
                    modifier = Modifier.fillMaxWidth().height(40.dp)
                )
            }
        }
    }
}
