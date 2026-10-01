package game.ludora.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryIndigo,
    onPrimary = TextPrimary,
    primaryContainer = PrimaryContainer,
    onPrimaryContainer = TextPrimary,
    secondary = WarmAmberGold,
    onSecondary = ObsidianBlack,
    background = ObsidianBlack,
    onBackground = TextPrimary,
    surface = DeepSlate,
    onSurface = TextPrimary,
    surfaceVariant = SlateCard,
    onSurfaceVariant = TextSecondary,
    outline = SlateBorder
)

val LocalLudoraColors = staticCompositionLocalOf { LudoraColorTokens() }
val LocalLudoraTypography = staticCompositionLocalOf { LudoraTypographyTokens() }
val LocalLudoraShapes = staticCompositionLocalOf { LudoraShapeTokens() }

object LudoraTheme {
    val colors: LudoraColorTokens
        @Composable
        @ReadOnlyComposable
        get() = LocalLudoraColors.current

    val typography: LudoraTypographyTokens
        @Composable
        @ReadOnlyComposable
        get() = LocalLudoraTypography.current

    val shapes: LudoraShapeTokens
        @Composable
        @ReadOnlyComposable
        get() = LocalLudoraShapes.current
}

@Composable
fun LudoraTheme(
    darkTheme: Boolean = true, // Default to true per Ludora specification
    content: @Composable () -> Unit
) {
    val colorTokens = LudoraColorTokens()
    val typographyTokens = LudoraTypographyTokens()
    val shapeTokens = LudoraShapeTokens()

    CompositionLocalProvider(
        LocalLudoraColors provides colorTokens,
        LocalLudoraTypography provides typographyTokens,
        LocalLudoraShapes provides shapeTokens
    ) {
        MaterialTheme(
            colorScheme = DarkColorScheme,
            typography = MaterialTypography,
            shapes = MaterialShapes,
            content = content
        )
    }
}
