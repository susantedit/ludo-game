package game.ludora.core.designsystem.board

import game.ludora.core.designsystem.dice.Point2D
import kotlin.math.PI
import kotlin.math.sin
import kotlin.math.cos
import kotlin.math.sqrt

data class SnakeScale(
    val center: Point2D,
    val normal: Point2D,
    val size: Float,
    val isGoldAccent: Boolean
)

data class SnakeSegment(
    val center: Point2D,
    val leftBoundary: Point2D,
    val rightBoundary: Point2D,
    val radius: Float,
    val t: Float
)

data class SnakeHeadGeometry(
    val snout: Point2D,
    val leftJaw: Point2D,
    val rightJaw: Point2D,
    val leftEye: Point2D,
    val rightEye: Point2D,
    val tongueTip: Point2D,
    val tongueForkLeft: Point2D,
    val tongueForkRight: Point2D
)

data class Realistic3DSnake(
    val spinePoints: List<Point2D>,
    val segments: List<SnakeSegment>,
    val scales: List<SnakeScale>,
    val head: SnakeHeadGeometry,
    val tail: Point2D
)

/**
 * Realistic 3D Animated Snake Renderer.
 * Evaluates cubic Bézier spine curves, continuous muscular sine wave undulation,
 * tapered cross-section geometry, procedural diamond scales, and predatory viper head features.
 */
class Realistic3DSnakeRenderer {

    fun evaluateCubicBezier(
        p0: Point2D,
        p1: Point2D,
        p2: Point2D,
        p3: Point2D,
        t: Float
    ): Point2D {
        val u = 1f - t
        val tt = t * t
        val uu = u * u
        val uuu = uu * u
        val ttt = tt * t

        val x = uuu * p0.x + 3 * uu * t * p1.x + 3 * u * tt * p2.x + ttt * p3.x
        val y = uuu * p0.y + 3 * uu * t * p1.y + 3 * u * tt * p2.y + ttt * p3.y
        return Point2D(x, y)
    }

    fun generateSpine(
        start: Point2D,
        control1: Point2D,
        control2: Point2D,
        end: Point2D,
        samples: Int = 30
    ): List<Point2D> {
        val points = mutableListOf<Point2D>()
        for (i in 0..samples) {
            val t = i.toFloat() / samples.toFloat()
            points.add(evaluateCubicBezier(start, control1, control2, end, t))
        }
        return points
    }

    fun applyUndulation(
        spine: List<Point2D>,
        timeSeconds: Float,
        amplitude: Float = 12f,
        frequency: Float = 2.0f
    ): List<Point2D> {
        if (spine.size < 2) return spine
        val n = spine.size
        val result = mutableListOf<Point2D>()

        for (i in 0 until n) {
            val t = i.toFloat() / (n - 1).toFloat()
            val current = spine[i]

            // Calculate local tangent
            val prev = if (i > 0) spine[i - 1] else current
            val next = if (i < n - 1) spine[i + 1] else current
            val dx = next.x - prev.x
            val dy = next.y - prev.y
            val len = sqrt(dx * dx + dy * dy).coerceAtLeast(0.001f)

            // Perpendicular normal: (-dy, dx)
            val nx = -dy / len
            val ny = dx / len

            // Taper wave amplitude at head (t=0) and tail (t=1)
            val envelope = sin(t * PI.toFloat())
            val phase = (t * 4f * PI.toFloat()) - (timeSeconds * frequency * 2f * PI.toFloat())
            val displacement = amplitude * envelope * sin(phase)

            result.add(Point2D(current.x + nx * displacement, current.y + ny * displacement))
        }

        return result
    }

    fun generateSnakeGeometry(
        spine: List<Point2D>,
        headRadius: Float = 16f,
        tailRadius: Float = 3f,
        swallowProgress: Float = -1f // -1 if no swallowed token, 0..1 along spine
    ): Realistic3DSnake {
        val n = spine.size
        val segments = mutableListOf<SnakeSegment>()
        val scales = mutableListOf<SnakeScale>()

        for (i in 0 until n) {
            val t = i.toFloat() / (n - 1).toFloat()
            val pt = spine[i]

            val prev = if (i > 0) spine[i - 1] else pt
            val next = if (i < n - 1) spine[i + 1] else pt
            val dx = next.x - prev.x
            val dy = next.y - prev.y
            val len = sqrt(dx * dx + dy * dy).coerceAtLeast(0.001f)
            val nx = -dy / len
            val ny = dx / len

            // Base radius tapering from head (t=0) down to tail (t=1)
            var r = tailRadius + (headRadius - tailRadius) * (1f - t)

            // Peristaltic token ingestion bulge
            if (swallowProgress in 0f..1f) {
                val dist = kotlin.math.abs(t - swallowProgress)
                if (dist < 0.15f) {
                    val bulgeFactor = cos((dist / 0.15f) * (PI.toFloat() / 2f))
                    r += 8f * bulgeFactor
                }
            }

            val left = Point2D(pt.x - nx * r, pt.y - ny * r)
            val right = Point2D(pt.x + nx * r, pt.y + ny * r)

            segments.add(SnakeSegment(pt, left, right, r, t))

            // Procedural dorsal scales along spine
            if (i % 2 == 0 && i < n - 1) {
                scales.add(
                    SnakeScale(
                        center = pt,
                        normal = Point2D(nx, ny),
                        size = r * 0.7f,
                        isGoldAccent = (i % 4 == 0)
                    )
                )
            }
        }

        // Generate Viper Head Geometry from first segment
        val headPt = spine.first()
        val headNext = spine[1]
        val hdx = headPt.x - headNext.x
        val hdy = headPt.y - headNext.y
        val hlen = sqrt(hdx * hdx + hdy * hdy).coerceAtLeast(0.001f)
        val fwdX = hdx / hlen
        val fwdY = hdy / hlen
        val rightX = -fwdY
        val rightY = fwdX

        val snout = Point2D(headPt.x + fwdX * (headRadius * 1.4f), headPt.y + fwdY * (headRadius * 1.4f))
        val leftJaw = Point2D(headPt.x - rightX * (headRadius * 1.1f), headPt.y - rightY * (headRadius * 1.1f))
        val rightJaw = Point2D(headPt.x + rightX * (headRadius * 1.1f), headPt.y + rightY * (headRadius * 1.1f))

        val eyeDistFwd = headRadius * 0.6f
        val eyeDistSide = headRadius * 0.75f
        val leftEye = Point2D(headPt.x + fwdX * eyeDistFwd - rightX * eyeDistSide, headPt.y + fwdY * eyeDistFwd - rightY * eyeDistSide)
        val rightEye = Point2D(headPt.x + fwdX * eyeDistFwd + rightX * eyeDistSide, headPt.y + fwdY * eyeDistFwd + rightY * eyeDistSide)

        val tongueBase = snout
        val tongueTip = Point2D(tongueBase.x + fwdX * 14f, tongueBase.y + fwdY * 14f)
        val tongueForkLeft = Point2D(tongueTip.x + fwdX * 6f - rightX * 4f, tongueTip.y + fwdY * 6f - rightY * 4f)
        val tongueForkRight = Point2D(tongueTip.x + fwdX * 6f + rightX * 4f, tongueTip.y + fwdY * 6f + rightY * 4f)

        val headGeometry = SnakeHeadGeometry(
            snout = snout,
            leftJaw = leftJaw,
            rightJaw = rightJaw,
            leftEye = leftEye,
            rightEye = rightEye,
            tongueTip = tongueTip,
            tongueForkLeft = tongueForkLeft,
            tongueForkRight = tongueForkRight
        )

        return Realistic3DSnake(
            spinePoints = spine,
            segments = segments,
            scales = scales,
            head = headGeometry,
            tail = spine.last()
        )
    }
}
