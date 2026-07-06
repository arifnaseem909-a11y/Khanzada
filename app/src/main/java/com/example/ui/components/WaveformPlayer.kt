package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.ui.theme.PremiumGold
import com.example.ui.theme.WaveformGold
import kotlinx.coroutines.delay

@Composable
fun WaveformPlayer(
    durationSec: Int,
    modifier: Modifier = Modifier,
    isSentByMe: Boolean = false
) {
    var isPlaying by remember { mutableStateOf(false) }
    var currentProgress by remember { mutableStateOf(0f) } // 0.0 to 1.0

    // Waveform bar sizes (height in dp)
    val barHeights = remember {
        listOf(
            12, 22, 16, 28, 38, 14, 24, 30, 20, 10,
            26, 32, 18, 24, 14, 28, 22, 12, 34, 18,
            12, 26, 30, 16, 24, 20, 10, 18, 24, 14
        )
    }

    // Coroutine to simulate audio playback
    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            val totalSteps = durationSec * 10
            val delayMs = 100L
            while (currentProgress < 1f && isPlaying) {
                delay(delayMs)
                currentProgress += 1f / totalSteps
            }
            if (currentProgress >= 1f) {
                isPlaying = false
                currentProgress = 0f
            }
        }
    }

    val containerBg = if (isSentByMe) Color(0x1F000000) else Color(0x15FFFFFF)
    val activeBarColor = PremiumGold
    val inactiveBarColor = if (isSentByMe) Color(0xFF94A3B8) else Color(0xFF475569)

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(containerBg)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Play/Pause Button
        IconButton(
            onClick = {
                if (currentProgress >= 1f) {
                    currentProgress = 0f
                }
                isPlaying = !isPlaying
            },
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = PremiumGold,
                contentColor = Color.Black
            ),
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = if (isPlaying) "Pause Voice Note" else "Play Voice Note",
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Waveform Bars Layout
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .clickable {
                        // Support quick scrubbing simulation
                        currentProgress = (currentProgress + 0.2f).coerceAtMost(1f)
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                barHeights.forEachIndexed { index, height ->
                    val barProgress = index.toFloat() / barHeights.size
                    val isActive = barProgress <= currentProgress

                    val color by animateColorAsState(
                        targetValue = if (isActive) activeBarColor else inactiveBarColor,
                        label = "WaveformColor"
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(height.dp)
                            .clip(RoundedCornerShape(1.dp))
                            .background(color)
                    )
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            // Duration text readout
            val currentSec = (currentProgress * durationSec).toInt()
            Text(
                text = "0:${currentSec.toString().padStart(2, '0')} / 0:${durationSec.toString().padStart(2, '0')}",
                style = MaterialTheme.typography.labelSmall,
                color = if (isSentByMe) Color(0xCCF1F5F9) else Color(0xCC94A3B8)
            )
        }
    }
}
