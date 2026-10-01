package game.ludora.core.common.hardware

/**
 * Hardware adaptation policy ensuring smooth performance on budget and low-RAM devices (e.g. 2GB devices).
 */
class LowMemoryPolicy(
    val isLowRamDevice: Boolean = false,
    val availableMemoryMb: Int = 2048
) {
    /**
     * Max particle count for celebrations (e.g. confetti bursts).
     */
    val maxConfettiParticles: Int
        get() = if (isLowRamDevice || availableMemoryMb <= 2048) 20 else 60

    /**
     * Whether complex multi-pass shadow blurs should be simplified to single flat borders.
     */
    val useSimplifiedShadows: Boolean
        get() = isLowRamDevice || availableMemoryMb <= 2048

    /**
     * Maximum number of match history records cached in memory before disk offload.
     */
    val maxInMemoryMatchHistoryRecords: Int
        get() = if (isLowRamDevice || availableMemoryMb <= 2048) 25 else 50

    /**
     * Recommended canvas render quality downscale fraction (1.0 = full resolution).
     */
    val renderScaleFactor: Float
        get() = if (isLowRamDevice || availableMemoryMb < 1500) 0.85f else 1.0f
}
