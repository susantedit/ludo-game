package game.ludora.engine.ai

import game.ludora.core.model.Player
import game.ludora.core.model.PlayerColor
import game.ludora.core.model.TokenState
import game.ludora.engine.ai.model.AiDifficulty
import game.ludora.engine.ludo.logic.LegalMove
import game.ludora.engine.ludo.model.LudoGameState
import game.ludora.engine.ludo.model.LudoPlayerState
import game.ludora.engine.ludo.model.LudoPosition
import game.ludora.engine.ludo.model.LudoToken
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class LudoAiAgentTest {

    private fun createPlayer(seatIndex: Int, color: PlayerColor): Player =
        Player(
            id = "p_$seatIndex",
            displayName = "Player $color",
            color = color,
            seatIndex = seatIndex,
            isAi = true
        )

    @Test
    fun testSingleLegalMoveReturnsDirectly() {
        val red = LudoPlayerState(
            player = createPlayer(0, PlayerColor.RED),
            tokens = listOf(
                LudoToken(id = 0, color = PlayerColor.RED, position = LudoPosition.OnTrack(5), state = TokenState.ON_TRACK),
                LudoToken(id = 1, color = PlayerColor.RED, position = LudoPosition.InBase(1), state = TokenState.IN_BASE),
                LudoToken(id = 2, color = PlayerColor.RED, position = LudoPosition.InBase(2), state = TokenState.IN_BASE),
                LudoToken(id = 3, color = PlayerColor.RED, position = LudoPosition.InBase(3), state = TokenState.IN_BASE)
            )
        )
        val green = LudoPlayerState.initial(createPlayer(1, PlayerColor.GREEN))
        val state = LudoGameState("test", listOf(red, green), activeSeatIndex = 0)

        // On roll 2, only token 0 can move
        val move = LudoAiAgent.selectMove(state, rollValue = 2, difficulty = AiDifficulty.HARD)
        assertNotNull(move)
        assertEquals(0, move!!.tokenId)
    }

    @Test
    fun testMediumAndHardPrioritizesCapturingOpponent() {
        // Red token 0 is at 10. Red token 1 is at 20.
        // Opponent Green token is at 14.
        // Roll = 4.
        // Token 0 lands on 14 (captures Green!).
        // Token 1 lands on 24 (normal advance).
        val red = LudoPlayerState(
            player = createPlayer(0, PlayerColor.RED),
            tokens = listOf(
                LudoToken(id = 0, color = PlayerColor.RED, position = LudoPosition.OnTrack(10), state = TokenState.ON_TRACK),
                LudoToken(id = 1, color = PlayerColor.RED, position = LudoPosition.OnTrack(20), state = TokenState.ON_TRACK),
                LudoToken(id = 2, color = PlayerColor.RED, position = LudoPosition.InBase(2), state = TokenState.IN_BASE),
                LudoToken(id = 3, color = PlayerColor.RED, position = LudoPosition.InBase(3), state = TokenState.IN_BASE)
            )
        )
        val green = LudoPlayerState(
            player = createPlayer(1, PlayerColor.GREEN),
            tokens = listOf(
                LudoToken(id = 0, color = PlayerColor.GREEN, position = LudoPosition.OnTrack(14), state = TokenState.ON_TRACK),
                LudoToken(id = 1, color = PlayerColor.GREEN, position = LudoPosition.InBase(1), state = TokenState.IN_BASE),
                LudoToken(id = 2, color = PlayerColor.GREEN, position = LudoPosition.InBase(2), state = TokenState.IN_BASE),
                LudoToken(id = 3, color = PlayerColor.GREEN, position = LudoPosition.InBase(3), state = TokenState.IN_BASE)
            )
        )
        val state = LudoGameState("capture_test", listOf(red, green), activeSeatIndex = 0)

        val moveMedium = LudoAiAgent.selectMove(state, rollValue = 4, difficulty = AiDifficulty.MEDIUM)
        assertNotNull(moveMedium)
        assertTrue("Medium AI must capture opponent", moveMedium!!.isCapture)
        assertEquals(0, moveMedium.tokenId)

        val moveHard = LudoAiAgent.selectMove(state, rollValue = 4, difficulty = AiDifficulty.HARD)
        assertNotNull(moveHard)
        assertTrue("Hard AI must capture opponent", moveHard!!.isCapture)
        assertEquals(0, moveHard.tokenId)
    }

    @Test
    fun testHardAvoidsMovingIntoOpponentThreatZone() {
        // Red token 0 is at 3.
        // Red token 1 is at 25.
        // Opponent Green is at 5.
        // Roll = 4.
        // If Red moves token 0: 3 + 4 = 7 (Distance from Green at 5 to 7 is 2 -> in danger!)
        // If Red moves token 1: 25 + 4 = 29 (Safe distance from all opponents)
        val red = LudoPlayerState(
            player = createPlayer(0, PlayerColor.RED),
            tokens = listOf(
                LudoToken(id = 0, color = PlayerColor.RED, position = LudoPosition.OnTrack(3), state = TokenState.ON_TRACK),
                LudoToken(id = 1, color = PlayerColor.RED, position = LudoPosition.OnTrack(25), state = TokenState.ON_TRACK),
                LudoToken(id = 2, color = PlayerColor.RED, position = LudoPosition.InBase(2), state = TokenState.IN_BASE),
                LudoToken(id = 3, color = PlayerColor.RED, position = LudoPosition.InBase(3), state = TokenState.IN_BASE)
            )
        )
        val green = LudoPlayerState(
            player = createPlayer(1, PlayerColor.GREEN),
            tokens = listOf(
                LudoToken(id = 0, color = PlayerColor.GREEN, position = LudoPosition.OnTrack(5), state = TokenState.ON_TRACK),
                LudoToken(id = 1, color = PlayerColor.GREEN, position = LudoPosition.InBase(1), state = TokenState.IN_BASE),
                LudoToken(id = 2, color = PlayerColor.GREEN, position = LudoPosition.InBase(2), state = TokenState.IN_BASE),
                LudoToken(id = 3, color = PlayerColor.GREEN, position = LudoPosition.InBase(3), state = TokenState.IN_BASE)
            )
        )
        val state = LudoGameState("threat_test", listOf(red, green), activeSeatIndex = 0)

        val move = LudoAiAgent.selectMove(state, rollValue = 4, difficulty = AiDifficulty.HARD)
        assertNotNull(move)
        assertEquals("Hard AI should avoid stepping into opponent threat range", 1, move!!.tokenId)
    }
}
