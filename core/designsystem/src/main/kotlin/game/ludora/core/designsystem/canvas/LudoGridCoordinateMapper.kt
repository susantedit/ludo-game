package game.ludora.core.designsystem.canvas

import androidx.compose.ui.geometry.Offset
import game.ludora.core.model.PlayerColor

/**
 * Pure coordinate calculator mapping the standard 15x15 Ludo board cells to Canvas offsets.
 */
object LudoGridCoordinateMapper {

    const val GRID_SIZE = 15

    // Track steps 0..51 mapped to Pair(col, row) on 15x15 grid
    private val TRACK_GRID_CELLS: List<Pair<Int, Int>> = listOf(
        Pair(1, 6), Pair(2, 6), Pair(3, 6), Pair(4, 6), Pair(5, 6),   // 0..4 (Red arm going right)
        Pair(6, 5), Pair(6, 4), Pair(6, 3), Pair(6, 2), Pair(6, 1), Pair(6, 0), // 5..10 (turning up)
        Pair(7, 0), // 11 (Green entrance)
        Pair(8, 0), Pair(8, 1), Pair(8, 2), Pair(8, 3), Pair(8, 4), Pair(8, 5), // 12..17 (down toward center)
        Pair(9, 6), Pair(10, 6), Pair(11, 6), Pair(12, 6), Pair(13, 6), Pair(14, 6), // 18..23 (Yellow arm going right)
        Pair(14, 7), // 24 (Yellow entrance)
        Pair(14, 8), Pair(13, 8), Pair(12, 8), Pair(11, 8), Pair(10, 8), Pair(9, 8), // 25..30 (left toward center)
        Pair(8, 9), Pair(8, 10), Pair(8, 11), Pair(8, 12), Pair(8, 13), Pair(8, 14), // 31..36 (down Blue arm)
        Pair(7, 14), // 37 (Blue entrance)
        Pair(6, 14), Pair(6, 13), Pair(6, 12), Pair(6, 11), Pair(6, 10), Pair(6, 9), // 38..43 (up toward center)
        Pair(5, 8), Pair(4, 8), Pair(3, 8), Pair(2, 8), Pair(1, 8), Pair(0, 8), // 44..49 (left along Red arm)
        Pair(0, 7), // 50 (Red entrance)
        Pair(0, 6)  // 51
    )

    fun getTrackGridCell(stepIndex: Int): Pair<Int, Int> {
        require(stepIndex in 0..51) { "Step index must be in 0..51, got $stepIndex" }
        return TRACK_GRID_CELLS[stepIndex]
    }

    fun getHomePathGridCell(color: PlayerColor, stepIndex: Int): Pair<Int, Int> {
        require(stepIndex in 1..5) { "Home path step must be in 1..5, got $stepIndex" }
        return when (color) {
            PlayerColor.RED -> Pair(stepIndex, 7)
            PlayerColor.GREEN -> Pair(7, stepIndex)
            PlayerColor.YELLOW -> Pair(14 - stepIndex, 7)
            PlayerColor.BLUE -> Pair(7, 14 - stepIndex)
        }
    }

    fun getBaseSlotGridCell(color: PlayerColor, slotIndex: Int): Pair<Int, Int> {
        require(slotIndex in 0..3) { "Base slot index must be in 0..3, got $slotIndex" }
        val (baseCol, baseRow) = when (color) {
            PlayerColor.RED -> Pair(2, 2)
            PlayerColor.GREEN -> Pair(11, 2)
            PlayerColor.YELLOW -> Pair(11, 11)
            PlayerColor.BLUE -> Pair(2, 11)
        }
        val colOffset = slotIndex % 2
        val rowOffset = slotIndex / 2
        return Pair(baseCol + colOffset, baseRow + rowOffset)
    }

    fun getCenterGoalGridCell(): Pair<Int, Int> = Pair(7, 7)

    fun cellCenterOffset(col: Int, row: Int, boardWidthPx: Float): Offset {
        val cellSize = boardWidthPx / GRID_SIZE
        return Offset(
            x = (col + 0.5f) * cellSize,
            y = (row + 0.5f) * cellSize
        )
    }
}
