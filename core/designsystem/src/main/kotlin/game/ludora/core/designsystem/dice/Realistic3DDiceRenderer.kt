package game.ludora.core.designsystem.dice

import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.max

data class Vector3D(val x: Float, val y: Float, val z: Float) {
    fun dot(other: Vector3D): Float = x * other.x + y * other.y + z * other.z

    fun length(): Float = sqrt(x * x + y * y + z * z)

    fun normalize(): Vector3D {
        val len = length()
        return if (len > 0f) Vector3D(x / len, y / len, z / len) else this
    }

    fun rotateX(angleRad: Float): Vector3D {
        val cosA = cos(angleRad)
        val sinA = sin(angleRad)
        return Vector3D(x, y * cosA - z * sinA, y * sinA + z * cosA)
    }

    fun rotateY(angleRad: Float): Vector3D {
        val cosA = cos(angleRad)
        val sinA = sin(angleRad)
        return Vector3D(x * cosA + z * sinA, y, -x * sinA + z * cosA)
    }

    fun rotateZ(angleRad: Float): Vector3D {
        val cosA = cos(angleRad)
        val sinA = sin(angleRad)
        return Vector3D(x * cosA - y * sinA, x * sinA + y * cosA, z)
    }

    fun rotate(eulerX: Float, eulerY: Float, eulerZ: Float): Vector3D {
        return this.rotateX(eulerX).rotateY(eulerY).rotateZ(eulerZ)
    }
}

data class Point2D(val x: Float, val y: Float)

data class PipPoint(
    val center: Point2D,
    val radius: Float,
    val isVisible: Boolean
)

data class DiceFacet(
    val faceValue: Int,
    val normal: Vector3D,
    val vertices: List<Point2D>,
    val lightIntensity: Float,
    val isVisible: Boolean,
    val pips: List<PipPoint>
)

/**
 * 3D Isometric Tumbling Dice Renderer.
 * Evaluates 3D cubic facet geometry, rotation matrices, Phong illumination vectors,
 * and recessed gold foil pips for high-fidelity board game rendering.
 */
class Realistic3DDiceRenderer(
    private val lightDirection: Vector3D = Vector3D(-0.4f, -0.6f, 0.7f).normalize(),
    private val cameraDirection: Vector3D = Vector3D(0f, 0f, 1f)
) {

    // 8 vertices of a unit cube centered at origin
    private val baseCubeVertices = listOf(
        Vector3D(-0.5f, -0.5f, -0.5f), // 0
        Vector3D(0.5f, -0.5f, -0.5f),  // 1
        Vector3D(0.5f, 0.5f, -0.5f),   // 2
        Vector3D(-0.5f, 0.5f, -0.5f),  // 3
        Vector3D(-0.5f, -0.5f, 0.5f),  // 4
        Vector3D(0.5f, -0.5f, 0.5f),   // 5
        Vector3D(0.5f, 0.5f, 0.5f),    // 6
        Vector3D(-0.5f, 0.5f, 0.5f)    // 7
    )

    // 6 faces: (faceValue, normal, vertexIndices)
    // Standard dice opposite sides sum to 7 (1 opp 6, 2 opp 5, 3 opp 4)
    private val faces = listOf(
        Triple(1, Vector3D(0f, 0f, 1f), listOf(4, 5, 6, 7)),     // Front (+Z): 1
        Triple(6, Vector3D(0f, 0f, -1f), listOf(1, 0, 3, 2)),    // Back (-Z): 6
        Triple(2, Vector3D(0f, 1f, 0f), listOf(7, 6, 2, 3)),     // Top (+Y): 2
        Triple(5, Vector3D(0f, -1f, 0f), listOf(4, 0, 1, 5)),    // Bottom (-Y): 5
        Triple(3, Vector3D(1f, 0f, 0f), listOf(5, 1, 2, 6)),     // Right (+X): 3
        Triple(4, Vector3D(-1f, 0f, 0f), listOf(0, 4, 7, 3))     // Left (-X): 4
    )

    fun computeLighting(normal: Vector3D): Float {
        val diffuse = max(0.18f, normal.dot(lightDirection))
        // Halfway specular reflection vector
        val halfway = Vector3D(
            lightDirection.x + cameraDirection.x,
            lightDirection.y + cameraDirection.y,
            lightDirection.z + cameraDirection.z
        ).normalize()
        val specular = (max(0f, normal.dot(halfway))).let { it * it * it * it } * 0.35f
        return (diffuse + specular).coerceIn(0.15f, 1.0f)
    }

    fun projectToScreen(v: Vector3D, centerX: Float, centerY: Float, size: Float): Point2D {
        // Isometric perspective projection with slight z-depth scaling
        val depth = 1f + (v.z * 0.15f)
        return Point2D(
            x = centerX + (v.x * size * depth),
            y = centerY - (v.y * size * depth)
        )
    }

    companion object {
        private val SETTLED_FACETS_MATRICES = mapOf(
            1 to floatArrayOf(0.7074f, -0.7068f, 0.0f, 0.4078f, 0.4081f, 0.8168f, -0.5773f, -0.5778f, 0.577f),
            2 to floatArrayOf(0.7074f, 0.0f, 0.7068f, 0.4078f, 0.8168f, -0.4081f, -0.5773f, 0.577f, 0.5778f),
            3 to floatArrayOf(0.0f, -0.7074f, 0.7068f, 0.8168f, -0.4078f, -0.4081f, 0.577f, 0.5773f, 0.5778f),
            4 to floatArrayOf(0.0f, 0.7074f, 0.7068f, -0.8168f, 0.4078f, -0.4081f, -0.577f, -0.5773f, 0.5778f),
            5 to floatArrayOf(0.7074f, 0.0f, -0.7068f, 0.4078f, -0.8168f, 0.4081f, -0.5773f, -0.577f, -0.5778f),
            6 to floatArrayOf(0.7074f, 0.7068f, 0.0f, 0.4078f, -0.4081f, -0.8168f, -0.5773f, 0.5778f, -0.577f)
        )
    }

    /**
     * Transforms vector by 3x3 matrix row-major FloatArray.
     */
    fun transformWithMatrix(m: FloatArray, v: Vector3D): Vector3D {
        return Vector3D(
            x = m[0] * v.x + m[1] * v.y + m[2] * v.z,
            y = m[3] * v.x + m[4] * v.y + m[5] * v.z,
            z = m[6] * v.x + m[7] * v.y + m[8] * v.z
        )
    }

    /**
     * Computes facets with the rolled number guaranteed to be facing upward on the top face.
     */
    fun computeSettledFacets(
        centerX: Float,
        centerY: Float,
        size: Float,
        rolledValue: Int
    ): List<DiceFacet> {
        val matrix = SETTLED_FACETS_MATRICES[rolledValue.coerceIn(1, 6)] ?: SETTLED_FACETS_MATRICES[6]!!
        val rotatedVertices = baseCubeVertices.map { transformWithMatrix(matrix, it) }
        val projectedVertices = rotatedVertices.map { projectToScreen(it, centerX, centerY, size) }

        return faces.map { (faceValue, normal, indices) ->
            val rotatedNormal = transformWithMatrix(matrix, normal).normalize()
            val isVisible = rotatedNormal.dot(cameraDirection) > 0.05f
            val lightIntensity = computeLighting(rotatedNormal)
            val faceVertices = indices.map { projectedVertices[it] }

            val pips = if (isVisible) {
                calculatePips(faceValue, faceVertices, size * 0.09f)
            } else {
                emptyList()
            }

            DiceFacet(
                faceValue = faceValue,
                normal = rotatedNormal,
                vertices = faceVertices,
                lightIntensity = lightIntensity,
                isVisible = isVisible,
                pips = pips
            )
        }.sortedBy { it.normal.z }
    }

    fun computeFacets(
        centerX: Float,
        centerY: Float,
        size: Float,
        eulerX: Float,
        eulerY: Float,
        eulerZ: Float
    ): List<DiceFacet> {
        val rotatedVertices = baseCubeVertices.map { it.rotate(eulerX, eulerY, eulerZ) }
        val projectedVertices = rotatedVertices.map { projectToScreen(it, centerX, centerY, size) }

        return faces.map { (faceValue, normal, indices) ->
            val rotatedNormal = normal.rotate(eulerX, eulerY, eulerZ).normalize()
            val isVisible = rotatedNormal.dot(cameraDirection) > 0.05f
            val lightIntensity = computeLighting(rotatedNormal)
            val faceVertices = indices.map { projectedVertices[it] }

            val pips = if (isVisible) {
                calculatePips(faceValue, faceVertices, size * 0.09f)
            } else {
                emptyList()
            }

            DiceFacet(
                faceValue = faceValue,
                normal = rotatedNormal,
                vertices = faceVertices,
                lightIntensity = lightIntensity,
                isVisible = isVisible,
                pips = pips
            )
        }.sortedBy { it.normal.z } // Sort by depth for painter's algorithm
    }

    fun calculatePips(faceValue: Int, quad: List<Point2D>, pipRadius: Float): List<PipPoint> {
        if (quad.size < 4) return emptyList()
        val v0 = quad[0]
        val v1 = quad[1]
        val v2 = quad[2]
        val v3 = quad[3]

        // Bilinear interpolation inside quad: u, v in [0, 1]
        fun interp(u: Float, v: Float): Point2D {
            val topX = v0.x + (v1.x - v0.x) * u
            val topY = v0.y + (v1.y - v0.y) * u
            val botX = v3.x + (v2.x - v3.x) * u
            val botY = v3.y + (v2.y - v3.y) * u
            return Point2D(
                x = topX + (botX - topX) * v,
                y = topY + (botY - topY) * v
            )
        }

        val pipCoords = when (faceValue) {
            1 -> listOf(Pair(0.5f, 0.5f))
            2 -> listOf(Pair(0.25f, 0.25f), Pair(0.75f, 0.75f))
            3 -> listOf(Pair(0.25f, 0.25f), Pair(0.5f, 0.5f), Pair(0.75f, 0.75f))
            4 -> listOf(Pair(0.25f, 0.25f), Pair(0.75f, 0.25f), Pair(0.25f, 0.75f), Pair(0.75f, 0.75f))
            5 -> listOf(Pair(0.25f, 0.25f), Pair(0.75f, 0.25f), Pair(0.5f, 0.5f), Pair(0.25f, 0.75f), Pair(0.75f, 0.75f))
            6 -> listOf(
                Pair(0.25f, 0.2f), Pair(0.75f, 0.2f),
                Pair(0.25f, 0.5f), Pair(0.75f, 0.5f),
                Pair(0.25f, 0.8f), Pair(0.75f, 0.8f)
            )
            else -> emptyList()
        }

        return pipCoords.map { (u, v) ->
            PipPoint(
                center = interp(u, v),
                radius = pipRadius,
                isVisible = true
            )
        }
    }
}
