package game.ludora.core.designsystem.mascot

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import game.ludora.core.designsystem.component.LudoraPrimaryButton
import game.ludora.core.designsystem.theme.DeepSlate
import game.ludora.core.designsystem.theme.LudoraTheme
import game.ludora.core.designsystem.theme.SlateCard
import game.ludora.core.designsystem.theme.TextPrimary
import game.ludora.core.designsystem.theme.TextSecondary
import game.ludora.core.designsystem.theme.WarmAmberGold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MascotSelectorSheet(
    selectedMascotId: String,
    onMascotSelected: (MascotDefinition) -> Unit,
    onDismissRequest: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var currentCategory by remember { mutableStateOf<MascotCategory?>(null) }
    var previewMascot by remember { mutableStateOf(MascotCatalog.findById(selectedMascotId)) }

    val filteredMascots = remember(currentCategory) {
        if (currentCategory == null) MascotCatalog.ALL else MascotCatalog.getByCategory(currentCategory!!)
    }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = DeepSlate
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Choose Your Mascot",
                style = LudoraTheme.typography.titleLarge,
                color = TextPrimary
            )

            Text(
                text = "Tap your mascot to boop, or drag to guide their gaze!",
                style = LudoraTheme.typography.bodyMedium,
                color = TextSecondary,
                modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
            )

            // Interactive Preview Stage
            Box(
                modifier = Modifier
                    .size(130.dp)
                    .clip(CircleShape)
                    .background(SlateCard)
                    .border(2.dp, WarmAmberGold, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                LudoraMascotView(
                    mascot = previewMascot,
                    size = 110.dp
                )
            }

            Text(
                text = previewMascot.name,
                style = LudoraTheme.typography.titleMedium,
                color = WarmAmberGold,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Category Filter Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    CategoryChip(
                        title = "All (56)",
                        isSelected = currentCategory == null,
                        onClick = { currentCategory = null }
                    )
                }
                items(MascotCategory.values()) { cat ->
                    val title = when (cat) {
                        MascotCategory.ANIMALS -> "Animals (20)"
                        MascotCategory.PEOPLE -> "People (18)"
                        MascotCategory.ROBOTS_AND_THINGS -> "Robots (13)"
                        MascotCategory.SPECIAL_STYLES -> "Styles (5)"
                    }
                    CategoryChip(
                        title = title,
                        isSelected = currentCategory == cat,
                        onClick = { currentCategory = cat }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Mascot Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
            ) {
                items(filteredMascots, key = { it.id }) { mascot ->
                    val isSelected = mascot.id == previewMascot.id
                    val borderColor = if (isSelected) WarmAmberGold else LudoraTheme.colors.border

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) SlateCard else DeepSlate)
                            .border(if (isSelected) 2.dp else 1.dp, borderColor, RoundedCornerShape(12.dp))
                            .clickable { previewMascot = mascot }
                            .padding(8.dp)
                    ) {
                        LudoraMascotView(
                            mascot = mascot,
                            size = 56.dp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = mascot.name,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) TextPrimary else TextSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Confirm Button
            LudoraPrimaryButton(
                text = "Equip ${previewMascot.name}",
                onClick = {
                    onMascotSelected(previewMascot)
                    onDismissRequest()
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun CategoryChip(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bg = if (isSelected) LudoraTheme.colors.primary else SlateCard
    val textColor = if (isSelected) TextPrimary else TextSecondary
    val border = if (isSelected) LudoraTheme.colors.primary else LudoraTheme.colors.border

    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(bg)
            .border(1.dp, border, CircleShape)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        Text(
            text = title,
            color = textColor,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}
