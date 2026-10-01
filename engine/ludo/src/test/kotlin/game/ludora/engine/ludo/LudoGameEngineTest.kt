package game.ludora.engine.ludo

import game.ludora.core.model.Player
import game.ludora.core.model.PlayerColor
import game.ludora.engine.core.DiceRoller
import game.ludora.engine.core.EngineAction
import game.ludora.engine.ludo.logic.LegalMove
import game.ludora.engine.ludo.model.LudoAction
import game.ludora.engine.ludo.model.LudoEvent
import game.ludora.engine.ludo.model.LudoGameState
import game.ludora.engine.ludo.model.LudoPlayerState
import game.ludora.engine.ludo.model.LudoPosition
import game.ludora.engine.ludo.model.LudoTurnPhase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class LudoGameEngineTest {

    class FixedDiceRoller(private val rolls: List<Int>) : DiceRoller {
        private var index = 0
        override fun roll(): Int {
            val r = rolls[index % rolls.size]
            index++
            return r
        }
    }

    @Test
    fun testInitialStateCreation() {
        val engine = LudoGameEngine()
        assertEquals("LUDO_CLASSIC_V1", engine.rulesetId)

        val state2P = engine.getInitialState(playerCount = 2)
        assertEquals(2, state2P.players.size)
        assertEquals(PlayerColor.RED, state2P.players[0].color)
        assertEquals(PlayerColor.YELLOW, state2P.players[1].color) // Opposing seats
        assertEquals(0, state2P.activeSeatIndex)
        assertEquals(LudoTurnPhase.WAITING_FOR_ROLL, state2P.phase)

        val state4P = engine.getInitialState(playerCount = 4)
        assertEquals(4, state4P.players.size)
    }

    @Test
    fun testEngineContractReduce() {
        val engine = LudoGameEngine()
        val state = engine.getInitialState(playerCount = 2)

        // 1. Roll 6
        val result1 = engine.reduce(state, EngineAction.RollDice(seatIndex = 0), rollValue = 6)
        assertEquals(LudoTurnPhase.WAITING_FOR_MOVE, result1.state.phase)
        assertEquals(6, result1.state.currentRoll)

        // 2. Select Token "0"
        val result2 = engine.reduce(result1.state, EngineAction.SelectToken(seatIndex = 0, tokenId = "0"))
        assertEquals(LudoPosition.OnTrack(0), result2.state.activePlayer.tokens[0].position)
        assertEquals(LudoTurnPhase.WAITING_FOR_ROLL, result2.state.phase) // Earned bonus roll
    }

    @Test
    fun testDeterministicCannedGameFlow() {
        // Red rolls 6, moves token 0 to step 0
        // Red rolls 4, moves token 0 to step 4
        // Red rolls 2, moves token 0 to step 6
        val roller = FixedDiceRoller(listOf(6, 4, 2))
        val engine = LudoGameEngine(diceRoller = roller)
        val state0 = engine.getInitialState(playerCount = 2)

        // Turn 1.1: roll 6
        val (state1, _) = engine.step(state0, LudoAction.RollDice())
        assertEquals(6, state1.currentRoll)
        val (state2, _) = engine.step(state1, LudoAction.SelectMove(0))
        assertEquals(LudoPosition.OnTrack(0), state2.activePlayer.tokens[0].position)

        // Turn 1.2: bonus roll 4
        val (state3, _) = engine.step(state2, LudoAction.RollDice())
        assertEquals(4, state3.currentRoll)
        val (state4, _) = engine.step(state3, LudoAction.SelectMove(0))
        assertEquals(LudoPosition.OnTrack(4), state4.activePlayer.tokens[0].position)

        // Turn 1.3: roll 2 (no bonus earned -> passes to Yellow seat 2)
        val (state5, _) = engine.step(state4, LudoAction.RollDice())
        assertEquals(2, state5.currentRoll)
        val (state6, _) = engine.step(state5, LudoAction.SelectMove(0))
        assertEquals(LudoPosition.OnTrack(6), state6.players.first { it.color == PlayerColor.RED }.tokens[0].position)
        assertEquals(2, state6.activeSeatIndex) // Turn passed to Yellow!
    }
}
