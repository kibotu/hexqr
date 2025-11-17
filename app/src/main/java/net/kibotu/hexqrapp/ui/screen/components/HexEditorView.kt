package net.kibotu.hexqrapp.ui.screen.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import net.kibotu.hexqrapp.data.model.QrCodeData
import kotlinx.coroutines.launch

@Composable
fun HexEditorView(
    qrCodeData: QrCodeData?,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    
    if (qrCodeData == null) {
        return
    }
    
    val hexLines = formatHexData(qrCodeData.rawBytes)
    
    Column(modifier = modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Hex Editor",
                style = MaterialTheme.typography.titleLarge
            )
            
            IconButton(onClick = {
                val hexString = qrCodeData.rawBytes.joinToString(" ") { 
                    "%02X".format(it)
                }
                copyToClipboard(context, hexString)
                scope.launch {
                    snackbarHostState.showSnackbar("Hex data copied to clipboard")
                }
            }) {
                Icon(Icons.Default.ContentCopy, contentDescription = "Copy Hex")
            }
        }
        
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Offset",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(0.2f)
                    )
                    Text(
                        text = "Hex Data",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(0.6f)
                    )
                    Text(
                        text = "ASCII",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(0.2f)
                    )
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            }
            
            itemsIndexed(hexLines) { index, line ->
                HexEditorRow(
                    offset = line.offset,
                    hexData = line.hexData,
                    ascii = line.ascii
                )
            }
        }
    }
    
    SnackbarHost(hostState = snackbarHostState)
}

private data class HexLine(
    val offset: String,
    val hexData: String,
    val ascii: String
)

private fun formatHexData(bytes: ByteArray): List<HexLine> {
    val lines = mutableListOf<HexLine>()
    val bytesPerLine = 16
    
    for (i in bytes.indices step bytesPerLine) {
        val endIndex = minOf(i + bytesPerLine, bytes.size)
        val lineBytes = bytes.sliceArray(i until endIndex)
        
        val offset = "%08X".format(i)
        val hexData = lineBytes.joinToString(" ") { "%02X".format(it) }
            .padEnd(bytesPerLine * 3 - 1)
        val ascii = lineBytes.map { byte ->
            if (byte.toInt() in 32..126) byte.toInt().toChar() else '.'
        }.joinToString("")
        
        lines.add(HexLine(offset, hexData, ascii))
    }
    
    return lines
}

@Composable
private fun HexEditorRow(
    offset: String,
    hexData: String,
    ascii: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = offset,
            style = MaterialTheme.typography.bodySmall,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.weight(0.2f)
        )
        Text(
            text = hexData,
            style = MaterialTheme.typography.bodySmall,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.weight(0.6f)
        )
        Text(
            text = ascii,
            style = MaterialTheme.typography.bodySmall,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.weight(0.2f)
        )
    }
}

private fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("Hex Data", text)
    clipboard.setPrimaryClip(clip)
}

