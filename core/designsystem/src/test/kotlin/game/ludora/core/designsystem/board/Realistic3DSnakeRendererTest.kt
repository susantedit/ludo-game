package game.ludora.core.designsystem.board

import game.ludora.core.designsystem.dice.Point2D
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class Realistic3DSnakeRendererTest {

    private val renderer = Realistic3DSnakeRenderer()

    @Test
    fun `cubic bezier starts at p0 and terminates at p3`() {
        val p0 = Point2D(0f, 0f)
        val p1 = Point2D(50f, 100f)
        val p2 = Point2D(150f, -50f)
        val p3 = Point2D(200f, 100f)

        val start = renderer.evaluateCubicBezier(p0, p1, p2, p3, 0f)
        val end = renderer.evaluateCubicBezier(p0, p1, p2, p3, 1f)

        assertEquals(p0.x, start.x, 0.001f)
        assertEquals(p0.y, start.y, 0.001f)
        assertEquals(p3.x, end.x, 0.001f)
        assertEquals(p3.y, end.y, 0.001f)
    }

    @Test
    fun `generateSpine produces expected number of samples`() {
        val spine = renderer.generateSpine(
            start = Point2D(10f, 10f),
            control1 = Point2D(30f, 80f),
            control2 = Point2D(80f, 20f),
            end = Point2D(100f, 100f),
            samples = 25
        )
        assertEquals(26, spine.size)
    }

    @Test
    fun `applyUndulation moves points perpendicularly while keeping endpoints anchored`() {
        val spine = renderer.generateSpine(
            start = Point2D(0f, 0f),
            control1 = Point2D(0f, 30f),
            control2 = Point2D(0f, 70f),
            end = Point2D(0f, 100f),
            samples = 20
        )

        val undulated = renderer.applyUndulation(spine, timeSeconds = 0.5f, amplitude = 15f)

        // Endpoints anchored by envelope sin(0) = 0 and sin(pi) = 0
        assertEquals(spine.first().x, undulated.first().x, 0.01f)
        assertEquals(spine.last().x, undulated.last().x, 0.01f)

        // Midpoints must be displaced
        val midIndex = spine.size / 2
        assertNotEquals(spine[midIndex].x, undulated[midIndex].x, 0.1f)
    }

    @Test
    fun `snake geometry tapers from head to tail`() {
        val spine = renderer.generateSpine(
            start = Point2D(50f, 10f),
            control1 = Point2D(70f, 50f),
            control2 = Point2D(30f, 80f),
            end = Point2D(50f, 120f),
            samples = 20
        )

        val snake = renderer.generateSnakeGeometry(spine, headRadius = 16f, tailRadius = 3f)

        val headSegment = snake.segments.first()
        val tailSegment = snake.segments.last()

        assertTrue("Head radius must be larger than tail radius", headSegment.radius > tailSegment.radius)
        assertEquals(16f, headSegment.radius, 0.01f)
        assertEquals(3f, tailSegment.radius, 0.01f)
        assertTrue("Must generate dorsal scales", snake.scales.isNotEmpty())
    }

    @Test
    fun `peristaltic bulge increases segment radius locally at swallow progress`() {
        val spine = renderer.generateSpine(
            start = Point2D(0f, 0f),
            control1 = Point2D(0f, 30f),
            control2 = Point2D(0f, 70f),
            end = Point2D(0f, 100f),
            samples = 20
        )

        val normalSnake = renderer.generateSnakeGeometry(spine, headRadius = 16f, tailRadius = 4f, swallowProgress = -1f)
        val swallowedSnake = renderer.generateSnakeGeometry(spine, headRadius = 16f, tailRadius = 4f, swallowProgress = 0.5f)

        val midIdx = spine.size / 2
        assertTrue(
            "Swallowed snake radius at midpoint must be larger due to bulge",
            swallowedSnake.segments[midIdx].radius > normalSnake.segments[midIdx].radius
        )
    }
}
