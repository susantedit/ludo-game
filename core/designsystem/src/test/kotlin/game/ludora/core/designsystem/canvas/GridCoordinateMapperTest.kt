package game.ludora.core.designsystem.canvas

import game.ludora.core.model.PlayerColor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GridCoordinateMapperTest {

    @Test
    fun testLudoTrackStepGridCoordinates() {
        // Red start step 0 is at (col 1, row 6)
        val step0 = LudoGridCoordinateMapper.getTrackGridCell(0)
        assertEquals(Pair(1, 6), step0)

        // Green start step 13 is at (col 8, row 1)
        val step13 = LudoGridCoordinateMapper.getTrackGridCell(13)
        assertEquals(Pair(8, 1), step13)

        // Yellow start step 26 is at (col 13, row 8)
        val step26 = LudoGridCoordinateMapper.getTrackGridCell(26)
        assertEquals(Pair(13, 8), step26)

        // Blue start step 39 is at (col 6, row 13)
        val step39 = LudoGridCoordinateMapper.getTrackGridCell(39)
        assertEquals(Pair(6, 13), step39)

        // All 52 track steps are within 0..14
        for (step in 0..51) {
            val (col, row) = LudoGridCoordinateMapper.getTrackGridCell(step)
            assertTrue("Col $col must be in 0..14", col in 0..14)
            assertTrue("Row $row must be in 0..14", row in 0..14)
        }
    }

    @Test
    fun testLudoHomePathGridCoordinates() {
        // Red H1 to H5 are along row 7, cols 1..5
        val redH1 = LudoGridCoordinateMapper.getHomePathGridCell(PlayerColor.RED, 1)
        assertEquals(Pair(1, 7), redH1)
        val redH5 = LudoGridCoordinateMapper.getHomePathGridCell(PlayerColor.RED, 5)
        assertEquals(Pair(5, 7), redH5)

        // Green H1 to H5 are along col 7, rows 1..5
        val greenH1 = LudoGridCoordinateMapper.getHomePathGridCell(PlayerColor.GREEN, 1)
        assertEquals(Pair(7, 1), greenH1)
        val greenH5 = LudoGridCoordinateMapper.getHomePathGridCell(PlayerColor.GREEN, 5)
        assertEquals(Pair(7, 5), greenH5)

        // Center goal is (7, 7)
        val center = LudoGridCoordinateMapper.getCenterGoalGridCell()
        assertEquals(Pair(7, 7), center)
    }

    @Test
    fun testSnakeGridCellMapping() {
        // Square 1: bottom-left (col 0, row 0)
        assertEquals(Pair(0, 0), SnakeGridCoordinateMapper.getGridCell(1))

        // Square 10: bottom-right (col 9, row 0)
        assertEquals(Pair(9, 0), SnakeGridCoordinateMapper.getGridCell(10))

        // Square 11: second row right-to-left (col 9, row 1)
        assertEquals(Pair(9, 1), SnakeGridCoordinateMapper.getGridCell(11))

        // Square 100: top-left (col 0, row 9)
        assertEquals(Pair(0, 9), SnakeGridCoordinateMapper.getGridCell(100))
    }
}
