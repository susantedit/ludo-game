package game.ludora.core.network.match

import game.ludora.core.model.PlayerColor
import game.ludora.engine.ludo.LudoGameEngine
import game.ludora.engine.ludo.model.LudoAction
import game.ludora.engine.ludo.model.LudoGameState
import game.ludora.engine.ludo.model.LudoTurnPhase
import kotlinx.serialization.Serializable

@Serializable
data class MatchSnapshot(
    val sequenceId: Long,
    val roomId: String,
    val activeSeatIndex: Int,
    val turnDeadlineTimestamp: Long,
    val isGameOver: Boolean,
    val winners: List<PlayerColor>
)

/**
 * Authoritative online match coordinator enforcing turn timeouts, sequence counters,
 * disconnect grace periods, and full state catch-up synchronization.
 */
class OnlineMatchCoordinator(
    val roomId: String,
    val turnDurationSeconds: Int = 15,
    private val engine: LudoGameEngine = LudoGameEngine()
) {

    private var currentSequenceId = 1L
    private var gameState: LudoGameState = engine.getInitialState(playerCount = 2)
    private var turnDeadlineTimestamp: Long = System.currentTimeMillis() + (turnDurationSeconds * 1000L)
    private val disconnectedSeats = mutableMapOf<Int, Long>() // seatIndex -> disconnectTimestamp

    fun getSequenceId(): Long = currentSequenceId
    fun getGameState(): LudoGameState = gameState
    fun getActiveSeatIndex(): Int = gameState.activeSeatIndex
    fun getTurnDeadline(): Long = turnDeadlineTimestamp

    /**
     * Executes client roll action under authoritative verification.
     */
    fun processRollIntent(seatIndex: Int): Result<LudoGameState> {
        if (seatIndex != gameState.activeSeatIndex) {
            return Result.failure(IllegalStateException("Not player's turn (Active: ${gameState.activeSeatIndex}, Got: $seatIndex)"))
        }
        if (gameState.phase != LudoTurnPhase.WAITING_FOR_ROLL) {
            return Result.failure(IllegalStateException("Cannot roll during phase ${gameState.phase}"))
        }

        currentSequenceId++
        val (nextState, _) = engine.step(gameState, LudoAction.RollDice())
        gameState = nextState
        updateTurnDeadline()

        return Result.success(nextState)
    }

    /**
     * Executes client token move under authoritative verification.
     */
    fun processMoveIntent(seatIndex: Int, tokenId: Int): Result<LudoGameState> {
        if (seatIndex != gameState.activeSeatIndex) {
            return Result.failure(IllegalStateException("Not player's turn"))
        }
        if (gameState.phase != LudoTurnPhase.WAITING_FOR_MOVE) {
            return Result.failure(IllegalStateException("Cannot move during phase ${gameState.phase}"))
        }

        currentSequenceId++
        val (nextState, _) = engine.step(gameState, LudoAction.SelectMove(tokenId))
        gameState = nextState
        updateTurnDeadline()

        return Result.success(nextState)
    }

    /**
     * Triggered when turn timer expires. Automatically advances turn or passes.
     */
    fun handleTurnTimeout(): LudoGameState {
        currentSequenceId++
        val (nextState, _) = engine.step(gameState, LudoAction.PassTurn)
        gameState = nextState
        updateTurnDeadline()
        return gameState
    }

    /**
     * Registers player disconnection and starts 60-second grace timer.
     */
    fun onPlayerDisconnected(seatIndex: Int) {
        disconnectedSeats[seatIndex] = System.currentTimeMillis()
    }

    /**
     * Handles player reconnection, verifying grace period and returning full synchronization snapshot.
     */
    fun onPlayerReconnected(seatIndex: Int, currentTimeMs: Long = System.currentTimeMillis()): Result<MatchSnapshot> {
        val disconnectTime = disconnectedSeats.remove(seatIndex)
        if (disconnectTime != null && (currentTimeMs - disconnectTime) > 60_000L) {
            return Result.failure(IllegalStateException("Grace period expired (60s)"))
        }

        val snapshot = MatchSnapshot(
            sequenceId = currentSequenceId,
            roomId = roomId,
            activeSeatIndex = gameState.activeSeatIndex,
            turnDeadlineTimestamp = turnDeadlineTimestamp,
            isGameOver = gameState.isGameOver,
            winners = gameState.winners
        )
        return Result.success(snapshot)
    }

    private fun updateTurnDeadline() {
        turnDeadlineTimestamp = System.currentTimeMillis() + (turnDurationSeconds * 1000L)
    }
}
