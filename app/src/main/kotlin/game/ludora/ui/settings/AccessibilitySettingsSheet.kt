package game.ludora.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import game.ludora.core.designsystem.theme.DeepSlate
import game.ludora.core.designsystem.theme.LudoraTheme
import game.ludora.core.designsystem.theme.PrimaryIndigo
import game.ludora.core.designsystem.theme.SlateBorder
import game.ludora.core.designsystem.theme.SlateCard
import game.ludora.core.designsystem.theme.TextPrimary
import game.ludora.core.designsystem.theme.TextSecondary
import game.ludora.core.designsystem.theme.WarmAmberGold
import game.ludora.core.model.AccessibilityConfig
import game.ludora.core.model.ColorBlindMode

/**
 * Bottom sheet allowing players to configure accessibility, audio, haptics, and motion settings.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccessibilitySettingsSheet(
    config: AccessibilityConfig,
    onConfigUpdated: (AccessibilityConfig) -> Unit,
    onDismissRequest: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = LudoraTheme.colors.surfaceElevated,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .padding(20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "⚙️", fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Accessibility & Audio",
                            style = LudoraTheme.typography.titleMedium,
                            color = TextPrimary
                        )
                        Text(
                            text = "Fine-tune contrast, motion, sound and haptics",
                            style = LudoraTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(SlateCard)
                        .clickable { onDismissRequest() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "✕", color = TextSecondary, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 1. Color Blind Palette Selection
            Text(
                text = "COLOR-BLIND PALETTE",
                color = WarmAmberGold,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ColorBlindMode.entries.forEach { mode ->
                    val isSelected = config.colorBlindMode == mode
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) PrimaryIndigo else DeepSlate)
                            .border(1.dp, if (isSelected) Color.White.copy(alpha = 0.6f) else SlateBorder, RoundedCornerShape(8.dp))
                            .clickable { onConfigUpdated(config.copy(colorBlindMode = mode)) }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = mode.name.take(4),
                            color = if (isSelected) Color.White else TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 2. Reduced Motion Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(DeepSlate)
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Reduced Motion",
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Disables particle bursts & shortens tile hops",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
                Switch(
                    checked = config.reducedMotion,
                    onCheckedChange = { onConfigUpdated(config.copy(reducedMotion = it)) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = PrimaryIndigo
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 3. Shape Overlays Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(DeepSlate)
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Geometric Shape Markers",
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Displays Circle, Triangle, Diamond, Square on pieces",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
                Switch(
                    checked = config.showShapeOverlays,
                    onCheckedChange = { onConfigUpdated(config.copy(showShapeOverlays = it)) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = PrimaryIndigo
                    )
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 4. Sound Effects Volume
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "SOUND EFFECTS",
                    color = WarmAmberGold,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${(config.soundVolume * 100).toInt()}%",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }
            Slider(
                value = if (config.soundEnabled) config.soundVolume else 0f,
                onValueChange = { onConfigUpdated(config.copy(soundVolume = it, soundEnabled = it > 0f)) },
                valueRange = 0f..1f,
                colors = SliderDefaults.colors(
                    thumbColor = WarmAmberGold,
                    activeTrackColor = WarmAmberGold,
                    inactiveTrackColor = SlateCard
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 5. Haptic Feedback Intensity
            Text(
                text = "HAPTIC FEEDBACK",
                color = WarmAmberGold,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("OFF", "LIGHT", "MEDIUM", "STRONG").forEach { intensity ->
                    val isSelected = config.hapticIntensity == intensity
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) PrimaryIndigo else DeepSlate)
                            .border(1.dp, if (isSelected) Color.White.copy(alpha = 0.6f) else SlateBorder, RoundedCornerShape(8.dp))
                            .clickable { onConfigUpdated(config.copy(hapticIntensity = intensity)) }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = intensity,
                            color = if (isSelected) Color.White else TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
