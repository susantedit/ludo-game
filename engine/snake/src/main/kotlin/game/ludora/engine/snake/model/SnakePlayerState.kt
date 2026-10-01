package game.ludora.engine.snake.model

import game.ludora.core.model.Player
import game.ludora.core.model.PlayerColor
import kotlinx.serialization.Serializable

/**
 * State of a single participant in Snake & Ladder.
 */
@Serializable
data class SnakePlayerState(
    val player: Player,
    val currentSquare: Int = 0, // 0 = start, 1..100 = board squares
    val consecutiveSixes: Int = 0,
    val rank: Int? = null
) {
    val color: PlayerColor get() = player.color
    val seatIndex: Int get() = player.seatIndex
    val isFinished: Boolean get() = currentSquare == 100
    val distanceToFinish: Int get() = 100 - currentSquare

    companion object {
        fun initial(player: Player): SnakePlayerState =
            SnakePlayerState(
                player = player,
                currentSquare = 0,
                consecutiveSixes = 0,
                rank = null
            )
    }
}
