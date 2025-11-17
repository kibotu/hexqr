package net.kibotu.hexqrapp.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import net.kibotu.hexqrapp.ui.screen.components.*
import net.kibotu.hexqrapp.ui.viewmodel.ScannerViewModel

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ScannerScreen(
    viewModel: ScannerViewModel = hiltViewModel(),
    onNavigateToHexEditor: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    
    val cameraPermissionState = rememberMultiplePermissionsState(
        permissions = listOf(android.Manifest.permission.CAMERA)
    )
    
    LaunchedEffect(cameraPermissionState.allPermissionsGranted) {
        viewModel.updatePermissionStatus(cameraPermissionState.allPermissionsGranted)
        if (cameraPermissionState.allPermissionsGranted) {
            viewModel.startScanning()
        }
    }
    
    when {
        !cameraPermissionState.allPermissionsGranted -> {
            PermissionRequestScreen(
                onRequestPermission = { cameraPermissionState.launchMultiplePermissionRequest() },
                onDismiss = { }
            )
        }
        uiState.showHexEditor -> {
            HexEditorScreen(
                qrCodeData = uiState.qrCodeData,
                onBack = { viewModel.toggleHexEditor() }
            )
        }
        else -> {
            ScannerContent(
                uiState = uiState,
                onBarcodeDetected = { barcode ->
                    viewModel.onBarcodeDetected(barcode)
                },
                onHexEditorClick = {
                    if (uiState.qrCodeData != null) {
                        viewModel.toggleHexEditor()
                    }
                }
            )
        }
    }
}

@Composable
private fun ScannerContent(
    uiState: net.kibotu.hexqrapp.ui.viewmodel.ScannerUiState,
    onBarcodeDetected: (com.google.mlkit.vision.barcode.common.Barcode) -> Unit,
    onHexEditorClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        CameraPreview(
            onBarcodeDetected = onBarcodeDetected,
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.5f),
            isScanning = uiState.isScanning
        )
        
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.5f)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (uiState.qrCodeData != null) {
                ContentDisplayCard(
                    qrCodeData = uiState.qrCodeData,
                    onHexEditorClick = onHexEditorClick
                )
                
                ParsedContentList(
                    items = uiState.parsedContents
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Point your camera at a QR code",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            uiState.error?.let { error ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = error,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }
    }
}

@Composable
private fun PermissionRequestScreen(
    onRequestPermission: () -> Unit,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Camera Permission Required",
                    style = MaterialTheme.typography.headlineSmall
                )
                
                Text(
                    text = "This app needs camera access to scan QR codes. Please grant camera permission to continue.",
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    
                    Button(onClick = onRequestPermission) {
                        Text("Grant Permission")
                    }
                }
            }
        }
    }
}

