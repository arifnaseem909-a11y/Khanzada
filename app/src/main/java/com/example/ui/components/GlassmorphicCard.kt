package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Canvas
import com.example.ui.theme.PremiumGold

@Composable
fun ElegantDarkBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF050505))
    ) {
        // Draw the subtle background grid dots vector art simulation
        Canvas(modifier = Modifier.fillMaxSize()) {
            val dotRadius = 0.6.dp.toPx()
            // Very subtle gold dot opacity
            val dotColor = PremiumGold.copy(alpha = 0.04f)
            val spacing = 24.dp.toPx()
            val width = size.width
            val height = size.height

            var x = spacing / 2
            while (x < width) {
                var y = spacing / 2
                while (y < height) {
                    drawCircle(
                        color = dotColor,
                        radius = dotRadius,
                        center = androidx.compose.ui.geometry.Offset(x, y)
                    )
                    y += spacing
                }
                x += spacing
            }
        }
        
        content()
    }
}

@Composable
fun GlassmorphicCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 16.dp,
    borderWidth: Dp = 1.dp,
    isPremiumGold: Boolean = false,
    content: @Composable BoxScope.() -> Unit
) {
    // Elegant translucent container matching the luxury theme
    val glassBg = Color(0x12FFFFFF)
    val borderBrush = if (isPremiumGold) {
        Brush.linearGradient(
            colors = listOf(
                PremiumGold, // Gold
                PremiumGold.copy(alpha = 0.1f), // Faded Gold
                PremiumGold, // Muted Gold Light
                Color(0x11FFFFFF)
            )
        )
    } else {
        Brush.linearGradient(
            colors = listOf(
                Color(0x2BFFFFFF),
                Color(0x05FFFFFF),
                Color(0x1AFFFFFF)
            )
        )
    }

    Box(
        modifier = modifier
            .shadow(
                elevation = 10.dp,
                shape = RoundedCornerShape(cornerRadius),
                clip = false,
                ambientColor = Color.Black,
                spotColor = if (isPremiumGold) PremiumGold.copy(alpha = 0.24f) else Color.Black
            )
            .clip(RoundedCornerShape(cornerRadius))
            .background(glassBg)
            .border(borderWidth, borderBrush, RoundedCornerShape(cornerRadius))
    ) {
        content()
    }
}
