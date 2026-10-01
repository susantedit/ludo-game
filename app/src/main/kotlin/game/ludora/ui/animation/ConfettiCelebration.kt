package game.ludora.ui.animation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private data class ConfettiParticle(
    val color: Color,
    val angle: Double,
    val speed: Float,
    val size: Float,
    val rotationSpeed: Float
)

/**
 * Canvas particle celebration burst for match victory screens.
 * Respects [reducedMotion] by suppressing particles entirely when enabled.
 */
@Composable
fun ConfettiCelebration(
    modifier: Modifier = Modifier,
    particleCount: Int = 60,
    reducedMotion: Boolean = false
) {
    if (reducedMotion) return

    val progress = remember { Animatable(0f) }

    val particles = remember {
        val colors = listOf(
            Color(0xFFFFB300), // Amber Gold
            Color(0xFF3D5AFE), // Indigo
            Color(0xFFE53935), // Red
            Color(0xFF00C853), // Green
            Color(0xFF00E5FF), // Cyan
            Color(0xFFFF4081)  // Pink
        )
        val rng = Random(42)
        List(particleCount) {
            ConfettiParticle(
                color = colors[rng.nextInt(colors.size)],
                angle = rng.nextDouble(0.0, 2.0 * Math.PI),
                speed = rng.nextFloat() * 450f + 200f,
                size = rng.nextFloat() * 10f + 6f,
                rotationSpeed = rng.nextFloat() * 720f - 360f
            )
        }
    }

    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1800, easing = LinearEasing)
        )
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val centerX = size.width / 2f
        val centerY = size.height / 2f
        val t = progress.value
        val gravity = 300f * t * t
        val alpha = (1f - t * 0.8f).coerceIn(0f, 1f)

        for (p in particles) {
            val distance = p.speed * t
            val x = centerX + (cos(p.angle) * distance).toFloat()
            val y = centerY + (sin(p.angle) * distance).toFloat() + gravity
            val currentRotation = p.rotationSpeed * t

            rotate(degrees = currentRotation, pivot = Offset(x, y)) {
                drawRect(
                    color = p.color.copy(alpha = alpha),
                    topLeft = Offset(x - p.size / 2f, y - p.size / 2f),
                    size = Size(p.size, p.size * 1.6f)
                )
            }
        }
    }
}
