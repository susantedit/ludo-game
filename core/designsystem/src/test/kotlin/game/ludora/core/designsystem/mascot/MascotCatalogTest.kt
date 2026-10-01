package game.ludora.core.designsystem.mascot

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MascotCatalogTest {

    @Test
    fun testAll56MascotsCataloged() {
        assertEquals(56, MascotCatalog.ALL.size)

        // 20 Animals
        assertEquals(20, MascotCatalog.getByCategory(MascotCategory.ANIMALS).size)

        // 18 People
        assertEquals(18, MascotCatalog.getByCategory(MascotCategory.PEOPLE).size)

        // 13 Robots & Things
        assertEquals(13, MascotCatalog.getByCategory(MascotCategory.ROBOTS_AND_THINGS).size)

        // 5 Fox Styles
        assertEquals(5, MascotCatalog.getByCategory(MascotCategory.SPECIAL_STYLES).size)
    }

    @Test
    fun testFindMascotById() {
        val fox = MascotCatalog.findById("fox")
        assertEquals("Fox", fox.name)
        assertEquals("mascots/fox-directions.webp", fox.directionsAsset)
        assertEquals("mascots/fox-reactions.webp", fox.reactionsAsset)

        val cat = MascotCatalog.findById("cat")
        assertEquals("Cat", cat.name)

        // Fallback for unknown id
        val fallback = MascotCatalog.findById("unknown_robot_x")
        assertEquals("fox", fallback.id)
    }

    @Test
    fun testGazeDirectionTrigonometryFromDelta() {
        // Delta (0, 0) inside dead zone -> CENTER
        assertEquals(MascotDirection.CENTER, MascotDirection.fromDelta(0f, 0f))
        assertEquals(MascotDirection.CENTER, MascotDirection.fromDelta(10f, 10f))

        // Direct Right (+X, 0)
        assertEquals(MascotDirection.RIGHT, MascotDirection.fromDelta(100f, 0f))

        // Direct Left (-X, 0)
        assertEquals(MascotDirection.LEFT, MascotDirection.fromDelta(-100f, 0f))

        // Direct Down (0, +Y)
        assertEquals(MascotDirection.DOWN, MascotDirection.fromDelta(0f, 100f))

        // Direct Up (0, -Y)
        assertEquals(MascotDirection.UP, MascotDirection.fromDelta(0f, -100f))

        // Diagonals
        assertEquals(MascotDirection.DOWN_RIGHT, MascotDirection.fromDelta(100f, 100f))
        assertEquals(MascotDirection.DOWN_LEFT, MascotDirection.fromDelta(-100f, 100f))
        assertEquals(MascotDirection.UP_RIGHT, MascotDirection.fromDelta(100f, -100f))
        assertEquals(MascotDirection.UP_LEFT, MascotDirection.fromDelta(-100f, -100f))
    }
}
