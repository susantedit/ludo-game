package game.ludora.ui.online

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import game.ludora.core.designsystem.component.LudoraCard
import game.ludora.core.designsystem.component.LudoraPrimaryButton
import game.ludora.core.designsystem.component.LudoraSecondaryButton
import game.ludora.core.designsystem.component.PlayerAvatarBadge
import game.ludora.core.designsystem.theme.LudoraTheme
import game.ludora.core.designsystem.theme.SlateCard
import game.ludora.core.designsystem.theme.TextPrimary
import game.ludora.core.designsystem.theme.TextSecondary
import game.ludora.core.designsystem.theme.WarmAmberGold
import game.ludora.core.network.model.RoomDetails
import game.ludora.core.network.model.RoomPlayer

@Composable
fun OnlineLobbyScreen(
    room: RoomDetails,
    currentUserId: String,
    onToggleReady: () -> Unit,
    onStartMatch: () -> Unit,
    onLeaveRoom: () -> Unit
) {
    val context = LocalContext.current
    val isHost = room.players.firstOrNull { it.id == currentUserId }?.isHost == true
    val myPlayer = room.players.firstOrNull { it.id == currentUserId }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LudoraTheme.colors.background)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Top bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            LudoraSecondaryButton(
                text = "← Leave Room",
                onClick = onLeaveRoom,
                modifier = Modifier.height(36.dp)
            )

            Text(
                text = if (isHost) "Room Host 👑" else "Room Guest",
                style = LudoraTheme.typography.titleSmall,
                color = WarmAmberGold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Room Code Display Card
        LudoraCard(
            modifier = Modifier.fillMaxWidth(),
            containerColor = LudoraTheme.colors.surfaceElevated,
            borderColor = WarmAmberGold
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "ROOM CODE",
                    style = LudoraTheme.typography.bodyMedium,
                    color = TextSecondary,
                    letterSpacing = 2.sp,
                    fontSize = 11.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = room.roomCode,
                        color = WarmAmberGold,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 6.sp
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(SlateCard)
                            .clickable {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(ClipData.newPlainText("Room Code", room.roomCode))
                                Toast.makeText(context, "Code copied to clipboard!", Toast.LENGTH_SHORT).show()
                            }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(text = "Copy 📋", color = TextPrimary, fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "${room.config.gameType.name.replace('_', ' ')} • ${room.config.turnTimeoutSeconds}s Turn Timer",
                    style = LudoraTheme.typography.bodyMedium,
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Player Roster Header
        Text(
            text = "Players (${room.players.size}/${room.config.maxPlayers})",
            style = LudoraTheme.typography.titleMedium,
            color = TextPrimary,
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        )

        // Slots
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            for (seat in 0 until room.config.maxPlayers) {
                val player = room.players.firstOrNull { it.seatIndex == seat }
                if (player != null) {
                    PlayerRosterRow(player = player)
                } else {
                    EmptyRosterRow(seatIndex = seat)
                }
            }
        }

        // Bottom Actions
        if (isHost) {
            LudoraPrimaryButton(
                text = "Start Match",
                onClick = onStartMatch,
                enabled = room.canStart,
                modifier = Modifier.fillMaxWidth().height(50.dp)
            )
            if (!room.canStart) {
                Text(
                    text = if (room.players.size < 2) "Waiting for opponents to join..." else "Waiting for players to toggle Ready...",
                    style = LudoraTheme.typography.bodyMedium,
                    color = TextSecondary,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
        } else {
            val isReady = myPlayer?.isReady == true
            LudoraPrimaryButton(
                text = if (isReady) "Cancel Ready" else "Ready Up! ✓",
                onClick = onToggleReady,
                modifier = Modifier.fillMaxWidth().height(50.dp)
            )
        }
    }
}

@Composable
private fun PlayerRosterRow(player: RoomPlayer) {
    LudoraCard(
        modifier = Modifier.fillMaxWidth(),
        containerColor = SlateCard,
        borderColor = if (player.isReady || player.isHost) WarmAmberGold else LudoraTheme.colors.border
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                PlayerAvatarBadge(
                    color = player.color,
                    isCurrentTurn = false,
                    size = 36.dp
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = player.displayName,
                        style = LudoraTheme.typography.titleSmall,
                        color = TextPrimary
                    )
                    Text(
                        text = if (player.isHost) "Lobby Leader" else "Seat #${player.seatIndex + 1}",
                        style = LudoraTheme.typography.bodyMedium,
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (player.isHost || player.isReady) Color(0xFF16A34A).copy(alpha = 0.25f) else Color(0xFF334155))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = if (player.isHost) "HOST 👑" else if (player.isReady) "READY ✓" else "WAITING...",
                    color = if (player.isHost || player.isReady) Color(0xFF4ADE80) else TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun EmptyRosterRow(seatIndex: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF0F172A).copy(alpha = 0.5f))
            .border(1.dp, Color(0xFF334155), RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = "Slot #${seatIndex + 1}: Waiting for player to join...",
            color = TextSecondary.copy(alpha = 0.6f),
            fontSize = 13.sp
        )
    }
}
