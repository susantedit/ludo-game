package game.ludora.ui.ludo

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import game.ludora.core.designsystem.canvas.DiceRenderer
import game.ludora.core.designsystem.canvas.LudoGridCoordinateMapper
import game.ludora.core.designsystem.canvas.TokenRenderer
import game.ludora.core.designsystem.component.LudoraCard
import game.ludora.core.designsystem.component.LudoraIconButton
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
import game.ludora.core.designsystem.theme.toColor
import game.ludora.core.designsystem.theme.toDarkColor
import game.ludora.core.model.Player
import game.ludora.core.model.PlayerColor
import game.ludora.engine.ai.LudoAiAgent
import game.ludora.engine.ai.model.AiDifficulty
import game.ludora.engine.ludo.LudoGameEngine
import game.ludora.engine.ludo.logic.LegalMove
import game.ludora.engine.ludo.logic.LudoMoveValidator
import game.ludora.engine.ludo.model.LudoAction
import game.ludora.engine.ludo.model.LudoBoard
import game.ludora.engine.ludo.model.LudoGameState
import game.ludora.engine.ludo.model.LudoPlayerState
import game.ludora.engine.ludo.model.LudoPosition
import game.ludora.engine.ludo.model.LudoTurnPhase
import game.ludora.ui.common.MatchOptions
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

@Composable
fun LudoGameScreen(
    options: MatchOptions,
    onBackToMenu: () -> Unit,
    onMatchFinished: ((placement: Int, captures: Int, isWin: Boolean, sixesRolled: Int) -> Unit)? = null
) {
    val engine = remember { LudoGameEngine() }

    // Initialize players based on MatchOptions
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

    var displayedDiceValue by remember { mutableIntStateOf(6) }
    var statusMessage by remember { mutableStateOf("Roll the dice to begin!") }
    var legalMoves by remember { mutableStateOf<List<LegalMove>>(emptyList()) }
    var isRollingAnimation by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    // Determine selectable token IDs for active player
    val selectableTokenIds = remember(gameState, legalMoves) {
        if (gameState.phase == LudoTurnPhase.WAITING_FOR_MOVE) {
            legalMoves.map { it.tokenId }.toSet()
        } else {
            emptySet()
        }
    }

    // AI Turn Controller
    val activePlayer = gameState.activePlayer
    LaunchedEffect(gameState.activeSeatIndex, gameState.phase, gameState.isGameOver) {
        if (gameState.isGameOver) return@LaunchedEffect

        if (activePlayer.player.isAi) {
            delay(500)
            if (gameState.phase == LudoTurnPhase.WAITING_FOR_ROLL) {
                // AI rolls dice
                isRollingAnimation = true
                repeat(4) {
                    displayedDiceValue = Random.nextInt(1, 7)
                    delay(80)
                }
                isRollingAnimation = false

                val (nextState, events) = engine.step(gameState, LudoAction.RollDice())
                gameState = nextState
                displayedDiceValue = nextState.currentRoll ?: 1

                if (nextState.phase == LudoTurnPhase.WAITING_FOR_MOVE) {
                    val roll = nextState.currentRoll ?: 1
                    val chosenMove = LudoAiAgent.selectMove(
                        state = nextState,
                        rollValue = roll,
                        difficulty = options.aiDifficulty
                    )
                    delay(400)
                    if (chosenMove != null) {
                        val (moveState, _) = engine.step(nextState, LudoAction.SelectMove(chosenMove.tokenId))
                        gameState = moveState
                        legalMoves = emptyList()
                    }
                }
            }
        } else {
            // Human player turn setup
            if (gameState.phase == LudoTurnPhase.WAITING_FOR_MOVE) {
                val roll = gameState.currentRoll ?: 1
                legalMoves = LudoMoveValidator.getLegalMoves(gameState, roll)
                if (legalMoves.isEmpty()) {
                    statusMessage = "No legal moves available. Turn passed."
                } else if (legalMoves.size == 1) {
                    // Auto-execute single forced move
                    delay(250)
                    val (nextState, _) = engine.step(gameState, LudoAction.SelectMove(legalMoves.first().tokenId))
                    gameState = nextState
                    legalMoves = emptyList()
                } else {
                    statusMessage = "Tap a highlighted token to move!"
                }
            } else if (gameState.phase == LudoTurnPhase.WAITING_FOR_ROLL) {
                statusMessage = "Your turn! Tap the dice to roll."
                legalMoves = emptyList()
            }
        }
    }

    // Function to handle human roll
    fun onHumanRoll() {
        if (activePlayer.player.isAi || gameState.phase != LudoTurnPhase.WAITING_FOR_ROLL || isRollingAnimation) return

        coroutineScope.launch {
            isRollingAnimation = true
            repeat(5) {
                displayedDiceValue = Random.nextInt(1, 7)
                delay(60)
            }
            isRollingAnimation = false

            val (nextState, events) = engine.step(gameState, LudoAction.RollDice())
            gameState = nextState
            displayedDiceValue = nextState.currentRoll ?: 1
        }
    }

    // Function to handle human token selection
    fun onTokenTapped(tokenId: Int) {
        if (activePlayer.player.isAi || gameState.phase != LudoTurnPhase.WAITING_FOR_MOVE) return
        if (selectableTokenIds.contains(tokenId)) {
            val (nextState, _) = engine.step(gameState, LudoAction.SelectMove(tokenId))
            gameState = nextState
            legalMoves = emptyList()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LudoraTheme.colors.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Bar: Back Button, Player Badge, Round info
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
                        text = "Round ${gameState.roundCount}",
                        style = LudoraTheme.typography.bodyMedium,
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }

            TurnStatusPill(
                text = if (activePlayer.player.isAi) "AI Thinking..." else "Your Turn",
                activeColor = activePlayer.color
            )
        }

        // 15x15 Canvas Board
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(gameState, selectableTokenIds) {
                        detectTapGestures { offset ->
                            val boardSizePx = size.width.toFloat()
                            val cellSize = boardSizePx / LudoGridCoordinateMapper.GRID_SIZE

                            // Check active player selectable tokens
                            for (t in activePlayer.tokens) {
                                if (!selectableTokenIds.contains(t.id)) continue

                                val tokenOffset = when (val p = t.position) {
                                    is LudoPosition.InBase -> {
                                        val cell = LudoGridCoordinateMapper.getBaseSlotGridCell(activePlayer.color, t.id)
                                        LudoGridCoordinateMapper.cellCenterOffset(cell.first, cell.second, boardSizePx)
                                    }
                                    is LudoPosition.OnTrack -> {
                                        val cell = LudoGridCoordinateMapper.getTrackGridCell(p.stepIndex)
                                        LudoGridCoordinateMapper.cellCenterOffset(cell.first, cell.second, boardSizePx)
                                    }
                                    is LudoPosition.InHomePath -> {
                                        val cell = LudoGridCoordinateMapper.getHomePathGridCell(activePlayer.color, p.stepIndex)
                                        LudoGridCoordinateMapper.cellCenterOffset(cell.first, cell.second, boardSizePx)
                                    }
                                    is LudoPosition.Finished -> null
                                }

                                if (tokenOffset != null) {
                                    val dist = (offset - tokenOffset).getDistance()
                                    if (dist <= cellSize * 0.75f) {
                                        onTokenTapped(t.id)
                                        break
                                    }
                                }
                            }
                        }
                    }
            ) {
                val boardWidth = size.width
                val cellSize = boardWidth / LudoGridCoordinateMapper.GRID_SIZE

                // 1. Board Background
                drawRect(color = SlateSurface)

                // 2. Draw 4 Home Base Quadrants (6x6 cells each)
                drawBaseQuadrant(PlayerColor.RED, 0f, 0f, cellSize * 6)
                drawBaseQuadrant(PlayerColor.GREEN, cellSize * 9, 0f, cellSize * 6)
                drawBaseQuadrant(PlayerColor.YELLOW, cellSize * 9, cellSize * 9, cellSize * 6)
                drawBaseQuadrant(PlayerColor.BLUE, 0f, cellSize * 9, cellSize * 6)

                // 3. Draw Track Grid & Safe squares
                for (step in 0..51) {
                    val (col, row) = LudoGridCoordinateMapper.getTrackGridCell(step)
                    val cellLeft = col * cellSize
                    val cellTop = row * cellSize
                    val isSafe = LudoBoard.isSafeSquare(step)

                    drawRoundRect(
                        color = if (isSafe) SlateCard else Color(0xFF1E293B),
                        topLeft = Offset(cellLeft + 1f, cellTop + 1f),
                        size = Size(cellSize - 2f, cellSize - 2f),
                        cornerRadius = CornerRadius(3f, 3f)
                    )

                    if (isSafe) {
                        drawCircle(
                            color = WarmAmberGold.copy(alpha = 0.6f),
                            radius = cellSize * 0.22f,
                            center = Offset(cellLeft + cellSize / 2f, cellTop + cellSize / 2f)
                        )
                    }
                }

                // 4. Draw Colored Home Columns
                listOf(PlayerColor.RED, PlayerColor.GREEN, PlayerColor.YELLOW, PlayerColor.BLUE).forEach { color ->
                    for (step in 1..5) {
                        val (col, row) = LudoGridCoordinateMapper.getHomePathGridCell(color, step)
                        drawRoundRect(
                            color = color.toColor().copy(alpha = 0.85f),
                            topLeft = Offset(col * cellSize + 1f, row * cellSize + 1f),
                            size = Size(cellSize - 2f, cellSize - 2f),
                            cornerRadius = CornerRadius(3f, 3f)
                        )
                    }
                }

                // 5. Draw Center Triangular Goal (3x3 center area)
                drawCenterTriangles(cellSize)

                // 6. Draw Tokens for all players
                gameState.players.forEach { pState ->
                    pState.tokens.forEach { token ->
                        val centerOffset = when (val pos = token.position) {
                            is LudoPosition.InBase -> {
                                val cell = LudoGridCoordinateMapper.getBaseSlotGridCell(pState.color, token.id)
                                LudoGridCoordinateMapper.cellCenterOffset(cell.first, cell.second, boardWidth)
                            }
                            is LudoPosition.OnTrack -> {
                                val cell = LudoGridCoordinateMapper.getTrackGridCell(pos.stepIndex)
                                LudoGridCoordinateMapper.cellCenterOffset(cell.first, cell.second, boardWidth)
                            }
                            is LudoPosition.InHomePath -> {
                                val cell = LudoGridCoordinateMapper.getHomePathGridCell(pState.color, pos.stepIndex)
                                LudoGridCoordinateMapper.cellCenterOffset(cell.first, cell.second, boardWidth)
                            }
                            is LudoPosition.Finished -> {
                                val centerCell = LudoGridCoordinateMapper.getCenterGoalGridCell()
                                val baseCenter = LudoGridCoordinateMapper.cellCenterOffset(centerCell.first, centerCell.second, boardWidth)
                                baseCenter + Offset((token.id - 1.5f) * (cellSize * 0.25f), 0f)
                            }
                        }

                        val isSelectable = pState.seatIndex == gameState.activeSeatIndex &&
                                selectableTokenIds.contains(token.id)

                        TokenRenderer.drawToken(
                            drawScope = this,
                            center = centerOffset,
                            radius = cellSize * 0.42f,
                            color = pState.color,
                            isSelectable = isSelectable,
                            isLifted = isSelectable
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Status message pill
        Text(
            text = statusMessage,
            style = LudoraTheme.typography.bodyMedium,
            color = WarmAmberGold,
            modifier = Modifier.padding(vertical = 4.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Bottom Controls: Interactive Tactile 3D Die & Roll Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Tactile 3D Die
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .pointerInput(Unit) {
                        detectTapGestures {
                            onHumanRoll()
                        }
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
                enabled = !activePlayer.player.isAi && gameState.phase == LudoTurnPhase.WAITING_FOR_ROLL && !isRollingAnimation,
                modifier = Modifier.height(52.dp)
            )
        }
    }

    // Match Completed Victory Dialog
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
                        text = "🏆 Match Finished!",
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
                                onMatchFinished(placement, 2, isWin, 3)
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

// Helpers for drawing board sections
private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawBaseQuadrant(
    color: PlayerColor,
    x: Float,
    y: Float,
    quadrantSize: Float
) {
    drawRoundRect(
        color = color.toDarkColor().copy(alpha = 0.9f),
        topLeft = Offset(x, y),
        size = Size(quadrantSize, quadrantSize),
        cornerRadius = CornerRadius(12f, 12f)
    )

    // Inner white disc
    drawCircle(
        color = Color.White.copy(alpha = 0.9f),
        radius = quadrantSize * 0.35f,
        center = Offset(x + quadrantSize / 2f, y + quadrantSize / 2f)
    )

    // Quadrant border stroke
    drawRoundRect(
        color = color.toColor(),
        topLeft = Offset(x + 2f, y + 2f),
        size = Size(quadrantSize - 4f, quadrantSize - 4f),
        cornerRadius = CornerRadius(10f, 10f),
        style = Stroke(width = 3f)
    )
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawCenterTriangles(cellSize: Float) {
    val centerLeft = 6 * cellSize
    val centerTop = 6 * cellSize
    val centerRight = 9 * cellSize
    val centerBottom = 9 * cellSize
    val centerPoint = Offset(7.5f * cellSize, 7.5f * cellSize)

    // Red Left Triangle
    val redPath = Path().apply {
        moveTo(centerLeft, centerTop)
        lineTo(centerPoint.x, centerPoint.y)
        lineTo(centerLeft, centerBottom)
        close()
    }
    drawPath(redPath, PlayerColor.RED.toColor().copy(alpha = 0.9f))

    // Green Top Triangle
    val greenPath = Path().apply {
        moveTo(centerLeft, centerTop)
        lineTo(centerRight, centerTop)
        lineTo(centerPoint.x, centerPoint.y)
        close()
    }
    drawPath(greenPath, PlayerColor.GREEN.toColor().copy(alpha = 0.9f))

    // Yellow Right Triangle
    val yellowPath = Path().apply {
        moveTo(centerRight, centerTop)
        lineTo(centerRight, centerBottom)
        lineTo(centerPoint.x, centerPoint.y)
        close()
    }
    drawPath(yellowPath, PlayerColor.YELLOW.toColor().copy(alpha = 0.9f))

    // Blue Bottom Triangle
    val bluePath = Path().apply {
        moveTo(centerLeft, centerBottom)
        lineTo(centerPoint.x, centerPoint.y)
        lineTo(centerRight, centerBottom)
        close()
    }
    drawPath(bluePath, PlayerColor.BLUE.toColor().copy(alpha = 0.9f))
}
