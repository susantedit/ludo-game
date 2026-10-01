package game.ludora.core.model

import kotlinx.serialization.Serializable

@Serializable
enum class PlayerColor(
    val seatIndex: Int,
    val startIndex: Int,
    val homeEntranceIndex: Int,
    val starIndex: Int,
    val symbolShape: String
) {
    RED(
        seatIndex = 0,
        startIndex = 0,
        homeEntranceIndex = 50,
        starIndex = 8,
        symbolShape = "CIRCLE"
    ),
    GREEN(
        seatIndex = 1,
        startIndex = 13,
        homeEntranceIndex = 11,
        starIndex = 21,
        symbolShape = "TRIANGLE"
    ),
    YELLOW(
        seatIndex = 2,
        startIndex = 26,
        homeEntranceIndex = 24,
        starIndex = 34,
        symbolShape = "DIAMOND"
    ),
    BLUE(
        seatIndex = 3,
        startIndex = 39,
        homeEntranceIndex = 37,
        starIndex = 47,
        symbolShape = "SQUARE"
    );

    companion object {
        fun fromSeatIndex(index: Int): PlayerColor =
            entries.firstOrNull { it.seatIndex == index } ?: RED
    }
}
