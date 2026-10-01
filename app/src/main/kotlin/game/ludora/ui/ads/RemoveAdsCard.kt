package game.ludora.ui.ads

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import game.ludora.core.designsystem.theme.DeepSlate
import game.ludora.core.designsystem.theme.PrimaryIndigo
import game.ludora.core.designsystem.theme.SlateBorder
import game.ludora.core.designsystem.theme.SlateCard
import game.ludora.core.designsystem.theme.TextPrimary
import game.ludora.core.designsystem.theme.TextSecondary
import game.ludora.core.designsystem.theme.WarmAmberGold

/**
 * In-App Purchase card for the permanent "Remove Ads" pass.
 */
@Composable
fun RemoveAdsCard(
    isAdFree: Boolean,
    onPurchaseClicked: () -> Unit,
    onRestoreClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        DeepSlate,
                        if (isAdFree) Color(0xFF0F291E) else Color(0xFF1E1B4B)
                    )
                )
            )
            .border(
                1.dp,
                if (isAdFree) Color(0xFF00C853).copy(alpha = 0.5f) else SlateBorder,
                RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = if (isAdFree) "🛡️" else "✨", fontSize = 28.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (isAdFree) "Ad-Free VIP Active" else "Remove All Ads",
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isAdFree) "Permanent ad removal unlocked" else "One-time purchase • $1.99",
                            color = if (isAdFree) Color(0xFF00C853) else WarmAmberGold,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                if (!isAdFree) {
                    Button(
                        onClick = onPurchaseClicked,
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "$1.99",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF00C853).copy(alpha = 0.2f))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = "ACTIVE ✓",
                            color = Color(0xFF00C853),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                BulletPoint(text = "Permanent zero banner ads on home dashboard")
                BulletPoint(text = "Zero post-match interstitial ad interruptions")
                BulletPoint(text = "Retain optional rewarded video ads for bonus coins")
            }

            if (!isAdFree) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onRestoreClicked) {
                        Text(
                            text = "Restore Purchases",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BulletPoint(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = "•", color = WarmAmberGold, fontSize = 14.sp)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, color = TextSecondary, fontSize = 12.sp)
    }
}
