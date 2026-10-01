package game.ludora.engine.ludo.model

import game.ludora.core.model.Player
import game.ludora.core.model.PlayerColor
import kotlinx.serialization.Serializable

/**
 * State of a single player in a Ludo match.
 */
@Serializable
data class LudoPlayerState(
    val player: Player,
    val tokens: List<LudoToken>,
    val consecutiveSixes: Int = 0,
    val rank: Int? = null
) {
    init {
        require(tokens.size == 4) { "Player must have exactly 4 tokens, found ${tokens.size}" }
    }

    val color: PlayerColor
        get() = player.color

    val seatIndex: Int
        get() = player.seatIndex

    val isFinished: Boolean
        get() = tokens.all { it.isFinished }

    val finishedTokenCount: Int
        get() = tokens.count { it.isFinished }

    companion object {
        fun initial(player: Player): LudoPlayerState {
            val tokens = (0..3).map { slot ->
                LudoToken(
                    id = slot,
                    color = player.color,
                    position = LudoPosition.InBase(slotIndex = slot)
                )
            }
            return LudoPlayerState(
                player = player,
                tokens = tokens,
                consecutiveSixes = 0,
                rank = null
            )
        }
    }
}
