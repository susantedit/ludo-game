package game.ludora.engine.ludo.logic

import game.ludora.core.model.Player
import game.ludora.core.model.PlayerColor
import game.ludora.engine.ludo.model.LudoGameState
import game.ludora.engine.ludo.model.LudoPlayerState
import game.ludora.engine.ludo.model.LudoPosition
import game.ludora.engine.ludo.model.LudoToken
import game.ludora.engine.ludo.model.LudoTurnPhase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class LudoMoveValidatorTest {

    private fun createPlayer(seatIndex: Int, color: PlayerColor): Player =
        Player(
            id = "player_$seatIndex",
            displayName = "Player $color",
            color = color,
            seatIndex = seatIndex,
            isAi = false
        )

    private fun createInitialState(): LudoGameState {
        val redPlayer = LudoPlayerState.initial(createPlayer(0, PlayerColor.RED))
        val yellowPlayer = LudoPlayerState.initial(createPlayer(2, PlayerColor.YELLOW))
        return LudoGameState(
            matchId = "match_1",
            players = listOf(redPlayer, yellowPlayer),
            activeSeatIndex = 0,
            phase = LudoTurnPhase.WAITING_FOR_MOVE
        )
    }

    @Test
    fun testBaseReleaseRequiresSix() {
        val state = createInitialState()

        for (roll in 1..5) {
            val moves = LudoMoveValidator.getLegalMoves(state, roll)
            assertTrue("Roll $roll should produce 0 moves from base", moves.isEmpty())
        }

        val movesOnSix = LudoMoveValidator.getLegalMoves(state, 6)
        assertEquals(4, movesOnSix.size) // All 4 tokens can be released
        assertEquals(LudoPosition.OnTrack(0), movesOnSix[0].to)
    }

    @Test
    fun testFriendlyCollisionOnRegularSquareIsIllegal() {
        // Red token 0 at step 4. Red token 1 at step 2.
        val redTokens = listOf(
            LudoToken(0, PlayerColor.RED, LudoPosition.OnTrack(4)),
            LudoToken(1, PlayerColor.RED, LudoPosition.OnTrack(2)),
            LudoToken(2, PlayerColor.RED, LudoPosition.InBase(2)),
            LudoToken(3, PlayerColor.RED, LudoPosition.InBase(3))
        )
        val redPlayer = LudoPlayerState(createPlayer(0, PlayerColor.RED), redTokens)
        val yellowPlayer = LudoPlayerState.initial(createPlayer(2, PlayerColor.YELLOW))
        val state = LudoGameState("match_1", listOf(redPlayer, yellowPlayer), activeSeatIndex = 0)

        // Roll 2:
        // Token 0 at step 4 moves to 6 (legal)
        // Token 1 at step 2 moves to 4 (would land on friendly token 0 on regular square 4 -> ILLEGAL)
        // Tokens 2, 3 in base cannot move on 2.
        val moves = LudoMoveValidator.getLegalMoves(state, 2)
        assertEquals(1, moves.size)
        assertEquals(0, moves[0].tokenId)
        assertEquals(LudoPosition.OnTrack(6), moves[0].to)
    }

    @Test
    fun testFriendlyCoexistenceOnSafeSquareIsLegal() {
        // Red token 0 at step 8 (Safe Star square). Red token 1 at step 6.
        val redTokens = listOf(
            LudoToken(0, PlayerColor.RED, LudoPosition.OnTrack(8)),
            LudoToken(1, PlayerColor.RED, LudoPosition.OnTrack(6)),
            LudoToken(2, PlayerColor.RED, LudoPosition.InBase(2)),
            LudoToken(3, PlayerColor.RED, LudoPosition.InBase(3))
        )
        val redPlayer = LudoPlayerState(createPlayer(0, PlayerColor.RED), redTokens)
        val yellowPlayer = LudoPlayerState.initial(createPlayer(2, PlayerColor.YELLOW))
        val state = LudoGameState("match_1", listOf(redPlayer, yellowPlayer), activeSeatIndex = 0)

        // Roll 2:
        // Token 0 at step 8 moves to step 10
        // Token 1 at step 6 moves to step 8 (Safe square -> LEGAL coexistence)
        val moves = LudoMoveValidator.getLegalMoves(state, 2)
        assertEquals(2, moves.size)
        assertTrue(moves.any { it.tokenId == 1 && it.to == LudoPosition.OnTrack(8) })
    }

    @Test
    fun testOpponentCaptureOnRegularSquare() {
        // Red token 0 at step 2. Yellow token 0 at step 5 (regular square).
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
            matchId = "match_1",
            players = listOf(
                LudoPlayerState(createPlayer(0, PlayerColor.RED), redTokens),
                LudoPlayerState(createPlayer(2, PlayerColor.YELLOW), yellowTokens)
            ),
            activeSeatIndex = 0
        )

        // Red rolls 3 -> lands on step 5, capturing Yellow token 0
        val moves = LudoMoveValidator.getLegalMoves(state, 3)
        assertEquals(1, moves.size)
        val move = moves[0]
        assertEquals(0, move.tokenId)
        assertEquals(LudoPosition.OnTrack(5), move.to)
        assertTrue(move.isCapture)
        assertNotNull(move.capturedOpponentToken)
        assertEquals(PlayerColor.YELLOW, move.capturedOpponentToken?.color)
    }

    @Test
    fun testNoCaptureOnSafeSquare() {
        // Red token 0 at step 6. Yellow token 0 at step 8 (Safe Star square).
        val redTokens = listOf(
            LudoToken(0, PlayerColor.RED, LudoPosition.OnTrack(6)),
            LudoToken(1, PlayerColor.RED, LudoPosition.InBase(1)),
            LudoToken(2, PlayerColor.RED, LudoPosition.InBase(2)),
            LudoToken(3, PlayerColor.RED, LudoPosition.InBase(3))
        )
        val yellowTokens = listOf(
            LudoToken(0, PlayerColor.YELLOW, LudoPosition.OnTrack(8)),
            LudoToken(1, PlayerColor.YELLOW, LudoPosition.InBase(1)),
            LudoToken(2, PlayerColor.YELLOW, LudoPosition.InBase(2)),
            LudoToken(3, PlayerColor.YELLOW, LudoPosition.InBase(3))
        )
        val state = LudoGameState(
            matchId = "match_1",
            players = listOf(
                LudoPlayerState(createPlayer(0, PlayerColor.RED), redTokens),
                LudoPlayerState(createPlayer(2, PlayerColor.YELLOW), yellowTokens)
            ),
            activeSeatIndex = 0
        )

        // Red rolls 2 -> lands on step 8 (safe) -> NO capture, co-existence
        val moves = LudoMoveValidator.getLegalMoves(state, 2)
        assertEquals(1, moves.size)
        val move = moves[0]
        assertEquals(0, move.tokenId)
        assertEquals(LudoPosition.OnTrack(8), move.to)
        assertFalse(move.isCapture)
    }

    @Test
    fun testHomePathOvershootRejected() {
        val redTokens = listOf(
            LudoToken(0, PlayerColor.RED, LudoPosition.InHomePath(4)),
            LudoToken(1, PlayerColor.RED, LudoPosition.InBase(1)),
            LudoToken(2, PlayerColor.RED, LudoPosition.InBase(2)),
            LudoToken(3, PlayerColor.RED, LudoPosition.InBase(3))
        )
        val state = LudoGameState(
            matchId = "match_1",
            players = listOf(
                LudoPlayerState(createPlayer(0, PlayerColor.RED), redTokens),
                LudoPlayerState.initial(createPlayer(2, PlayerColor.YELLOW))
            ),
            activeSeatIndex = 0
        )

        // Roll 3: from H4 + 3 = 7 > 6 (overshoot) -> 0 moves
        val moves = LudoMoveValidator.getLegalMoves(state, 3)
        assertTrue(moves.isEmpty())

        // Roll 2: from H4 + 2 = 6 -> Finished (legal)
        val movesOnTwo = LudoMoveValidator.getLegalMoves(state, 2)
        assertEquals(1, movesOnTwo.size)
        assertEquals(LudoPosition.Finished, movesOnTwo[0].to)
    }
}
