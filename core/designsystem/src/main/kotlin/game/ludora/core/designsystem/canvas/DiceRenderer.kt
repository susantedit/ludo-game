package game.ludora.core.designsystem.canvas

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope

/**
 * Pure Canvas renderer for a tactile cubic die face with recessed pip dots.
 */
object DiceRenderer {

    fun drawDiceFace(
        drawScope: DrawScope,
        topLeft: Offset,
        size: Float,
        value: Int,
        diceColor: Color = Color.White,
        pipColor: Color = Color(0xFF0F172A), // Deep Slate
        cornerRadius: Float = size * 0.18f
    ) {
        // 1. Soft drop shadow
        drawScope.drawRoundRect(
            color = Color.Black.copy(alpha = 0.28f),
            topLeft = topLeft + Offset(0f, size * 0.08f),
            size = Size(size, size),
            cornerRadius = CornerRadius(cornerRadius, cornerRadius)
        )

        // 2. Main cubic die face
        drawScope.drawRoundRect(
            color = diceColor,
            topLeft = topLeft,
            size = Size(size, size),
            cornerRadius = CornerRadius(cornerRadius, cornerRadius)
        )

        // 3. Subtle inset bevel stroke
        drawScope.drawRoundRect(
            color = Color(0xFFE2E8F0), // Slate 200 border
            topLeft = topLeft,
            size = Size(size, size),
            cornerRadius = CornerRadius(cornerRadius, cornerRadius),
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = size * 0.03f)
        )

        // 4. Pip dots (values 1..6)
        if (value in 1..6) {
            val pipRadius = size * 0.09f
            val pips = getPipNormalizedOffsets(value)

            for ((normX, normY) in pips) {
                val pipCenter = Offset(
                    x = topLeft.x + normX * size,
                    y = topLeft.y + normY * size
                )
                // Recessed inner shadow on pip
                drawScope.drawCircle(
                    color = Color.Black.copy(alpha = 0.15f),
                    radius = pipRadius * 1.15f,
                    center = pipCenter + Offset(0f, size * 0.015f)
                )
                // Pip dot
                drawScope.drawCircle(
                    color = pipColor,
                    radius = pipRadius,
                    center = pipCenter
                )
            }
        }
    }

    private fun getPipNormalizedOffsets(value: Int): List<Pair<Float, Float>> {
        val center = Pair(0.5f, 0.5f)
        val tl = Pair(0.28f, 0.28f)
        val br = Pair(0.72f, 0.72f)
        val tr = Pair(0.72f, 0.28f)
        val bl = Pair(0.28f, 0.72f)
        val ml = Pair(0.28f, 0.5f)
        val mr = Pair(0.72f, 0.5f)

        return when (value) {
            1 -> listOf(center)
            2 -> listOf(tl, br)
            3 -> listOf(tl, center, br)
            4 -> listOf(tl, tr, bl, br)
            5 -> listOf(tl, tr, center, bl, br)
            6 -> listOf(tl, tr, ml, mr, bl, br)
            else -> emptyList()
        }
    }
}
