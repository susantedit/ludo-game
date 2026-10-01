package game.ludora.core.designsystem.canvas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import game.ludora.core.designsystem.theme.toColor
import game.ludora.core.designsystem.theme.toDarkColor
import game.ludora.core.model.PlayerColor

/**
 * Renders 2.5D layered tokens with tactile depth and accessibility symbols directly on Canvas.
 */
object TokenRenderer {

    fun drawToken(
        drawScope: DrawScope,
        center: Offset,
        radius: Float,
        color: PlayerColor,
        isSelectable: Boolean = false,
        isLifted: Boolean = false
    ) {
        val playerColor = color.toColor()
        val darkColor = color.toDarkColor()

        // 1. Drop shadow (increases when lifted)
        val shadowOffsetY = if (isLifted) radius * 0.35f else radius * 0.15f
        val shadowAlpha = if (isLifted) 0.45f else 0.30f
        drawScope.drawCircle(
            color = Color.Black.copy(alpha = shadowAlpha),
            radius = radius * (if (isLifted) 1.08f else 0.98f),
            center = center + Offset(0f, shadowOffsetY)
        )

        // 2. Selectable pulsing highlight ring
        if (isSelectable) {
            drawScope.drawCircle(
                color = Color(0xFFFFB300), // Amber Gold
                radius = radius * 1.25f,
                center = center,
                style = Stroke(width = radius * 0.15f)
            )
        }

        // 3. High-contrast outer rim
        drawScope.drawCircle(
            color = Color.White.copy(alpha = 0.90f),
            radius = radius,
            center = center
        )

        // 4. Dark bevel ring
        drawScope.drawCircle(
            color = darkColor,
            radius = radius * 0.92f,
            center = center
        )

        // 5. Vibrant player color fill
        drawScope.drawCircle(
            color = playerColor,
            radius = radius * 0.84f,
            center = center
        )

        // 6. Specular lighting highlight (top-left)
        drawScope.drawCircle(
            color = Color.White.copy(alpha = 0.30f),
            radius = radius * 0.38f,
            center = center - Offset(radius * 0.22f, radius * 0.22f)
        )

        // 7. Center Accessibility Geometric Symbol
        drawAccessibilitySymbol(drawScope, center, radius * 0.32f, color)
    }

    private fun drawAccessibilitySymbol(
        drawScope: DrawScope,
        center: Offset,
        size: Float,
        color: PlayerColor
    ) {
        val symbolColor = Color.White
        when (color) {
            PlayerColor.RED -> {
                // Circle
                drawScope.drawCircle(
                    color = symbolColor,
                    radius = size * 0.65f,
                    center = center
                )
            }
            PlayerColor.GREEN -> {
                // Triangle
                val path = Path().apply {
                    moveTo(center.x, center.y - size)
                    lineTo(center.x - size, center.y + size * 0.7f)
                    lineTo(center.x + size, center.y + size * 0.7f)
                    close()
                }
                drawScope.drawPath(path, symbolColor)
            }
            PlayerColor.YELLOW -> {
                // Diamond
                val path = Path().apply {
                    moveTo(center.x, center.y - size)
                    lineTo(center.x + size, center.y)
                    lineTo(center.x, center.y + size)
                    lineTo(center.x - size, center.y)
                    close()
                }
                drawScope.drawPath(path, symbolColor)
            }
            PlayerColor.BLUE -> {
                // Square
                drawScope.drawRect(
                    color = symbolColor,
                    topLeft = center - Offset(size * 0.65f, size * 0.65f),
                    size = androidx.compose.ui.geometry.Size(size * 1.3f, size * 1.3f)
                )
            }
        }
    }
}
