package game.ludora.core.designsystem.dice

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.PI

class Realistic3DDiceRendererTest {

    private val renderer = Realistic3DDiceRenderer()

    @Test
    fun `vector rotation preserves vector magnitude`() {
        val v = Vector3D(1f, 2f, 3f)
        val initialLen = v.length()
        val rotated = v.rotate((PI / 4).toFloat(), (PI / 3).toFloat(), (PI / 6).toFloat())
        assertEquals(initialLen, rotated.length(), 0.001f)
    }

    @Test
    fun `computeFacets returns exactly six facets for unit cube`() {
        val facets = renderer.computeFacets(
            centerX = 100f,
            centerY = 100f,
            size = 80f,
            eulerX = 0f,
            eulerY = 0f,
            eulerZ = 0f
        )
        assertEquals(6, facets.size)
    }

    @Test
    fun `unrotated cube displays front face 1 with 1 pip`() {
        val facets = renderer.computeFacets(
            centerX = 100f,
            centerY = 100f,
            size = 80f,
            eulerX = 0f,
            eulerY = 0f,
            eulerZ = 0f
        )
        val frontFacet = facets.first { it.faceValue == 1 }
        assertTrue("Front face must be visible", frontFacet.isVisible)
        assertEquals(1, frontFacet.pips.size)
    }

    @Test
    fun `isometric tilted cube displays up to three visible faces`() {
        // Tilted 35.26 degrees on X, 45 degrees on Y (Standard isometric orientation)
        val facets = renderer.computeFacets(
            centerX = 100f,
            centerY = 100f,
            size = 80f,
            eulerX = 0.615f,
            eulerY = 0.785f,
            eulerZ = 0f
        )
        val visibleFacets = facets.filter { it.isVisible }
        assertTrue("Should have 2 or 3 visible facets in isometric angle", visibleFacets.size in 2..3)
    }

    @Test
    fun `lighting intensity is bounded between ambient floor and full reflection`() {
        val normal = Vector3D(0f, 0f, 1f)
        val intensity = renderer.computeLighting(normal)
        assertTrue("Intensity must be >= 0.15f", intensity >= 0.15f)
        assertTrue("Intensity must be <= 1.0f", intensity <= 1.0f)
    }

    @Test
    fun `all pip counts match face values exactly`() {
        val dummyQuad = listOf(
            Point2D(0f, 0f),
            Point2D(100f, 0f),
            Point2D(100f, 100f),
            Point2D(0f, 100f)
        )
        for (face in 1..6) {
            val pips = renderer.calculatePips(face, dummyQuad, 5f)
            assertEquals("Face $face must have $face pips", face, pips.size)
        }
    }
}
