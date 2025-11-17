package net.kibotu.hexqrapp.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import net.kibotu.hexqrapp.data.model.QrCodeData
import net.kibotu.hexqrapp.ui.screen.components.HexEditorView
import net.kibotu.hexqrapp.ui.screen.components.QrCodeByteVisualization

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HexEditorScreen(
    qrCodeData: QrCodeData?,
    onBack: () -> Unit
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (qrCodeData == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "No QR code data available")
                }
            } else {
                // Tab row
                TabRow(selectedTabIndex = selectedTabIndex) {
                    Tab(
                        selected = selectedTabIndex == 0,
                        onClick = { selectedTabIndex = 0 },
                        text = { Text("Hex View") }
                    )
                    Tab(
                        selected = selectedTabIndex == 1,
                        onClick = { selectedTabIndex = 1 },
                        text = { Text("Byte Map") }
                    )
                }
                
                // Content based on selected tab
                when (selectedTabIndex) {
                    0 -> {
                        HexEditorView(
                            qrCodeData = qrCodeData,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    1 -> {
                        QrCodeByteVisualization(
                            qrCodeData = qrCodeData,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}

