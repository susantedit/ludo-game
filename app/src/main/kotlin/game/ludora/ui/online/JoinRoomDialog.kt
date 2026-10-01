package game.ludora.ui.online

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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import game.ludora.core.designsystem.component.LudoraCard
import game.ludora.core.designsystem.component.LudoraPrimaryButton
import game.ludora.core.designsystem.component.LudoraSecondaryButton
import game.ludora.core.designsystem.theme.LudoraTheme
import game.ludora.core.designsystem.theme.SlateCard
import game.ludora.core.designsystem.theme.TextPrimary
import game.ludora.core.designsystem.theme.TextSecondary
import game.ludora.core.designsystem.theme.WarmAmberGold
import game.ludora.core.network.util.RoomCodeGenerator

@Composable
fun JoinRoomDialog(
    onDismiss: () -> Unit,
    onJoinRoom: (String) -> Unit
) {
    var codeInput by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        LudoraCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            containerColor = LudoraTheme.colors.surfaceElevated,
            borderColor = LudoraTheme.colors.border
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Join Private Room",
                    style = LudoraTheme.typography.titleLarge,
                    color = TextPrimary
                )
                Text(
                    text = "Enter the 6-character room code",
                    style = LudoraTheme.typography.bodyMedium,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(20.dp))

                // 6-Character input box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(SlateCard)
                        .border(
                            width = 1.dp,
                            color = if (errorMessage != null) androidx.compose.ui.graphics.Color(0xFFEF4444) else WarmAmberGold,
                            shape = RoundedCornerShape(10.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    BasicTextField(
                        value = codeInput,
                        onValueChange = { input ->
                            if (input.length <= 6) {
                                codeInput = input.uppercase().filter { it.isLetterOrDigit() }
                                errorMessage = null
                            }
                        },
                        textStyle = TextStyle(
                            color = WarmAmberGold,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            textAlign = TextAlign.Center,
                            letterSpacing = 6.sp
                        ),
                        singleLine = true,
                        cursorBrush = SolidColor(WarmAmberGold),
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters),
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                    )

                    if (codeInput.isEmpty()) {
                        Text(
                            text = "CODE",
                            color = TextSecondary.copy(alpha = 0.5f),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 4.sp
                        )
                    }
                }

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = errorMessage!!,
                        color = androidx.compose.ui.graphics.Color(0xFFEF4444),
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    LudoraSecondaryButton(text = "Cancel", onClick = onDismiss)
                    Spacer(modifier = Modifier.width(12.dp))
                    LudoraPrimaryButton(
                        text = "Join",
                        enabled = codeInput.length == 6,
                        onClick = {
                            if (RoomCodeGenerator.isValid(codeInput)) {
                                onJoinRoom(codeInput)
                            } else {
                                errorMessage = "Invalid code. Avoid 0, O, 1, I."
                            }
                        }
                    )
                }
            }
        }
    }
}
