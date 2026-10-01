package game.ludora.core.designsystem.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

@Immutable
data class LudoraShapeTokens(
    val card: Shape = RoundedCornerShape(16.dp),
    val button: Shape = RoundedCornerShape(12.dp),
    val chip: Shape = CircleShape,
    val token: Shape = CircleShape,
    val dice: Shape = RoundedCornerShape(12.dp),
    val dialog: Shape = RoundedCornerShape(20.dp)
)

val MaterialShapes = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(20.dp)
)
