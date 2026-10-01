package game.ludora.engine.snake.model

import kotlinx.serialization.Serializable

/**
 * Result of a single step transition on the Snake & Ladder board.
 */
@Serializable
data class SquareTransition(
    val landedSquare: Int,
    val finalSquare: Int,
    val isLadder: Boolean = false,
    val isSnake: Boolean = false,
    val isOvershoot: Boolean = false
)

/**
 * Constants, matrix definitions, and pure coordinate math for the 100-square Classic Snake & Ladder board.
 */
object SnakeBoard {

    const val TOTAL_SQUARES = 100
    const val VIRTUAL_START_SQUARE = 0
    const val VICTORY_SQUARE = 100

    /**
     * 7 Classic Ladders: Base -> Top
     */
    val LADDERS: Map<Int, Int> = mapOf(
        4 to 14,
        9 to 31,
        20 to 38,
        28 to 84,
        40 to 59,
        51 to 67,
        63 to 81
    )

    /**
     * 8 Classic Snakes: Head -> Tail
     */
    val SNAKES: Map<Int, Int> = mapOf(
        17 to 7,
        54 to 34,
        62 to 19,
        64 to 60,
        87 to 24,
        93 to 73,
        95 to 75,
        99 to 78
    )

    /**
     * Calculates the destination square given the current square and rolled die value.
     */
    fun calculateDestination(currentSquare: Int, roll: Int): SquareTransition {
        if (roll !in 1..6) {
            return SquareTransition(currentSquare, currentSquare)
        }

        val targetSquare = currentSquare + roll

        // Exact finish requirement: overshoot stalls in place
        if (targetSquare > TOTAL_SQUARES) {
            return SquareTransition(
                landedSquare = currentSquare,
                finalSquare = currentSquare,
                isOvershoot = true
            )
        }

        // Ladder base reached
        if (targetSquare in LADDERS) {
            val ladderTop = LADDERS.getValue(targetSquare)
            return SquareTransition(
                landedSquare = targetSquare,
                finalSquare = ladderTop,
                isLadder = true
            )
        }

        // Snake head reached
        if (targetSquare in SNAKES) {
            val snakeTail = SNAKES.getValue(targetSquare)
            return SquareTransition(
                landedSquare = targetSquare,
                finalSquare = snakeTail,
                isSnake = true
            )
        }

        // Normal square (or exact victory square 100)
        return SquareTransition(
            landedSquare = targetSquare,
            finalSquare = targetSquare
        )
    }

    /**
     * Maps square index (1 to 100) to standard 10x10 boustrophedon (row, col) grid coordinates.
     * Row 0 is bottom (squares 1..10, left-to-right).
     * Row 9 is top (squares 91..100, right-to-left).
     * Returns Pair(row, col) where row is 0..9 and col is 0..9.
     */
    fun boustrophedonCoordinates(square: Int): Pair<Int, Int> {
        require(square in 1..TOTAL_SQUARES) { "Square must be in 1..100, got $square" }
        val zeroIndexed = square - 1
        val row = zeroIndexed / 10
        val col = if (row % 2 == 0) {
            zeroIndexed % 10
        } else {
            9 - (zeroIndexed % 10)
        }
        return Pair(row, col)
    }
}
