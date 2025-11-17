package net.kibotu.hexqrapp.ui.screen.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun QrCodeOverlay(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "scan_line")
    
    val scanLineOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "scan_line_offset"
    )
    
    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cornerSize = 40.dp.toPx()
            val strokeWidth = 4.dp.toPx()
            val overlayColor = Color.White.copy(alpha = 0.8f)
            
            val width = size.width
            val height = size.height
            val centerX = width / 2
            val centerY = height / 2
            val boxSize = minOf(width, height) * 0.7f
            val boxLeft = centerX - boxSize / 2
            val boxTop = centerY - boxSize / 2
            val boxRight = boxLeft + boxSize
            val boxBottom = boxTop + boxSize
            
            // Draw corner brackets
            drawPath(
                path = Path().apply {
                    // Top-left corner
                    moveTo(boxLeft, boxTop + cornerSize)
                    lineTo(boxLeft, boxTop)
                    lineTo(boxLeft + cornerSize, boxTop)
                },
                color = overlayColor,
                style = Stroke(width = strokeWidth)
            )
            
            drawPath(
                path = Path().apply {
                    // Top-right corner
                    moveTo(boxRight - cornerSize, boxTop)
                    lineTo(boxRight, boxTop)
                    lineTo(boxRight, boxTop + cornerSize)
                },
                color = overlayColor,
                style = Stroke(width = strokeWidth)
            )
            
            drawPath(
                path = Path().apply {
                    // Bottom-left corner
                    moveTo(boxLeft, boxBottom - cornerSize)
                    lineTo(boxLeft, boxBottom)
                    lineTo(boxLeft + cornerSize, boxBottom)
                },
                color = overlayColor,
                style = Stroke(width = strokeWidth)
            )
            
            drawPath(
                path = Path().apply {
                    // Bottom-right corner
                    moveTo(boxRight - cornerSize, boxBottom)
                    lineTo(boxRight, boxBottom)
                    lineTo(boxRight, boxBottom - cornerSize)
                },
                color = overlayColor,
                style = Stroke(width = strokeWidth)
            )
            
            // Draw scanning line
            val scanLineY = boxTop + (boxBottom - boxTop) * scanLineOffset
            drawLine(
                color = overlayColor,
                start = Offset(boxLeft, scanLineY),
                end = Offset(boxRight, scanLineY),
                strokeWidth = strokeWidth
            )
        }
    }
}

