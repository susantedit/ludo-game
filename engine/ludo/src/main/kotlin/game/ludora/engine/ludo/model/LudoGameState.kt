package game.ludora.engine.ludo.model

import game.ludora.core.model.PlayerColor
import kotlinx.serialization.Serializable

/**
 * Immutable root state of a Ludo game.
 */
@Serializable
data class LudoGameState(
    val matchId: String,
    val players: List<LudoPlayerState>,
    val activeSeatIndex: Int,
    val currentRoll: Int? = null,
    val phase: LudoTurnPhase = LudoTurnPhase.WAITING_FOR_ROLL,
    val roundCount: Int = 1,
    val winners: List<PlayerColor> = emptyList(),
    val isGameOver: Boolean = false,
    val maxRounds: Int = 200
) {
    init {
        require(players.size in 2..4) { "Ludo match must have 2 to 4 players, found ${players.size}" }
    }

    val activePlayer: LudoPlayerState
        get() = players.first { it.seatIndex == activeSeatIndex }

    fun getPlayerByColor(color: PlayerColor): LudoPlayerState? =
        players.firstOrNull { it.color == color }

    fun getPlayerBySeat(seatIndex: Int): LudoPlayerState? =
        players.firstOrNull { it.seatIndex == seatIndex }

    /**
     * Determines whether only one unfinished player remains, in which case the match naturally ends.
     */
    val remainingActivePlayers: List<LudoPlayerState>
        get() = players.filterNot { it.isFinished }
}
