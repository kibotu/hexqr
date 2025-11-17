package net.kibotu.hexqrapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import net.kibotu.hexqrapp.data.model.ParsedContentItem
import net.kibotu.hexqrapp.data.model.QrCodeData
import net.kibotu.hexqrapp.data.repository.QrCodeRepository
import com.google.mlkit.vision.barcode.common.Barcode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ScannerUiState(
    val isScanning: Boolean = false,
    val qrCodeData: QrCodeData? = null,
    val parsedContents: List<ParsedContentItem> = emptyList(),
    val showHexEditor: Boolean = false,
    val error: String? = null,
    val hasPermission: Boolean = false
)

@HiltViewModel
class ScannerViewModel @Inject constructor(
    private val repository: QrCodeRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ScannerUiState())
    val uiState: StateFlow<ScannerUiState> = _uiState.asStateFlow()
    
    fun updatePermissionStatus(hasPermission: Boolean) {
        _uiState.value = _uiState.value.copy(hasPermission = hasPermission)
    }
    
    fun startScanning() {
        _uiState.value = _uiState.value.copy(isScanning = true, error = null)
    }
    
    fun stopScanning() {
        _uiState.value = _uiState.value.copy(isScanning = false)
    }
    
    fun onBarcodeDetected(barcode: Barcode) {
        viewModelScope.launch {
            try {
                val qrCodeData = repository.processBarcode(barcode)
                val parsedContents = repository.parseContent(qrCodeData.rawText, qrCodeData.rawBytes)
                
                _uiState.value = _uiState.value.copy(
                    qrCodeData = qrCodeData,
                    parsedContents = parsedContents,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Error processing QR code"
                )
            }
        }
    }
    
    fun toggleHexEditor() {
        _uiState.value = _uiState.value.copy(
            showHexEditor = !_uiState.value.showHexEditor
        )
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    fun clearQrCodeData() {
        _uiState.value = _uiState.value.copy(
            qrCodeData = null,
            parsedContents = emptyList()
        )
    }
}

