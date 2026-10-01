package game.ludora.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class GuestLoginRequest(
    val deviceFingerprint: String,
    val appVersion: String = "0.1.0"
)

@Serializable
data class AuthSession(
    val userId: String,
    val accessToken: String,
    val refreshToken: String,
    val expiresAtTimestamp: Long,
    val isNewUser: Boolean = false
)

@Serializable
data class AccountLinkRequest(
    val provider: String, // "GOOGLE_PLAY", "GAME_CENTER"
    val providerToken: String
)
