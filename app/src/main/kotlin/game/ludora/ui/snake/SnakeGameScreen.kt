package game.ludora.ui.snake

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import game.ludora.core.designsystem.canvas.DiceRenderer
import game.ludora.core.designsystem.canvas.SnakeGridCoordinateMapper
import game.ludora.core.designsystem.canvas.TokenRenderer
import game.ludora.core.designsystem.component.LudoraCard
import game.ludora.core.designsystem.component.LudoraPrimaryButton
import game.ludora.core.designsystem.component.LudoraSecondaryButton
import game.ludora.core.designsystem.component.PlayerAvatarBadge
import game.ludora.core.designsystem.component.TurnStatusPill
import game.ludora.core.designsystem.theme.LudoraTheme
import game.ludora.core.designsystem.theme.SlateCard
import game.ludora.core.designsystem.theme.SlateSurface
import game.ludora.core.designsystem.theme.TextPrimary
import game.ludora.core.designsystem.theme.TextSecondary
import game.ludora.core.designsystem.theme.WarmAmberGold
import game.ludora.engine.snake.SnakeGameEngine
import game.ludora.engine.snake.model.SnakeAction
import game.ludora.engine.snake.model.SnakeBoard
import game.ludora.engine.snake.model.SnakeEvent
import game.ludora.engine.snake.model.SnakeGameState
import game.ludora.engine.snake.model.SnakeTurnPhase
import game.ludora.ui.common.MatchOptions
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

@Composable
fun SnakeGameScreen(
    options: MatchOptions,
    onBackToMenu: () -> Unit,
    onMatchFinished: ((placement: Int, captures: Int, isWin: Boolean, sixesRolled: Int) -> Unit)? = null
) {
    val engine = remember { SnakeGameEngine() }

    var gameState by remember {
        val baseState = engine.getInitialState(options.playerCount)
        val setupPlayers = baseState.players.mapIndexed { idx, pState ->
            val isAi = if (idx == 0) false else options.isVsAi
            pState.copy(
                player = pState.player.copy(
                    isAi = isAi,
                    displayName = if (idx == 0) "You" else if (isAi) "AI ${pState.color.name}" else "Player ${idx + 1}"
                )
            )
        }
        mutableStateOf(baseState.copy(players = setupPlayers))
    }

    var displayedDiceValue by remember { mutableIntStateOf(1) }
    var statusMessage by remember { mutableStateOf("Roll the dice to climb to 100!") }
    var isRollingAnimation by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val activePlayer = gameState.activePlayer

    // AI Turn Controller
    LaunchedEffect(gameState.activeSeatIndex, gameState.phase, gameState.isGameOver) {
        if (gameState.isGameOver) return@LaunchedEffect

        if (activePlayer.player.isAi && gameState.phase == SnakeTurnPhase.WAITING_FOR_ROLL) {
            delay(500)
            isRollingAnimation = true
            repeat(4) {
                displayedDiceValue = Random.nextInt(1, 7)
                delay(70)
            }
            isRollingAnimation = false

            val (nextState, events) = engine.step(gameState, SnakeAction.RollDice())
            gameState = nextState
            displayedDiceValue = nextState.currentRoll ?: 1

            // Parse events for status feedback
            events.forEach { ev ->
                when (ev) {
                    is SnakeEvent.LadderClimbed -> statusMessage = "${activePlayer.player.displayName} climbed a ladder to ${ev.topSquare}!"
                    is SnakeEvent.SnakeDropped -> statusMessage = "${activePlayer.player.displayName} swallowed by snake down to ${ev.tailSquare}!"
                    is SnakeEvent.BonusRollGranted -> statusMessage = "${activePlayer.player.displayName} rolled a 6! Extra roll!"
                    is SnakeEvent.ThreeSixesPenalty -> statusMessage = "Three consecutive sixes! Turn forfeited."
                    else -> Unit
                }
            }
        }
    }

    fun onHumanRoll() {
        if (activePlayer.player.isAi || gameState.phase != SnakeTurnPhase.WAITING_FOR_ROLL || isRollingAnimation) return

        coroutineScope.launch {
            isRollingAnimation = true
            repeat(5) {
                displayedDiceValue = Random.nextInt(1, 7)
                delay(60)
            }
            isRollingAnimation = false

            val (nextState, events) = engine.step(gameState, SnakeAction.RollDice())
            gameState = nextState
            displayedDiceValue = nextState.currentRoll ?: 1

            events.forEach { ev ->
                when (ev) {
                    is SnakeEvent.LadderClimbed -> statusMessage = "Ladder climbed to square ${ev.topSquare}! 🪜"
                    is SnakeEvent.SnakeDropped -> statusMessage = "Oops! Snake slid down to ${ev.tailSquare}! 🐍"
                    is SnakeEvent.BonusRollGranted -> statusMessage = "Rolled a 6! Bonus roll earned! 🎲"
                    is SnakeEvent.ThreeSixesPenalty -> statusMessage = "Three sixes penalty! Turn passed."
                    is SnakeEvent.OvershootStalled -> statusMessage = "Need exact roll to reach 100! Stalled."
                    else -> Unit
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LudoraTheme.colors.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            LudoraSecondaryButton(
                text = "← Exit",
                onClick = onBackToMenu,
                modifier = Modifier.height(36.dp)
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                PlayerAvatarBadge(
                    color = activePlayer.color,
                    isCurrentTurn = true,
                    size = 32.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = activePlayer.player.displayName,
                        style = LudoraTheme.typography.titleSmall,
                        color = TextPrimary
                    )
                    Text(
                        text = "Square ${activePlayer.currentSquare}/100",
                        style = LudoraTheme.typography.bodyMedium,
                        color = WarmAmberGold,
                        fontSize = 11.sp
                    )
                }
            }

            TurnStatusPill(
                text = if (activePlayer.player.isAi) "AI Turn" else "Your Turn",
                activeColor = activePlayer.color
            )
        }

        // 10x10 Boustrophedon Canvas Board
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val boardW = size.width
                val boardH = size.height
                val cellW = boardW / 10f
                val cellH = boardH / 10f

                // 1. Board background
                drawRect(color = SlateSurface)

                // 2. 100 Squares
                for (sq in 1..100) {
                    val (col, row) = SnakeGridCoordinateMapper.getGridCell(sq)
                    val cellLeft = col * cellW
                    val cellTop = boardH - (row + 1) * cellH
                    val isAlt = (col + row) % 2 == 0

                    drawRoundRect(
                        color = if (isAlt) Color(0xFF1E293B) else Color(0xFF0F172A),
                        topLeft = Offset(cellLeft + 1f, cellTop + 1f),
                        size = Size(cellW - 2f, cellH - 2f),
                        cornerRadius = CornerRadius(4f, 4f)
                    )

                    // Special 100 finish highlight
                    if (sq == 100) {
                        drawRoundRect(
                            color = WarmAmberGold.copy(alpha = 0.35f),
                            topLeft = Offset(cellLeft + 1f, cellTop + 1f),
                            size = Size(cellW - 2f, cellH - 2f),
                            cornerRadius = CornerRadius(4f, 4f)
                        )
                    }
                }

                // 3. Render 7 Ladders (golden bridge rails)
                SnakeBoard.LADDERS.forEach { (base, top) ->
                    val fromOffset = SnakeGridCoordinateMapper.squareCenterOffset(base, boardW, boardH)
                    val toOffset = SnakeGridCoordinateMapper.squareCenterOffset(top, boardW, boardH)

                    val railColor = WarmAmberGold.copy(alpha = 0.85f)
                    val perp = Offset(-(toOffset.y - fromOffset.y), toOffset.x - fromOffset.x)
                    val len = perp.getDistance()
                    if (len > 0f) {
                        val normPerp = perp / len * (cellW * 0.16f)

                        // Left rail
                        drawLine(
                            color = railColor,
                            start = fromOffset - normPerp,
                            end = toOffset - normPerp,
                            strokeWidth = 3f,
                            cap = StrokeCap.Round
                        )
                        // Right rail
                        drawLine(
                            color = railColor,
                            start = fromOffset + normPerp,
                            end = toOffset + normPerp,
                            strokeWidth = 3f,
                            cap = StrokeCap.Round
                        )

                        // Rungs
                        val rungs = 5
                        for (i in 1..rungs) {
                            val t = i.toFloat() / (rungs + 1)
                            val centerRung = fromOffset + (toOffset - fromOffset) * t
                            drawLine(
                                color = Color.White.copy(alpha = 0.7f),
                                start = centerRung - normPerp,
                                end = centerRung + normPerp,
                                strokeWidth = 2f
                            )
                        }
                    }
                }

                // 4. Render 8 Snakes (crimson serpentine curves)
                SnakeBoard.SNAKES.forEach { (head, tail) ->
                    val headOffset = SnakeGridCoordinateMapper.squareCenterOffset(head, boardW, boardH)
                    val tailOffset = SnakeGridCoordinateMapper.squareCenterOffset(tail, boardW, boardH)

                    val snakeColor = Color(0xFFEF4444).copy(alpha = 0.80f)
                    val midPoint = (headOffset + tailOffset) / 2f + Offset(cellW * 0.35f, 0f)

                    val path = Path().apply {
                        moveTo(headOffset.x, headOffset.y)
                        quadraticBezierTo(midPoint.x, midPoint.y, tailOffset.x, tailOffset.y)
                    }
                    drawPath(
                        path = path,
                        color = snakeColor,
                        style = Stroke(width = 4f, cap = StrokeCap.Round)
                    )

                    // Snake Head dot
                    drawCircle(
                        color = Color(0xFFDC2626),
                        radius = cellW * 0.18f,
                        center = headOffset
                    )
                }

                // 5. Render Player Tokens
                gameState.players.forEachIndexed { idx, pState ->
                    if (pState.currentSquare > 0) {
                        val offset = SnakeGridCoordinateMapper.squareCenterOffset(pState.currentSquare, boardW, boardH)
                        val slotAdjustment = Offset((idx - 0.5f) * (cellW * 0.25f), 0f)
                        TokenRenderer.drawToken(
                            drawScope = this,
                            center = offset + slotAdjustment,
                            radius = cellW * 0.36f,
                            color = pState.color,
                            isSelectable = false,
                            isLifted = pState.seatIndex == gameState.activeSeatIndex
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Status text
        Text(
            text = statusMessage,
            style = LudoraTheme.typography.bodyMedium,
            color = WarmAmberGold,
            modifier = Modifier.padding(vertical = 4.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Bottom Controls: Tactile 3D Die & Roll Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .pointerInput(Unit) {
                        detectTapGestures { onHumanRoll() }
                    }
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    DiceRenderer.drawDiceFace(
                        drawScope = this,
                        topLeft = Offset(4f, 4f),
                        size = size.width - 8f,
                        value = displayedDiceValue,
                        diceColor = if (isRollingAnimation) Color(0xFFFDE68A) else Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.width(20.dp))

            LudoraPrimaryButton(
                text = if (isRollingAnimation) "Rolling..." else "Roll Dice (${displayedDiceValue})",
                onClick = { onHumanRoll() },
                enabled = !activePlayer.player.isAi && gameState.phase == SnakeTurnPhase.WAITING_FOR_ROLL && !isRollingAnimation,
                modifier = Modifier.height(52.dp)
            )
        }
    }

    // Victory Dialog
    if (gameState.isGameOver) {
        Dialog(onDismissRequest = {}) {
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
                        text = "🎉 Victory!",
                        style = LudoraTheme.typography.headlineMedium,
                        color = WarmAmberGold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Winner: ${gameState.winners.firstOrNull() ?: "Player"}",
                        style = LudoraTheme.typography.titleMedium,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    LudoraPrimaryButton(
                        text = "Claim Rewards & Continue",
                        onClick = {
                            val humanPlayer = gameState.players.firstOrNull { !it.player.isAi } ?: gameState.players.first()
                            val placement = (gameState.winners.indexOf(humanPlayer.color).takeIf { it >= 0 } ?: (gameState.winners.size)) + 1
                            val isWin = placement == 1
                            if (onMatchFinished != null) {
                                onMatchFinished(placement, 0, isWin, 2)
                            } else {
                                onBackToMenu()
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
