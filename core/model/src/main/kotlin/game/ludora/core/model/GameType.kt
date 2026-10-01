package game.ludora.core.model

import kotlinx.serialization.Serializable

@Serializable
enum class GameType(val displayName: String) {
    LUDO("Ludo"),
    SNAKE_AND_LADDER("Snake & Ladder"),
    REMIX("Remix Mode")
}

@Serializable
enum class GameMode {
    OFFLINE_PASS_AND_PLAY,
    OFFLINE_AI,
    ONLINE_PRIVATE,
    ONLINE_MATCHMAKING
}
