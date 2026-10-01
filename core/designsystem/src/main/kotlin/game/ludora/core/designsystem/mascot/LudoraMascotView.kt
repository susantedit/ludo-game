package game.ludora.core.designsystem.mascot

import android.graphics.BitmapFactory
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

/**
 * Interactive Page-Mascot component that tracks user touch gestures and expresses reactions on tap.
 */
@Composable
fun LudoraMascotView(
    mascot: MascotDefinition,
    modifier: Modifier = Modifier,
    size: Dp = 120.dp,
    targetOffset: Offset? = null,
    onMascotClick: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Load and cache bitmap sheets
    val directionsBitmap = remember(mascot.directionsAsset) {
        runCatching {
            context.assets.open(mascot.directionsAsset).use { input ->
                BitmapFactory.decodeStream(input)?.asImageBitmap()
            }
        }.getOrNull()
    }

    val reactionsBitmap = remember(mascot.reactionsAsset) {
        runCatching {
            context.assets.open(mascot.reactionsAsset).use { input ->
                BitmapFactory.decodeStream(input)?.asImageBitmap()
            }
        }.getOrNull()
    }

    var mascotCenterInRoot by remember { mutableStateOf(Offset.Zero) }
    var currentDirection by remember { mutableStateOf(MascotDirection.CENTER) }
    var activeReaction by remember { mutableStateOf<MascotReaction?>(null) }
    val squashScaleX = remember { Animatable(1f) }
    val squashScaleY = remember { Animatable(1f) }

    // External target tracking
    LaunchedEffect(targetOffset, mascotCenterInRoot) {
        if (targetOffset != null && mascotCenterInRoot != Offset.Zero && activeReaction == null) {
            val dx = targetOffset.x - mascotCenterInRoot.x
            val dy = targetOffset.y - mascotCenterInRoot.y
            currentDirection = MascotDirection.fromDelta(dx, dy)
        }
    }

    val triggerBoop = {
        scope.launch {
            // Pick a fun reaction
            val reaction = MascotReaction.TAP_REACTIONS[Random.nextInt(MascotReaction.TAP_REACTIONS.size)]
            activeReaction = reaction

            // Squash and stretch spring
            launch {
                squashScaleX.animateTo(1.15f, spring(dampingRatio = 0.4f))
                squashScaleX.animateTo(1.0f, spring(dampingRatio = 0.6f))
            }
            launch {
                squashScaleY.animateTo(0.85f, spring(dampingRatio = 0.4f))
                squashScaleY.animateTo(1.0f, spring(dampingRatio = 0.6f))
            }

            delay(650)
            activeReaction = null
        }
        onMascotClick?.invoke()
    }

    Box(
        modifier = modifier
            .size(size)
            .scale(squashScaleX.value, squashScaleY.value)
            .onGloballyPositioned { coordinates ->
                val pos = coordinates.positionInRoot()
                mascotCenterInRoot = Offset(
                    x = pos.x + coordinates.size.width / 2f,
                    y = pos.y + coordinates.size.height / 2f
                )
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { triggerBoop() }
                )
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDrag = { change, _ ->
                        change.consume()
                        val dx = change.position.x - size.toPx() / 2f
                        val dy = change.position.y - size.toPx() / 2f
                        if (activeReaction == null) {
                            currentDirection = MascotDirection.fromDelta(dx, dy)
                        }
                    },
                    onDragEnd = {
                        if (activeReaction == null) {
                            currentDirection = MascotDirection.CENTER
                        }
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        val (sheet, cellIndex) = if (activeReaction != null && reactionsBitmap != null) {
            reactionsBitmap to activeReaction!!.cellIndex
        } else {
            directionsBitmap to currentDirection.cellIndex
        }

        if (sheet != null) {
            Canvas(modifier = Modifier.size(size)) {
                val cellW = sheet.width / 3
                val cellH = sheet.height / 3
                val col = cellIndex % 3
                val row = cellIndex / 3

                val srcOffset = IntOffset(col * cellW, row * cellH)
                val srcSize = IntSize(cellW, cellH)
                val dstSize = IntSize(this.size.width.toInt(), this.size.height.toInt())

                drawImage(
                    image = sheet,
                    srcOffset = srcOffset,
                    srcSize = srcSize,
                    dstSize = dstSize
                )
            }
        }
    }
}
