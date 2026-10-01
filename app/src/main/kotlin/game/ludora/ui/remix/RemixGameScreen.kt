package game.ludora.ui.remix

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import game.ludora.core.designsystem.canvas.DiceRenderer
import game.ludora.core.designsystem.canvas.LudoGridCoordinateMapper
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
import game.ludora.core.designsystem.theme.toColor
import game.ludora.core.model.PlayerColor
import game.ludora.engine.ai.LudoAiAgent
import game.ludora.engine.ludo.logic.LegalMove
import game.ludora.engine.ludo.logic.LudoMoveValidator
import game.ludora.engine.ludo.model.LudoAction
import game.ludora.engine.ludo.model.LudoBoard
import game.ludora.engine.ludo.model.LudoPosition
import game.ludora.engine.ludo.model.LudoTurnPhase
import game.ludora.engine.remix.RemixGameEngine
import game.ludora.engine.remix.RemixGameState
import game.ludora.engine.remix.model.ChaosModifier
import game.ludora.engine.remix.model.HazardType
import game.ludora.engine.remix.model.PowerUpType
import game.ludora.engine.remix.model.RemixConfig
import game.ludora.engine.remix.model.RemixHazard
import game.ludora.ui.common.MatchOptions
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

@Composable
fun RemixGameScreen(
    options: MatchOptions,
    onBackToMenu: () -> Unit
) {
    val engine = remember { RemixGameEngine() }

    var gameState by remember {
        val baseRemix = engine.getInitialState(
            playerCount = options.playerCount,
            config = RemixConfig(tokenCountPerPlayer = 2, enableHazards = true, enableChaosModifiers = true)
        )
        val setupPlayers = baseRemix.baseState.players.mapIndexed { idx, pState ->
            val isAi = if (idx == 0) false else options.isVsAi
            pState.copy(
                player = pState.player.copy(
                    isAi = isAi,
                    displayName = if (idx == 0) "You" else if (isAi) "AI ${pState.color.name}" else "Player ${idx + 1}"
                )
            )
        }
        val fullInventories = baseRemix.baseState.players.associate {
            it.color to listOf(
                PowerUpType.SHIELD,
                PowerUpType.SPEED_BOOST,
                PowerUpType.REROLL,
                PowerUpType.SWAP,
                PowerUpType.BOMB
            )
        }
        mutableStateOf(
            baseRemix.copy(
                baseState = baseRemix.baseState.copy(players = setupPlayers),
                inventories = fullInventories
            )
        )
    }

    var displayedDiceValue by remember { mutableIntStateOf(6) }
    var statusMessage by remember { mutableStateOf("Remix Mode: Hybrid Hazards & Power Cards Active!") }
    var legalMoves by remember { mutableStateOf<List<LegalMove>>(emptyList()) }
    var isRollingAnimation by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val activePlayer = gameState.baseState.activePlayer
    val humanInventory = gameState.inventories[PlayerColor.RED] ?: emptyList()

    val selectableTokenIds = remember(gameState, legalMoves) {
        if (gameState.baseState.phase == LudoTurnPhase.WAITING_FOR_MOVE) {
            legalMoves.map { it.tokenId }.toSet()
        } else {
            emptySet()
        }
    }

    // AI Turn Controller
    LaunchedEffect(gameState.baseState.activeSeatIndex, gameState.baseState.phase, gameState.isGameOver) {
        if (gameState.isGameOver) return@LaunchedEffect

        if (activePlayer.player.isAi) {
            delay(500)
            if (gameState.baseState.phase == LudoTurnPhase.WAITING_FOR_ROLL) {
                isRollingAnimation = true
                repeat(4) {
                    displayedDiceValue = Random.nextInt(1, 7)
                    delay(70)
                }
                isRollingAnimation = false

                val nextState = engine.step(gameState, LudoAction.RollDice())
                gameState = nextState
                displayedDiceValue = nextState.baseState.currentRoll ?: 1

                if (nextState.baseState.phase == LudoTurnPhase.WAITING_FOR_MOVE) {
                    val roll = nextState.baseState.currentRoll ?: 1
                    val chosenMove = LudoAiAgent.selectMove(
                        state = nextState.baseState,
                        rollValue = roll,
                        difficulty = options.aiDifficulty
                    )
                    delay(400)
                    if (chosenMove != null) {
                        val moveState = engine.step(nextState, LudoAction.SelectMove(chosenMove.tokenId))
                        gameState = moveState
                        legalMoves = emptyList()
                        moveState.lastTriggeredHazardDescription?.let { statusMessage = it }
                    }
                }
            }
        } else {
            if (gameState.baseState.phase == LudoTurnPhase.WAITING_FOR_MOVE) {
                val roll = gameState.baseState.currentRoll ?: 1
                legalMoves = LudoMoveValidator.getLegalMoves(gameState.baseState, roll)
                if (legalMoves.isEmpty()) {
                    statusMessage = "No moves. Turn passed."
                } else if (legalMoves.size == 1) {
                    delay(250)
                    val nextState = engine.step(gameState, LudoAction.SelectMove(legalMoves.first().tokenId))
                    gameState = nextState
                    legalMoves = emptyList()
                    nextState.lastTriggeredHazardDescription?.let { statusMessage = it }
                } else {
                    statusMessage = "Tap your highlighted token to move!"
                }
            } else if (gameState.baseState.phase == LudoTurnPhase.WAITING_FOR_ROLL) {
                statusMessage = "Your turn! Tap dice or activate a power card."
                legalMoves = emptyList()
            }
        }
    }

    fun onHumanRoll() {
        if (activePlayer.player.isAi || gameState.baseState.phase != LudoTurnPhase.WAITING_FOR_ROLL || isRollingAnimation) return

        coroutineScope.launch {
            isRollingAnimation = true
            repeat(5) {
                displayedDiceValue = Random.nextInt(1, 7)
                delay(60)
            }
            isRollingAnimation = false

            val nextState = engine.step(gameState, LudoAction.RollDice())
            gameState = nextState
            displayedDiceValue = nextState.baseState.currentRoll ?: 1
        }
    }

    fun onUsePowerUp(powerUp: PowerUpType) {
        if (activePlayer.player.isAi) return
        val next = engine.activatePowerUp(
            state = gameState,
            playerColor = activePlayer.color,
            powerUp = powerUp,
            targetTokenId = 0
        )
        gameState = next
        statusMessage = "Activated ${powerUp.name}! ⚡"
    }

    fun onTokenTapped(tokenId: Int) {
        if (activePlayer.player.isAi || gameState.baseState.phase != LudoTurnPhase.WAITING_FOR_MOVE) return
        if (selectableTokenIds.contains(tokenId)) {
            val nextState = engine.step(gameState, LudoAction.SelectMove(tokenId))
            gameState = nextState
            legalMoves = emptyList()
            nextState.lastTriggeredHazardDescription?.let { statusMessage = it }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LudoraTheme.colors.background)
            .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            LudoraSecondaryButton(
                text = "← Exit",
                onClick = onBackToMenu,
                modifier = Modifier.height(34.dp)
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                PlayerAvatarBadge(
                    color = activePlayer.color,
                    isCurrentTurn = true,
                    size = 30.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = activePlayer.player.displayName,
                        style = LudoraTheme.typography.titleSmall,
                        color = TextPrimary
                    )
                    Text(
                        text = "Round ${gameState.baseState.roundCount}",
                        style = LudoraTheme.typography.bodyMedium,
                        color = WarmAmberGold,
                        fontSize = 11.sp
                    )
                }
            }

            TurnStatusPill(
                text = if (activePlayer.player.isAi) "AI Thinking..." else "Your Turn",
                activeColor = activePlayer.color
            )
        }

        // Active Chaos Modifier Pill
        if (gameState.activeChaosModifier != ChaosModifier.NONE) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF7C3AED).copy(alpha = 0.25f))
                    .border(1.dp, Color(0xFF8B5CF6), RoundedCornerShape(6.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🌀 ${gameState.activeChaosModifier.displayName}: ${gameState.activeChaosModifier.description}",
                    color = Color(0xFFDDD6FE),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Power Cards Bar (Shield, Speed Boost, Reroll, Swap, Bomb)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            listOf(
                PowerUpType.SHIELD to "🛡️ Shield",
                PowerUpType.SPEED_BOOST to "⚡ +2 Roll",
                PowerUpType.REROLL to "🎲 Reroll",
                PowerUpType.SWAP to "🔄 Swap",
                PowerUpType.BOMB to "💣 Bomb"
            ).forEach { (pType, label) ->
                val hasItem = humanInventory.contains(pType)
                val isUsable = hasItem && !activePlayer.player.isAi && (
                    if (pType == PowerUpType.REROLL) gameState.baseState.phase == LudoTurnPhase.WAITING_FOR_MOVE
                    else gameState.baseState.phase == LudoTurnPhase.WAITING_FOR_ROLL
                )

                LudoraSecondaryButton(
                    text = label,
                    onClick = { onUsePowerUp(pType) },
                    enabled = isUsable,
                    modifier = Modifier
                        .weight(1f)
                        .height(34.dp)
                )
            }
        }

        // 15x15 Canvas Board with Track Ladders and Snakes
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
                                if (tokenOffset != null && (offset - tokenOffset).getDistance() <= cellSize * 0.75f) {
                                    onTokenTapped(t.id)
                                    break
                                }
                            }
                        }
                    }
            ) {
                val boardWidth = size.width
                val cellSize = boardWidth / LudoGridCoordinateMapper.GRID_SIZE

                drawRect(color = SlateSurface)

                // 1. Track tiles
                for (step in 0..51) {
                    val (col, row) = LudoGridCoordinateMapper.getTrackGridCell(step)
                    val cellLeft = col * cellSize
                    val cellTop = row * cellSize
                    val isSafe = LudoBoard.isSafeSquare(step)
                    val hazard = RemixHazard.getHazardAt(step)

                    val tileBg = when {
                        hazard?.type == HazardType.LADDER -> Color(0xFF14532D) // Dark Emerald Ladder Tile
                        hazard?.type == HazardType.SNAKE -> Color(0xFF7F1D1D)  // Dark Crimson Snake Tile
                        isSafe -> SlateCard
                        else -> Color(0xFF1E293B)
                    }

                    drawRoundRect(
                        color = tileBg,
                        topLeft = Offset(cellLeft + 1f, cellTop + 1f),
                        size = Size(cellSize - 2f, cellSize - 2f),
                        cornerRadius = CornerRadius(3f, 3f)
                    )

                    // Draw Hazard Markers
                    if (hazard?.type == HazardType.LADDER) {
                        // Golden ladder rungs indicator
                        drawCircle(
                            color = WarmAmberGold,
                            radius = cellSize * 0.22f,
                            center = Offset(cellLeft + cellSize / 2f, cellTop + cellSize / 2f),
                            style = Stroke(width = 2f)
                        )
                    } else if (hazard?.type == HazardType.SNAKE) {
                        // Red serpent dot indicator
                        drawCircle(
                            color = Color(0xFFEF4444),
                            radius = cellSize * 0.22f,
                            center = Offset(cellLeft + cellSize / 2f, cellTop + cellSize / 2f)
                        )
                    }
                }

                // 2. Colored home columns
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

                // 3. Render active tokens
                gameState.baseState.players.forEach { pState ->
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
                                LudoGridCoordinateMapper.cellCenterOffset(centerCell.first, centerCell.second, boardWidth)
                            }
                        }

                        val isSelectable = pState.seatIndex == gameState.baseState.activeSeatIndex &&
                                selectableTokenIds.contains(token.id)

                        val isShielded = gameState.shieldedTokens.contains(pState.color to token.id)

                        TokenRenderer.drawToken(
                            drawScope = this,
                            center = centerOffset,
                            radius = cellSize * 0.42f,
                            color = pState.color,
                            isSelectable = isSelectable,
                            isLifted = isSelectable
                        )

                        // Render subtle shield aura if active
                        if (isShielded) {
                            drawCircle(
                                color = Color(0xFF38BDF8),
                                radius = cellSize * 0.50f,
                                center = centerOffset,
                                style = Stroke(width = 3f)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = statusMessage,
            style = LudoraTheme.typography.bodyMedium,
            color = WarmAmberGold,
            modifier = Modifier.padding(vertical = 2.dp)
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Tactile 3D Die & Roll Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .pointerInput(Unit) { detectTapGestures { onHumanRoll() } }
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

            Spacer(modifier = Modifier.width(18.dp))

            LudoraPrimaryButton(
                text = if (isRollingAnimation) "Rolling..." else "Roll Dice (${displayedDiceValue})",
                onClick = { onHumanRoll() },
                enabled = !activePlayer.player.isAi && gameState.baseState.phase == LudoTurnPhase.WAITING_FOR_ROLL && !isRollingAnimation,
                modifier = Modifier.height(48.dp)
            )
        }
    }

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
                        text = "⚡ Remix Champion!",
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
                        text = "Back to Menu",
                        onClick = onBackToMenu,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
