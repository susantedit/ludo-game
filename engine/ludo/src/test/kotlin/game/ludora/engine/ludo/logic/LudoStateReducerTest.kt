package game.ludora.engine.ludo.logic

import game.ludora.core.model.Player
import game.ludora.core.model.PlayerColor
import game.ludora.engine.ludo.model.BonusRollReason
import game.ludora.engine.ludo.model.LudoAction
import game.ludora.engine.ludo.model.LudoEvent
import game.ludora.engine.ludo.model.LudoGameState
import game.ludora.engine.ludo.model.LudoPlayerState
import game.ludora.engine.ludo.model.LudoPosition
import game.ludora.engine.ludo.model.LudoToken
import game.ludora.engine.ludo.model.LudoTurnPhase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class LudoStateReducerTest {

    private fun createPlayer(seatIndex: Int, color: PlayerColor): Player =
        Player(
            id = "player_$seatIndex",
            displayName = "Player $color",
            color = color,
            seatIndex = seatIndex,
            isAi = false
        )

    private fun create2PlayerGame(): LudoGameState {
        val red = LudoPlayerState.initial(createPlayer(0, PlayerColor.RED))
        val yellow = LudoPlayerState.initial(createPlayer(2, PlayerColor.YELLOW))
        return LudoGameState(
            matchId = "match_test",
            players = listOf(red, yellow),
            activeSeatIndex = 0,
            phase = LudoTurnPhase.WAITING_FOR_ROLL
        )
    }

    @Test
    fun testRollSixAllowsTokenReleaseAndGrantsBonusRoll() {
        val initial = create2PlayerGame()

        // 1. Red rolls 6
        val (stateAfterRoll, eventsAfterRoll) = LudoStateReducer.reduce(initial, LudoAction.RollDice(forcedValue = 6))
        assertEquals(LudoTurnPhase.WAITING_FOR_MOVE, stateAfterRoll.phase)
        assertEquals(6, stateAfterRoll.currentRoll)
        assertTrue(eventsAfterRoll.any { it is LudoEvent.DiceRolled && it.value == 6 })

        // 2. Red selects token 0 to move (release)
        val (stateAfterMove, eventsAfterMove) = LudoStateReducer.reduce(stateAfterRoll, LudoAction.SelectMove(tokenId = 0))
        
        // Token 0 should now be on track at step 0
        val token0 = stateAfterMove.activePlayer.tokens[0]
        assertEquals(LudoPosition.OnTrack(0), token0.position)

        // Rolling 6 grants a bonus roll: phase returns to WAITING_FOR_ROLL for the same player (seat 0)
        assertEquals(LudoTurnPhase.WAITING_FOR_ROLL, stateAfterMove.phase)
        assertEquals(0, stateAfterMove.activeSeatIndex)
        assertTrue(eventsAfterMove.any { it is LudoEvent.TokenReleased && it.tokenId == 0 })
        assertTrue(eventsAfterMove.any { it is LudoEvent.BonusRollGranted && it.reason == BonusRollReason.ROLLED_SIX })
    }

    @Test
    fun testThreeConsecutiveSixesForfeitsTurn() {
        val initial = create2PlayerGame()

        // Roll 1: Six
        val (s1, _) = LudoStateReducer.reduce(initial, LudoAction.RollDice(6))
        val (s2, _) = LudoStateReducer.reduce(s1, LudoAction.SelectMove(0))
        assertEquals(0, s2.activeSeatIndex)
        assertEquals(1, s2.activePlayer.consecutiveSixes)

        // Roll 2: Six
        val (s3, _) = LudoStateReducer.reduce(s2, LudoAction.RollDice(6))
        val (s4, _) = LudoStateReducer.reduce(s3, LudoAction.SelectMove(0))
        assertEquals(0, s4.activeSeatIndex)
        assertEquals(2, s4.activePlayer.consecutiveSixes)

        // Roll 3: Six -> Penalty! Forfeits immediately, advances to Seat 2 (Yellow)
        val (s5, events5) = LudoStateReducer.reduce(s4, LudoAction.RollDice(6))
        assertEquals(2, s5.activeSeatIndex) // Yellow's turn now!
        assertEquals(LudoTurnPhase.WAITING_FOR_ROLL, s5.phase)
        assertEquals(0, s5.players.first { it.seatIndex == 0 }.consecutiveSixes)
        assertTrue(events5.any { it is LudoEvent.ThreeSixesPenalty })
        assertTrue(events5.any { it is LudoEvent.TurnPassed && it.nextSeatIndex == 2 })
    }

    @Test
    fun testNoLegalMovesAdvancesTurn() {
        val initial = create2PlayerGame()

        // Red rolls 3 with all tokens in base -> zero legal moves
        val (state, events) = LudoStateReducer.reduce(initial, LudoAction.RollDice(3))
        assertEquals(2, state.activeSeatIndex) // Passed to Yellow
        assertEquals(LudoTurnPhase.WAITING_FOR_ROLL, state.phase)
        assertNull(state.currentRoll)
        assertTrue(events.any { it is LudoEvent.NoLegalMoves && it.roll == 3 })
        assertTrue(events.any { it is LudoEvent.TurnPassed && it.nextSeatIndex == 2 })
    }

    @Test
    fun testCaptureSendsOpponentToBaseAndGrantsBonusRoll() {
        // Red token 0 at step 2. Yellow token 0 at step 5.
        val redTokens = listOf(
            LudoToken(0, PlayerColor.RED, LudoPosition.OnTrack(2)),
            LudoToken(1, PlayerColor.RED, LudoPosition.InBase(1)),
            LudoToken(2, PlayerColor.RED, LudoPosition.InBase(2)),
            LudoToken(3, PlayerColor.RED, LudoPosition.InBase(3))
        )
        val yellowTokens = listOf(
            LudoToken(0, PlayerColor.YELLOW, LudoPosition.OnTrack(5)),
            LudoToken(1, PlayerColor.YELLOW, LudoPosition.InBase(1)),
            LudoToken(2, PlayerColor.YELLOW, LudoPosition.InBase(2)),
            LudoToken(3, PlayerColor.YELLOW, LudoPosition.InBase(3))
        )
        val state = LudoGameState(
            matchId = "match_capture",
            players = listOf(
                LudoPlayerState(createPlayer(0, PlayerColor.RED), redTokens),
                LudoPlayerState(createPlayer(2, PlayerColor.YELLOW), yellowTokens)
            ),
            activeSeatIndex = 0,
            phase = LudoTurnPhase.WAITING_FOR_ROLL
        )

        // Red rolls 3
        val (stateAfterRoll, _) = LudoStateReducer.reduce(state, LudoAction.RollDice(3))
        // Red moves token 0 to step 5 -> Capture!
        val (stateAfterMove, events) = LudoStateReducer.reduce(stateAfterRoll, LudoAction.SelectMove(0))

        // Red token 0 at step 5
        assertEquals(LudoPosition.OnTrack(5), stateAfterMove.activePlayer.tokens[0].position)
        // Yellow token 0 returned to InBase(0)
        val yellowPlayer = stateAfterMove.players.first { it.color == PlayerColor.YELLOW }
        assertEquals(LudoPosition.InBase(0), yellowPlayer.tokens[0].position)

        // Capture grants bonus roll to Red (seat 0)
        assertEquals(0, stateAfterMove.activeSeatIndex)
        assertEquals(LudoTurnPhase.WAITING_FOR_ROLL, stateAfterMove.phase)
        assertTrue(events.any { it is LudoEvent.TokenCaptured && it.capturedColor == PlayerColor.YELLOW })
        assertTrue(events.any { it is LudoEvent.BonusRollGranted && it.reason == BonusRollReason.CAPTURED_OPPONENT })
    }

    @Test
    fun testTwoPlayerGameFinishesWhenFirstPlayerGetsFourTokensInGoal() {
        // Red has 3 tokens finished, 1 token at H5
        val redTokens = listOf(
            LudoToken(0, PlayerColor.RED, LudoPosition.InHomePath(5)),
            LudoToken(1, PlayerColor.RED, LudoPosition.Finished),
            LudoToken(2, PlayerColor.RED, LudoPosition.Finished),
            LudoToken(3, PlayerColor.RED, LudoPosition.Finished)
        )
        val yellowTokens = LudoPlayerState.initial(createPlayer(2, PlayerColor.YELLOW)).tokens
        val state = LudoGameState(
            matchId = "match_finish",
            players = listOf(
                LudoPlayerState(createPlayer(0, PlayerColor.RED), redTokens),
                LudoPlayerState(createPlayer(2, PlayerColor.YELLOW), yellowTokens)
            ),
            activeSeatIndex = 0,
            phase = LudoTurnPhase.WAITING_FOR_ROLL
        )

        // Red rolls 1 -> enters goal
        val (stateAfterRoll, _) = LudoStateReducer.reduce(state, LudoAction.RollDice(1))
        val (finalState, events) = LudoStateReducer.reduce(stateAfterRoll, LudoAction.SelectMove(0))

        assertTrue(finalState.isGameOver)
        assertEquals(LudoTurnPhase.GAME_OVER, finalState.phase)
        assertEquals(listOf(PlayerColor.RED, PlayerColor.YELLOW), finalState.winners)
        assertEquals(1, finalState.players.first { it.color == PlayerColor.RED }.rank)
        assertEquals(2, finalState.players.first { it.color == PlayerColor.YELLOW }.rank)
        assertTrue(events.any { it is LudoEvent.PlayerFinished && it.playerColor == PlayerColor.RED && it.rank == 1 })
        assertTrue(events.any { it is LudoEvent.MatchCompleted })
    }

    @Test
    fun testAntiStallCutoffEndsMatchAtRound200() {
        val red = LudoPlayerState.initial(createPlayer(0, PlayerColor.RED))
        val yellow = LudoPlayerState.initial(createPlayer(2, PlayerColor.YELLOW))
        val state = LudoGameState(
            matchId = "match_cutoff",
            players = listOf(red, yellow),
            activeSeatIndex = 2, // Yellow's turn at round 200
            roundCount = 200,
            phase = LudoTurnPhase.WAITING_FOR_ROLL,
            maxRounds = 200
        )

        // Yellow rolls 3 with tokens in base -> no legal moves, round advances to 201 -> cutoff!
        val (finalState, events) = LudoStateReducer.reduce(state, LudoAction.RollDice(3))
        assertTrue(finalState.isGameOver)
        assertEquals(LudoTurnPhase.GAME_OVER, finalState.phase)
        assertTrue(events.any { it is LudoEvent.MatchCompleted && it.reason == "TIME_LIMIT_REACHED" })
    }
}
