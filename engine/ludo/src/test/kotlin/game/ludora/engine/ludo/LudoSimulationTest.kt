package game.ludora.engine.ludo

import game.ludora.core.model.PlayerColor
import game.ludora.engine.core.SecureDiceRoller
import game.ludora.engine.ludo.logic.LudoMoveValidator
import game.ludora.engine.ludo.model.LudoAction
import game.ludora.engine.ludo.model.LudoTurnPhase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Random

class LudoSimulationTest {

    @Test
    fun testHeadlessSimulationsTerminateWithValidInvariants() {
        val random = Random(42) // Fixed seed for reproducible test run
        val engine = LudoGameEngine(diceRoller = SecureDiceRoller)

        // Run 25 complete simulated matches across 2-player, 3-player, and 4-player setups
        for (matchIndex in 1..25) {
            val playerCount = when (matchIndex % 3) {
                0 -> 2
                1 -> 3
                else -> 4
            }
            var state = engine.getInitialState(playerCount = playerCount, seed = matchIndex.toLong())

            var stepCount = 0
            val maxSteps = 50_000

            while (!state.isGameOver && stepCount < maxSteps) {
                stepCount++

                when (state.phase) {
                    LudoTurnPhase.WAITING_FOR_ROLL -> {
                        val (nextState, _) = engine.step(state, LudoAction.RollDice())
                        state = nextState
                    }
                    LudoTurnPhase.WAITING_FOR_MOVE -> {
                        val roll = state.currentRoll ?: 1
                        val legalMoves = LudoMoveValidator.getLegalMoves(state, roll)
                        if (legalMoves.isEmpty()) {
                            val (nextState, _) = engine.step(state, LudoAction.PassTurn)
                            state = nextState
                        } else {
                            // Pick random legal move
                            val chosenMove = legalMoves[random.nextInt(legalMoves.size)]
                            val (nextState, _) = engine.step(state, LudoAction.SelectMove(chosenMove.tokenId))
                            state = nextState
                        }
                    }
                    LudoTurnPhase.GAME_OVER -> break
                }

                // Invariant checks on every step
                for (p in state.players) {
                    assertEquals(4, p.tokens.size)
                }
            }

            assertTrue("Match $matchIndex should terminate within step limit", state.isGameOver)
            assertEquals(LudoTurnPhase.GAME_OVER, state.phase)
            assertTrue("Standings must not be empty", state.winners.isNotEmpty())
            assertEquals(playerCount, state.winners.size)

            // Verify unique rankings assigned
            val ranks = state.players.mapNotNull { it.rank }.toSet()
            assertEquals(playerCount, ranks.size)
        }
    }
}
