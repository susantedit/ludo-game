package game.ludora

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Color tokens from docs/14_DESIGN_SYSTEM.md
val ObsidianBlack = Color(0xFF0B0F19)
val DeepSlate = Color(0xFF0F172A)
val PrimaryIndigo = Color(0xFF3D5AFE)
val WarmAmberGold = Color(0xFFFFB300)

private val LudoraColorScheme = darkColorScheme(
    primary = PrimaryIndigo,
    secondary = WarmAmberGold,
    background = ObsidianBlack,
    surface = DeepSlate
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme(colorScheme = LudoraColorScheme) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = ObsidianBlack
                ) { innerPadding ->
                    LudoraRootScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun LudoraRootScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBlack),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Ludora\nWhere every roll matters.",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 32.sp
        )
    }
}
