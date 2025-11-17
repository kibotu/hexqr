package net.kibotu.hexqrapp.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import net.kibotu.hexqrapp.data.model.QrCodeData
import net.kibotu.hexqrapp.ui.screen.components.HexEditorView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HexEditorScreen(
    qrCodeData: QrCodeData?,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hex Editor") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (qrCodeData == null) {
                Text(
                    text = "No QR code data available",
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                HexEditorView(
                    qrCodeData = qrCodeData,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

