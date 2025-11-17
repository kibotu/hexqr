package net.kibotu.hexqrapp.ui.screen.components

import android.annotation.SuppressLint
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.TimeUnit

class BarcodeAnalyzer(
    private val onBarcodeDetected: (Barcode) -> Unit
) : ImageAnalysis.Analyzer {
    
    private val scanner = BarcodeScanning.getClient()
    private var lastAnalyzedTimeStamp = 0L
    private val minTimeBetweenScans = TimeUnit.SECONDS.toMillis(1) // Throttle to 1 scan per second
    
    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastAnalyzedTimeStamp >= minTimeBetweenScans) {
                val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                
                scanner.process(image)
                    .addOnSuccessListener { barcodes ->
                        barcodes.firstOrNull()?.let { barcode ->
                            onBarcodeDetected(barcode)
                        }
                    }
                    .addOnFailureListener {
                        // Handle error silently
                    }
                    .addOnCompleteListener {
                        imageProxy.close()
                    }
                
                lastAnalyzedTimeStamp = currentTime
            } else {
                imageProxy.close()
            }
        } else {
            imageProxy.close()
        }
    }
}

