package game.ludora.core.designsystem.board

import game.ludora.core.designsystem.dice.Point2D

data class PrayerFlagTassel(
    val anchor: Point2D,
    val colors: List<Long>, // 0xFFRRGGBB color hex values
    val length: Float
)

data class StupaLocation(
    val center: Point2D,
    val tierRadius: List<Float>,
    val spireHeight: Float
)

data class SwayambhunathEyesMedallion(
    val center: Point2D,
    val outerRadius: Float,
    val innerGoldRadius: Float,
    val leftEyeCenter: Point2D,
    val rightEyeCenter: Point2D,
    val unityNoseGimbal: Point2D
)

data class KathmanduBoardLayout(
    val boardSize: Float,
    val borderThickness: Float,
    val centerMedallion: SwayambhunathEyesMedallion,
    val cornerTassels: List<PrayerFlagTassel>,
    val stupas: List<StupaLocation>
)

/**
 * Kathmandu Cultural Board Renderer.
 * Generates geometry and coordinates for carved dark walnut borders,
 * ancient Tibetan mandala stone patterns, sacred 5-color prayer flag tassels,
 * miniature brass stupas, and the central Swayambhunath wisdom eyes medallion.
 */
class KathmanduBoardRenderer {

    // Five sacred prayer flag colors: Blue (Sky), White (Air), Red (Fire), Green (Water), Yellow (Earth)
    val sacredFlagColors = listOf(
        0xFF0066CCL, // Blue
        0xFFEEEEFFL, // White
        0xFFCC2222L, // Red
        0xFF008844L, // Green
        0xFFFFCC00L  // Yellow
    )

    fun computeLayout(boardSize: Float): KathmanduBoardLayout {
        val center = Point2D(boardSize / 2f, boardSize / 2f)
        val borderThickness = boardSize * 0.05f
        val centerMedallionRadius = boardSize * 0.16f

        // Central Swayambhunath wisdom eyes medallion
        val eyeSpacing = centerMedallionRadius * 0.42f
        val eyeElevation = centerMedallionRadius * 0.12f
        val medallion = SwayambhunathEyesMedallion(
            center = center,
            outerRadius = centerMedallionRadius,
            innerGoldRadius = centerMedallionRadius * 0.88f,
            leftEyeCenter = Point2D(center.x - eyeSpacing, center.y - eyeElevation),
            rightEyeCenter = Point2D(center.x + eyeSpacing, center.y - eyeElevation),
            unityNoseGimbal = Point2D(center.x, center.y + centerMedallionRadius * 0.28f)
        )

        // Corner prayer flag tassels at four outer corners
        val inset = borderThickness * 0.5f
        val corners = listOf(
            Point2D(inset, inset),
            Point2D(boardSize - inset, inset),
            Point2D(boardSize - inset, boardSize - inset),
            Point2D(inset, boardSize - inset)
        )
        val tassels = corners.map { pt ->
            PrayerFlagTassel(
                anchor = pt,
                colors = sacredFlagColors,
                length = boardSize * 0.08f
            )
        }

        // Four miniature brass stupas at the player home corners
        val homeOffset = boardSize * 0.22f
        val stupas = listOf(
            Point2D(homeOffset, homeOffset),
            Point2D(boardSize - homeOffset, homeOffset),
            Point2D(boardSize - homeOffset, boardSize - homeOffset),
            Point2D(homeOffset, boardSize - homeOffset)
        ).map { pt ->
            StupaLocation(
                center = pt,
                tierRadius = listOf(boardSize * 0.045f, boardSize * 0.032f, boardSize * 0.020f),
                spireHeight = boardSize * 0.035f
            )
        }

        return KathmanduBoardLayout(
            boardSize = boardSize,
            borderThickness = borderThickness,
            centerMedallion = medallion,
            cornerTassels = tassels,
            stupas = stupas
        )
    }
}
