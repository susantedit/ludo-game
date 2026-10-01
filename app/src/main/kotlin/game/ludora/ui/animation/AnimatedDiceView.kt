package game.ludora.ui.animation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import game.ludora.core.designsystem.canvas.DiceRenderer
import kotlinx.coroutines.delay

/**
 * Tactile cubic dice view with 3D rotation, spring bounce, and pip flash.
 * Complies with the 600ms motion timeline in `docs/15_ANIMATION_SPEC.md` and supports [reducedMotion].
 */
@Composable
fun AnimatedDiceView(
    diceValue: Int,
    isRolling: Boolean,
    onRollRequested: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 64.dp,
    reducedMotion: Boolean = false
) {
    val scale = remember { Animatable(1f) }
    val rotation = remember { Animatable(0f) }
    var displayedValue by remember { mutableIntStateOf(diceValue) }

    LaunchedEffect(isRolling) {
        if (isRolling) {
            if (reducedMotion) {
                // Reduced motion: instant 100ms fade without 3D rotations or shakes
                scale.animateTo(0.95f, tween(50, easing = LinearEasing))
                displayedValue = diceValue
                scale.animateTo(1f, tween(50, easing = LinearEasing))
            } else {
                // Phase 1: Lift & Shake (0ms - 150ms)
                scale.animateTo(1.18f, tween(150, easing = FastOutSlowInEasing))
                rotation.animateTo(360f, tween(300, easing = FastOutSlowInEasing))

                // Phase 2: Bounce & Settle (150ms - 450ms)
                displayedValue = diceValue
                scale.animateTo(
                    targetValue = 1f,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium)
                )

                // Reset rotation
                rotation.snapTo(0f)
            }
        } else {
            displayedValue = diceValue
        }
    }

    Box(
        modifier = modifier
            .size(size)
            .scale(scale.value)
            .clickable(enabled = !isRolling) { onRollRequested() },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            rotate(degrees = rotation.value) {
                DiceRenderer.drawDice(
                    drawScope = this,
                    value = displayedValue.coerceIn(1, 6),
                    backgroundColor = Color(0xFFF8FAFC),
                    pipColor = if (displayedValue == 6) Color(0xFFE53935) else Color(0xFF0F172A),
                    size = this.size.minDimension
                )
            }
        }
    }
}
