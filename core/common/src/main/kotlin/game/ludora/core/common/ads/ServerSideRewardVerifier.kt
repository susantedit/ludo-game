package game.ludora.core.common.ads

import java.security.MessageDigest
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

/**
 * Server-Side Verification (SSV) token validator preventing client-side reward spoofing.
 */
class ServerSideRewardVerifier(
    private val verificationSecret: String = "ludora_production_ssv_secret_key_2026"
) {

    /**
     * Computes an HMAC-SHA256 signature for an ad completion event.
     */
    fun generateSignature(
        userId: String,
        placement: String,
        rewardAmount: Int,
        timestampMs: Long,
        nonce: String
    ): String {
        val payload = "$userId:$placement:$rewardAmount:$timestampMs:$nonce"
        val hmac = Mac.getInstance("HmacSHA256")
        val secretKey = SecretKeySpec(verificationSecret.toByteArray(Charsets.UTF_8), "HmacSHA256")
        hmac.init(secretKey)
        val hash = hmac.doFinal(payload.toByteArray(Charsets.UTF_8))
        return hash.joinToString("") { "%02x".format(it) }
    }

    /**
     * Verifies that the inbound callback signature matches the calculated hash and is within the timestamp freshness window.
     */
    fun verifyReward(
        userId: String,
        placement: String,
        rewardAmount: Int,
        timestampMs: Long,
        nonce: String,
        signature: String,
        currentTimeMs: Long = System.currentTimeMillis(),
        maxDriftMs: Long = 5 * 60 * 1000L // 5 minutes freshness window
    ): Boolean {
        // Enforce freshness window
        if (kotlin.math.abs(currentTimeMs - timestampMs) > maxDriftMs) {
            return false
        }

        val expectedSignature = generateSignature(userId, placement, rewardAmount, timestampMs, nonce)
        return MessageDigest.isEqual(
            expectedSignature.toByteArray(Charsets.UTF_8),
            signature.toByteArray(Charsets.UTF_8)
        )
    }
}
