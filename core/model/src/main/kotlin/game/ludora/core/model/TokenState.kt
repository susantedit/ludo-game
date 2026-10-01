package game.ludora.core.model

import kotlinx.serialization.Serializable

@Serializable
enum class TokenState {
    IN_BASE,
    ON_TRACK,
    IN_HOME_PATH,
    FINISHED
}

@Serializable
data class Token(
    val id: String,
    val color: PlayerColor,
    val tokenIndex: Int, // 0 to 3
    val state: TokenState = TokenState.IN_BASE,
    val trackPosition: Int = -1, // 0 to 51 when ON_TRACK, 1 to 5 when IN_HOME_PATH
    val stepsMoved: Int = 0
) {
    val isFinished: Boolean get() = state == TokenState.FINISHED
    val isInBase: Boolean get() = state == TokenState.IN_BASE
    val isOnTrack: Boolean get() = state == TokenState.ON_TRACK
    val isInHomePath: Boolean get() = state == TokenState.IN_HOME_PATH
}
