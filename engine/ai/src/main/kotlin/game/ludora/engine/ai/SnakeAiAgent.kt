package game.ludora.engine.ai

import game.ludora.engine.snake.model.SnakeAction
import game.ludora.engine.snake.model.SnakeGameState
import game.ludora.engine.snake.model.SnakeTurnPhase

/**
 * AI player agent for Snake and Ladder matches.
 */
object SnakeAiAgent {

    fun decideAction(state: SnakeGameState): SnakeAction? {
        if (state.isGameOver) return null
        val active = state.activePlayer
        if (!active.player.isAi) return null

        return when (state.phase) {
            SnakeTurnPhase.WAITING_FOR_ROLL -> SnakeAction.RollDice()
            SnakeTurnPhase.RESOLVING_ANIMATION -> null
            SnakeTurnPhase.GAME_OVER -> null
        }
    }
}
