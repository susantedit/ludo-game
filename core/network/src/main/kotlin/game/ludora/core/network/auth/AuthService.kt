package game.ludora.core.network.auth

import game.ludora.core.network.model.AccountLinkRequest
import game.ludora.core.network.model.AuthSession
import game.ludora.core.network.model.GuestLoginRequest
import java.util.UUID

interface AuthService {
    suspend fun loginAsGuest(request: GuestLoginRequest): Result<AuthSession>
    suspend fun linkAccount(userId: String, request: AccountLinkRequest): Result<AuthSession>
    suspend fun refreshSession(refreshToken: String): Result<AuthSession>
}

class DefaultAuthService : AuthService {

    private val sessions = mutableMapOf<String, AuthSession>()

    override suspend fun loginAsGuest(request: GuestLoginRequest): Result<AuthSession> {
        val userId = "guest_${UUID.nameUUIDFromBytes(request.deviceFingerprint.toByteArray())}"
        val now = System.currentTimeMillis()
        val session = AuthSession(
            userId = userId,
            accessToken = "jwt_access_${UUID.randomUUID()}",
            refreshToken = "jwt_refresh_${UUID.randomUUID()}",
            expiresAtTimestamp = now + (24 * 60 * 60 * 1000L), // 24 hours
            isNewUser = !sessions.containsKey(userId)
        )
        sessions[userId] = session
        return Result.success(session)
    }

    override suspend fun linkAccount(userId: String, request: AccountLinkRequest): Result<AuthSession> {
        val existing = sessions[userId] ?: return Result.failure(IllegalStateException("Session not found"))
        val updated = existing.copy(
            accessToken = "jwt_linked_${UUID.randomUUID()}"
        )
        sessions[userId] = updated
        return Result.success(updated)
    }

    override suspend fun refreshSession(refreshToken: String): Result<AuthSession> {
        val session = sessions.values.firstOrNull { it.refreshToken == refreshToken }
            ?: return Result.failure(IllegalArgumentException("Invalid refresh token"))

        val refreshed = session.copy(
            accessToken = "jwt_access_${UUID.randomUUID()}",
            expiresAtTimestamp = System.currentTimeMillis() + (24 * 60 * 60 * 1000L)
        )
        sessions[session.userId] = refreshed
        return Result.success(refreshed)
    }
}
