package game.ludora.engine.remix.model

import kotlinx.serialization.Serializable

@Serializable
enum class HazardType {
    LADDER,
    SNAKE
}

@Serializable
data class TrackHazard(
    val fromStep: Int,
    val toStep: Int,
    val type: HazardType
)

object RemixHazard {
    // 3 Track Ladders (advances tokens forward on 52-step track)
    val LADDERS = mapOf(
        6 to 18,
        22 to 32,
        36 to 44
    )

    // 3 Track Snakes (drops tokens backward on 52-step track)
    val SNAKES = mapOf(
        16 to 4,
        28 to 14,
        42 to 24
    )

    fun getHazardAt(stepIndex: Int): TrackHazard? {
        val ladderTop = LADDERS[stepIndex]
        if (ladderTop != null) {
            return TrackHazard(fromStep = stepIndex, toStep = ladderTop, type = HazardType.LADDER)
        }
        val snakeTail = SNAKES[stepIndex]
        if (snakeTail != null) {
            return TrackHazard(fromStep = stepIndex, toStep = snakeTail, type = HazardType.SNAKE)
        }
        return null
    }
}
