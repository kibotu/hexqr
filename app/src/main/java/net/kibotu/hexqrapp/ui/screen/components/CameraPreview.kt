package net.kibotu.hexqrapp.ui.screen.components

import android.content.Context
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import net.kibotu.hexqrapp.ui.screen.components.QrCodeOverlay
import java.util.concurrent.Executor

@Composable
fun CameraPreview(
    onBarcodeDetected: (com.google.mlkit.vision.barcode.common.Barcode) -> Unit,
    modifier: Modifier = Modifier,
    isScanning: Boolean = true
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    
    val previewView = remember { PreviewView(context) }
    val cameraExecutor = remember { ContextCompat.getMainExecutor(context) }
    
    LaunchedEffect(isScanning) {
        startCamera(context, previewView, lifecycleOwner, cameraExecutor, onBarcodeDetected, isScanning)
    }
    
    Box(modifier = modifier.fillMaxSize()) {
        AndroidView(
            factory = { previewView },
            modifier = Modifier.fillMaxSize()
        )
        
        if (isScanning) {
            QrCodeOverlay(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp)
            )
        }
    }
}

private suspend fun startCamera(
    context: Context,
    previewView: PreviewView,
    lifecycleOwner: LifecycleOwner,
    executor: Executor,
    onBarcodeDetected: (com.google.mlkit.vision.barcode.common.Barcode) -> Unit,
    isScanning: Boolean
) {
    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
    val cameraProvider = cameraProviderFuture.get()
    
    val preview = Preview.Builder().build().also {
        it.setSurfaceProvider(previewView.surfaceProvider)
    }
    
    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    
    val barcodeAnalyzer = BarcodeAnalyzer(onBarcodeDetected)
    
    try {
        cameraProvider.unbindAll()
        
        if (isScanning) {
            val imageAnalysis = androidx.camera.core.ImageAnalysis.Builder()
                .setBackpressureStrategy(androidx.camera.core.ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(executor, barcodeAnalyzer)
                }
            
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageAnalysis
            )
        } else {
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview
            )
        }
    } catch (e: Exception) {
        // Handle error
    }
}

