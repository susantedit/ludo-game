package game.ludora.engine.snake.model

import game.ludora.core.model.PlayerColor
import kotlinx.serialization.Serializable

@Serializable
enum class SnakeTurnPhase {
    WAITING_FOR_ROLL,
    GAME_OVER
}

@Serializable
data class SnakeGameState(
    val matchId: String,
    val players: List<SnakePlayerState>,
    val activeSeatIndex: Int,
    val currentRoll: Int? = null,
    val phase: SnakeTurnPhase = SnakeTurnPhase.WAITING_FOR_ROLL,
    val roundCount: Int = 1,
    val winners: List<PlayerColor> = emptyList(),
    val isGameOver: Boolean = false,
    val maxRounds: Int = 200
) {
    init {
        require(players.size in 2..4) { "Snake & Ladder match must have 2 to 4 players, found ${players.size}" }
    }

    val activePlayer: SnakePlayerState
        get() = players.first { it.seatIndex == activeSeatIndex }

    fun getPlayerByColor(color: PlayerColor): SnakePlayerState? =
        players.firstOrNull { it.color == color }

    fun getPlayerBySeat(seatIndex: Int): SnakePlayerState? =
        players.firstOrNull { it.seatIndex == seatIndex }
}
