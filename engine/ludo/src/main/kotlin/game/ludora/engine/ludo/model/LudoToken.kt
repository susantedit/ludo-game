package game.ludora.engine.ludo.model

import game.ludora.core.model.PlayerColor
import game.ludora.core.model.TokenState
import kotlinx.serialization.Serializable

/**
 * Immutable representation of a single Ludo token.
 *
 * @property id Identifier of the token (0 to 3).
 * @property color Player color that owns this token.
 * @property position Current board position.
 */
@Serializable
data class LudoToken(
    val id: Int,
    val color: PlayerColor,
    val position: LudoPosition
) {
    val state: TokenState
        get() = when (position) {
            is LudoPosition.InBase -> TokenState.IN_BASE
            is LudoPosition.OnTrack -> TokenState.ON_TRACK
            is LudoPosition.InHomePath -> TokenState.IN_HOME_PATH
            is LudoPosition.Finished -> TokenState.FINISHED
        }

    val isFinished: Boolean
        get() = position is LudoPosition.Finished
}
