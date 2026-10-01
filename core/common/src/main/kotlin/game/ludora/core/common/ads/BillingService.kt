package game.ludora.core.common.ads

/**
 * In-App Purchase (IAP) contract for non-consumable entitlements such as "Remove Ads".
 */
interface BillingService {
    /**
     * Checks if the user holds an active Ad-Free entitlement.
     */
    fun isAdFree(): Boolean

    /**
     * Initiates the purchase flow for the one-time "Remove Ads" pass.
     */
    suspend fun purchaseRemoveAds(): Result<Boolean>

    /**
     * Restores previously completed Google Play or App Store purchases.
     */
    suspend fun restorePurchases(): Result<Boolean>
}

/**
 * In-memory test double of [BillingService] for testing purchase workflows.
 */
class FakeBillingService(
    private var adFreeEntitlement: Boolean = false,
    var shouldFailPurchases: Boolean = false
) : BillingService {

    var purchaseAttempts: Int = 0
        private set

    var restoreAttempts: Int = 0
        private set

    override fun isAdFree(): Boolean = adFreeEntitlement

    override suspend fun purchaseRemoveAds(): Result<Boolean> {
        purchaseAttempts++
        return if (shouldFailPurchases) {
            Result.failure(IllegalStateException("Billing service unavailable"))
        } else {
            adFreeEntitlement = true
            Result.success(true)
        }
    }

    override suspend fun restorePurchases(): Result<Boolean> {
        restoreAttempts++
        return if (shouldFailPurchases) {
            Result.failure(IllegalStateException("Failed to query purchase history"))
        } else {
            Result.success(adFreeEntitlement)
        }
    }
}
