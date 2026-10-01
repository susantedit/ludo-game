package game.ludora.engine.ai

import game.ludora.engine.ai.model.AiDifficulty
import game.ludora.engine.ludo.logic.LegalMove
import game.ludora.engine.ludo.logic.LudoMoveValidator
import game.ludora.engine.ludo.model.LudoBoard
import game.ludora.engine.ludo.model.LudoGameState
import game.ludora.engine.ludo.model.LudoPosition
import kotlin.random.Random

/**
 * Intelligent AI player agent implementing Easy, Medium, and Hard heuristic strategies.
 */
object LudoAiAgent {

    fun selectMove(
        state: LudoGameState,
        rollValue: Int,
        difficulty: AiDifficulty = AiDifficulty.MEDIUM,
        random: Random = Random
    ): LegalMove? {
        val legalMoves = LudoMoveValidator.getLegalMoves(state, rollValue)
        if (legalMoves.isEmpty()) return null
        if (legalMoves.size == 1) return legalMoves.first()

        return when (difficulty) {
            AiDifficulty.EASY -> legalMoves.random(random)
            AiDifficulty.MEDIUM -> selectMediumMove(legalMoves, random)
            AiDifficulty.HARD -> selectHardMove(state, legalMoves, random)
        }
    }

    private fun selectMediumMove(moves: List<LegalMove>, random: Random): LegalMove {
        // 1. Capture moves
        val captureMoves = moves.filter { it.isCapture }
        if (captureMoves.isNotEmpty()) return captureMoves.random(random)

        // 2. Base release moves
        val releaseMoves = moves.filter { it.from is LudoPosition.InBase }
        if (releaseMoves.isNotEmpty()) return releaseMoves.random(random)

        // 3. Move token nearest to home
        val finishMoves = moves.filter { it.to is LudoPosition.Finished }
        if (finishMoves.isNotEmpty()) return finishMoves.first()

        // Default: pick move with largest forward step
        return moves.maxByOrNull { calculateMoveProgress(it) } ?: moves.first()
    }

    private fun selectHardMove(
        state: LudoGameState,
        moves: List<LegalMove>,
        random: Random
    ): LegalMove {
        val scoredMoves = moves.map { move ->
            move to evaluateMoveScore(state, move)
        }

        val maxScore = scoredMoves.maxOf { it.second }
        val bestMoves = scoredMoves.filter { it.second == maxScore }.map { it.first }
        return bestMoves.random(random)
    }

    fun evaluateMoveScore(state: LudoGameState, move: LegalMove): Int {
        var score = 0
        val activePlayer = state.activePlayer

        // 1. Capture priority (+250)
        if (move.isCapture) {
            score += 250
        }

        // 2. Reaching goal / finishing token (+200)
        if (move.to is LudoPosition.Finished) {
            score += 200
        }

        // 3. Releasing token from base on a 6 (+120)
        if (move.from is LudoPosition.InBase) {
            score += 120
        }

        // 4. Entering safe home column (+90)
        if (move.to is LudoPosition.InHomePath && move.from !is LudoPosition.InHomePath) {
            score += 90
        }

        // 5. Landing on a safe star / globe square (+60)
        if (move.to is LudoPosition.OnTrack && LudoBoard.isSafeSquare((move.to as LudoPosition.OnTrack).stepIndex)) {
            score += 60
        }

        // 6. Escape from danger bonus (+75)
        if (move.from is LudoPosition.OnTrack) {
            val fromIndex = (move.from as LudoPosition.OnTrack).stepIndex
            if (!LudoBoard.isSafeSquare(fromIndex) && isSquareThreatened(state, fromIndex)) {
                score += 75
            }
        }

        // 7. Penalty for landing on an unprotected, threatened square (-150)
        if (move.to is LudoPosition.OnTrack) {
            val toIndex = (move.to as LudoPosition.OnTrack).stepIndex
            if (!LudoBoard.isSafeSquare(toIndex) && isSquareThreatened(state, toIndex)) {
                score -= 150
            }
        }

        // 8. General forward progression score
        score += calculateMoveProgress(move) * 2

        return score
    }

    private fun isSquareThreatened(state: LudoGameState, stepIndex: Int): Boolean {
        val opponents = state.players.filter { it.seatIndex != state.activeSeatIndex }
        for (opp in opponents) {
            for (token in opp.tokens) {
                if (token.position is LudoPosition.OnTrack) {
                    val oppPos = (token.position as LudoPosition.OnTrack).stepIndex
                    val distance = (stepIndex - oppPos + LudoBoard.TRACK_SIZE) % LudoBoard.TRACK_SIZE
                    if (distance in 1..6) {
                        return true
                    }
                }
            }
        }
        return false
    }

    private fun calculateMoveProgress(move: LegalMove): Int = when (val to = move.to) {
        is LudoPosition.InBase -> 0
        is LudoPosition.OnTrack -> to.stepIndex
        is LudoPosition.InHomePath -> 52 + to.stepIndex
        is LudoPosition.Finished -> 60
    }
}
