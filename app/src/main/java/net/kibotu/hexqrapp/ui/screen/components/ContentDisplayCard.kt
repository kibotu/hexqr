package net.kibotu.hexqrapp.ui.screen.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import kotlinx.coroutines.launch
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import net.kibotu.hexqrapp.data.model.QrCodeData
import net.kibotu.hexqrapp.data.model.QrCodeFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentDisplayCard(
    qrCodeData: QrCodeData?,
    onHexEditorClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    
    if (qrCodeData == null) {
        return
    }
    
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilterChip(
                    selected = true,
                    onClick = { },
                    label = { Text(formatTypeLabel(qrCodeData.format)) }
                )
                
                Row {
                    IconButton(onClick = {
                        copyToClipboard(context, qrCodeData.rawText)
                        scope.launch {
                            snackbarHostState.showSnackbar("Copied to clipboard")
                        }
                    }) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy")
                    }
                    
                    IconButton(onClick = {
                        shareContent(context, qrCodeData.rawText)
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "Share")
                    }
                    
                    TextButton(onClick = onHexEditorClick) {
                        Text("Hex Editor")
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = qrCodeData.rawText,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.fillMaxWidth(),
                maxLines = 5,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
    
    SnackbarHost(hostState = snackbarHostState)
}

private fun formatTypeLabel(format: QrCodeFormat): String {
    return when (format) {
        QrCodeFormat.URL -> "URL"
        QrCodeFormat.TEXT -> "Text"
        QrCodeFormat.VCARD -> "Contact"
        QrCodeFormat.WIFI -> "WiFi"
        QrCodeFormat.EMAIL -> "Email"
        QrCodeFormat.SMS -> "SMS"
        QrCodeFormat.GEO -> "Location"
        QrCodeFormat.CALENDAR -> "Calendar"
        QrCodeFormat.UNKNOWN -> "Unknown"
    }
}

private fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("QR Code", text)
    clipboard.setPrimaryClip(clip)
}

private fun shareContent(context: Context, text: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }
    context.startActivity(Intent.createChooser(intent, "Share QR Code"))
}

