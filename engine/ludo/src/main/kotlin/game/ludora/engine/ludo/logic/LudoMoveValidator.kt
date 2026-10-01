package game.ludora.engine.ludo.logic

import game.ludora.engine.ludo.model.LudoBoard
import game.ludora.engine.ludo.model.LudoGameState
import game.ludora.engine.ludo.model.LudoPosition
import game.ludora.engine.ludo.model.LudoToken

/**
 * Encapsulates a valid move that a player can execute with a given token.
 *
 * @property tokenId Token index (0 to 3) being moved.
 * @property from Original board position before move execution.
 * @property to Destination board position after move execution.
 * @property isCapture True if this move captures an opponent token.
 * @property capturedOpponentToken The opponent token instance that will be sent back to base, or null.
 */
data class LegalMove(
    val tokenId: Int,
    val from: LudoPosition,
    val to: LudoPosition,
    val isCapture: Boolean = false,
    val capturedOpponentToken: LudoToken? = null
)

/**
 * Pure evaluator for legal Ludo moves based on authoritative rules in docs/04_GAME_RULES.md.
 */
object LudoMoveValidator {

    /**
     * Computes all legal moves available to the active player for the specified roll value.
     */
    fun getLegalMoves(state: LudoGameState, roll: Int): List<LegalMove> {
        if (state.isGameOver || roll !in 1..6) return emptyList()

        val activePlayer = state.activePlayer
        if (activePlayer.isFinished) return emptyList()

        val opponentTokens = state.players
            .filter { it.seatIndex != activePlayer.seatIndex }
            .flatMap { it.tokens }

        val legalMoves = mutableListOf<LegalMove>()

        for (token in activePlayer.tokens) {
            if (token.isFinished) continue

            val destination = LudoBoard.calculateNextPosition(
                current = token.position,
                roll = roll,
                color = activePlayer.color
            ) ?: continue

            // Evaluate destination constraints
            when (destination) {
                is LudoPosition.OnTrack -> {
                    val stepIndex = destination.stepIndex
                    val isSafe = LudoBoard.isSafeSquare(stepIndex)

                    // Friendly collision check: friendly tokens cannot share regular squares
                    val friendlyCollision = activePlayer.tokens.any { friendly ->
                        friendly.id != token.id && friendly.position == destination
                    }
                    if (friendlyCollision && !isSafe) {
                        // Prohibited by Section 10.1 in docs/04_GAME_RULES.md
                        continue
                    }

                    // Opponent capture check
                    val opponentTokenOnSquare = opponentTokens.firstOrNull { it.position == destination }
                    if (opponentTokenOnSquare != null && !isSafe) {
                        legalMoves.add(
                            LegalMove(
                                tokenId = token.id,
                                from = token.position,
                                to = destination,
                                isCapture = true,
                                capturedOpponentToken = opponentTokenOnSquare
                            )
                        )
                    } else {
                        legalMoves.add(
                            LegalMove(
                                tokenId = token.id,
                                from = token.position,
                                to = destination,
                                isCapture = false,
                                capturedOpponentToken = null
                            )
                        )
                    }
                }
                is LudoPosition.InHomePath,
                is LudoPosition.Finished -> {
                    legalMoves.add(
                        LegalMove(
                            tokenId = token.id,
                            from = token.position,
                            to = destination,
                            isCapture = false,
                            capturedOpponentToken = null
                        )
                    )
                }
                is LudoPosition.InBase -> {
                    // Cannot move into base
                    continue
                }
            }
        }

        return legalMoves
    }
}
