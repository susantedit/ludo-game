package game.ludora.engine.snake.model

import kotlinx.serialization.Serializable

@Serializable
sealed interface SnakeAction {
    @Serializable
    data class RollDice(val forcedValue: Int? = null) : SnakeAction

    @Serializable
    data object PassTurn : SnakeAction
}
