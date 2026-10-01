package game.ludora.core.designsystem.canvas

import androidx.compose.ui.geometry.Offset

/**
 * Pure coordinate calculator mapping 100-square Snake & Ladder board cells to Canvas offsets.
 */
object SnakeGridCoordinateMapper {

    const val GRID_SIZE = 10

    /**
     * Maps square 1..100 to Pair(col, row) where row 0 is the bottom row.
     */
    fun getGridCell(square: Int): Pair<Int, Int> {
        require(square in 1..100) { "Square must be in 1..100, got $square" }
        val zeroIndexed = square - 1
        val row = zeroIndexed / GRID_SIZE
        val col = if (row % 2 == 0) {
            zeroIndexed % GRID_SIZE
        } else {
            (GRID_SIZE - 1) - (zeroIndexed % GRID_SIZE)
        }
        return Pair(col, row)
    }

    /**
     * Maps square 1..100 to Canvas pixel offset.
     * Note that Canvas origin (0, 0) is top-left, so row 0 (bottom) is at boardHeight - cellSizeY.
     */
    fun squareCenterOffset(square: Int, boardWidthPx: Float, boardHeightPx: Float): Offset {
        val (col, row) = getGridCell(square)
        val cellWidth = boardWidthPx / GRID_SIZE
        val cellHeight = boardHeightPx / GRID_SIZE

        val x = (col + 0.5f) * cellWidth
        val y = boardHeightPx - (row + 0.5f) * cellHeight

        return Offset(x, y)
    }
}
