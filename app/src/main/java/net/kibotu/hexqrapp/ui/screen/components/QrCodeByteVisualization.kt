package net.kibotu.hexqrapp.ui.screen.components

import android.graphics.Bitmap
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import net.kibotu.hexqrapp.data.model.QrCodeData
import java.util.EnumMap

/**
 * Visualizes QR code with byte position mapping.
 * Shows where each byte is located on the QR code grid.
 */
@Composable
fun QrCodeByteVisualization(
    qrCodeData: QrCodeData,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val qrBitmap = remember(qrCodeData.rawText, qrCodeData.rawBytes) {
        generateQrCodeBitmap(qrCodeData.rawText, 512)
    }
    
    var selectedByteIndex by remember { mutableStateOf<Int?>(null) }
    var showByteLabels by remember { mutableStateOf(true) }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header with controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "QR Code Byte Map",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = showByteLabels,
                    onClick = { showByteLabels = !showByteLabels },
                    label = { Text("Show Labels") }
                )
            }
        }
        
        // Info card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Total Bytes: ${qrCodeData.rawBytes.size}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Tap on the QR code to see byte positions",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        // QR Code visualization
        if (qrBitmap != null) {
            var containerSize by remember { mutableStateOf(androidx.compose.ui.geometry.Size.Zero) }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .background(Color.White)
                    .onSizeChanged { layoutSize ->
                        containerSize = androidx.compose.ui.geometry.Size(
                            layoutSize.width.toFloat(),
                            layoutSize.height.toFloat()
                        )
                    }
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(qrBitmap.width, qrCodeData.rawBytes.size, containerSize.width, containerSize.height) {
                            detectTapGestures { tapOffset ->
                                val width = containerSize.width
                                val height = containerSize.height
                                if (width > 0 && height > 0) {
                                    val qrSize = width.coerceAtMost(height)
                                    val moduleSize = qrSize / qrBitmap.width
                                    val offsetX = (width - qrSize) / 2
                                    val offsetY = (height - qrSize) / 2
                                    
                                    val relativeX = tapOffset.x - offsetX
                                    val relativeY = tapOffset.y - offsetY
                                    
                                    if (relativeX >= 0 && relativeY >= 0 && 
                                        relativeX < qrSize && relativeY < qrSize) {
                                        val x = (relativeX / moduleSize).toInt()
                                        val y = (relativeY / moduleSize).toInt()
                                        
                                        if (x >= 0 && x < qrBitmap.width && y >= 0 && y < qrBitmap.width) {
                                            // Calculate approximate byte index based on position
                                            val byteIndex = calculateByteIndexFromPosition(
                                                x, y, qrBitmap.width, qrCodeData.rawBytes.size
                                            )
                                            selectedByteIndex = byteIndex
                                        }
                                    }
                                }
                            }
                        }
                ) {
                    drawQrCodeWithByteOverlay(
                        qrBitmap = qrBitmap,
                        bytes = qrCodeData.rawBytes,
                        selectedByteIndex = selectedByteIndex,
                        showByteLabels = showByteLabels
                    )
                }
            }
            
            // Selected byte info
            selectedByteIndex?.let { index ->
                if (index >= 0 && index < qrCodeData.rawBytes.size) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Byte at position: $index",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Hex: ${"%02X".format(qrCodeData.rawBytes[index])}",
                                style = MaterialTheme.typography.bodyLarge,
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                            )
                            Text(
                                text = "Decimal: ${qrCodeData.rawBytes[index].toInt() and 0xFF}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "ASCII: ${if (qrCodeData.rawBytes[index].toInt() in 32..126) qrCodeData.rawBytes[index].toInt().toChar() else '.'}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        } else {
            Text(
                text = "Unable to generate QR code visualization",
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

/**
 * Generates a QR code bitmap from the given text.
 */
private fun generateQrCodeBitmap(text: String, size: Int): Bitmap? {
    return try {
        val hints = EnumMap<EncodeHintType, Any>(EncodeHintType::class.java)
        hints[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.M
        hints[EncodeHintType.CHARACTER_SET] = "UTF-8"
        hints[EncodeHintType.MARGIN] = 1
        
        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, size, size, hints)
        
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565)
        for (x in 0 until size) {
            for (y in 0 until size) {
                bitmap.setPixel(x, y, if (bitMatrix[x, y]) 0xFF000000.toInt() else 0xFFFFFFFF.toInt())
            }
        }
        bitmap
    } catch (e: Exception) {
        null
    }
}

/**
 * Draws QR code with byte position overlay.
 */
private fun DrawScope.drawQrCodeWithByteOverlay(
    qrBitmap: Bitmap,
    bytes: ByteArray,
    selectedByteIndex: Int?,
    showByteLabels: Boolean
) {
    val qrSize = size.width.coerceAtMost(size.height)
    val moduleSize = qrSize / qrBitmap.width
    val offsetX = (size.width - qrSize) / 2
    val offsetY = (size.height - qrSize) / 2
    
    // Draw QR code base
    val imageBitmap = qrBitmap.asImageBitmap()
    // drawImage requires IntOffset and IntSize for dstOffset and dstSize
    drawImage(
        image = imageBitmap,
        dstOffset = IntOffset(offsetX.toInt(), offsetY.toInt()),
        dstSize = IntSize(qrSize.toInt(), qrSize.toInt())
    )
    
    // Calculate byte-to-position mapping
    val bytePositions = calculateBytePositions(qrBitmap.width, bytes.size)
    
    // Draw byte overlays
    bytePositions.forEachIndexed { byteIndex, positions ->
        val isSelected = selectedByteIndex == byteIndex
        
        positions.forEachIndexed { positionIndex, (x, y) ->
            val moduleX = offsetX + x * moduleSize
            val moduleY = offsetY + y * moduleSize
            
            // Highlight selected byte
            if (isSelected) {
                drawRect(
                    color = Color(0x6600FF00), // Semi-transparent green
                    topLeft = Offset(moduleX, moduleY),
                    size = Size(moduleSize, moduleSize)
                )
            } else {
                // Color code by byte index (for visual grouping)
                val hue = (byteIndex * 360f / bytes.size) % 360f
                val overlayColor = Color.hsv(hue, 0.3f, 0.3f).copy(alpha = 0.2f)
                drawRect(
                    color = overlayColor,
                    topLeft = Offset(moduleX, moduleY),
                    size = Size(moduleSize, moduleSize)
                )
            }
            
            // Note: Text labels are disabled for now due to Canvas API complexity
            // The color overlays still provide visual indication of byte positions
        }
    }
}

/**
 * Calculates approximate byte positions on QR code grid.
 * Note: This is an approximation since exact mapping requires full QR code encoding knowledge.
 */
private fun calculateBytePositions(
    qrSize: Int,
    totalBytes: Int
): List<List<Pair<Int, Int>>> {
    val positions = mutableListOf<List<Pair<Int, Int>>>()
    
    // QR codes encode data in codewords (8 bits = 1 byte)
    // Data is placed in a zigzag pattern, avoiding functional patterns
    // This is a simplified approximation
    
    val modulesPerByte = 8 // Each byte is 8 bits
    val functionalPatternSize = 21 // Finder patterns + timing patterns (approximate)
    val dataStartX = functionalPatternSize
    val dataStartY = functionalPatternSize
    val dataEndX = qrSize - functionalPatternSize
    val dataEndY = qrSize - functionalPatternSize
    
    var byteIndex = 0
    var currentX = dataStartX
    var currentY = dataStartY
    var goingUp = true
    
    while (byteIndex < totalBytes && currentY < dataEndY) {
        val bytePositions = mutableListOf<Pair<Int, Int>>()
        
        // Each byte occupies 8 modules (bits)
        for (bit in 0 until modulesPerByte) {
            if (currentX >= dataStartX && currentX < dataEndX && 
                currentY >= dataStartY && currentY < dataEndY) {
                bytePositions.add(Pair(currentX, currentY))
            }
            
            // Move to next position (zigzag pattern)
            currentX++
            if (currentX >= dataEndX) {
                currentX = dataStartX
                if (goingUp) {
                    currentY--
                } else {
                    currentY++
                }
                goingUp = !goingUp
            }
        }
        
        if (bytePositions.isNotEmpty()) {
            positions.add(bytePositions)
            byteIndex++
        } else {
            break
        }
    }
    
    return positions
}

/**
 * Calculates approximate byte index from tap position.
 */
private fun calculateByteIndexFromPosition(
    x: Int,
    y: Int,
    qrSize: Int,
    totalBytes: Int
): Int {
    // Simplified calculation - find closest byte
    val functionalPatternSize = 21
    if (x < functionalPatternSize || y < functionalPatternSize ||
        x >= qrSize - functionalPatternSize || y >= qrSize - functionalPatternSize) {
        return -1 // Functional pattern area
    }
    
    val dataArea = (qrSize - functionalPatternSize * 2) * (qrSize - functionalPatternSize * 2)
    val modulesPerByte = 8
    val totalDataModules = totalBytes * modulesPerByte
    
    val relativeX = x - functionalPatternSize
    val relativeY = y - functionalPatternSize
    val dataWidth = qrSize - functionalPatternSize * 2
    
    val moduleIndex = relativeY * dataWidth + relativeX
    val byteIndex = (moduleIndex / modulesPerByte).coerceIn(0, totalBytes - 1)
    
    return byteIndex
}

