package net.kibotu.hexqrapp.data.repository

import net.kibotu.hexqrapp.data.model.QrCodeData
import net.kibotu.hexqrapp.data.model.QrCodeFormat
import net.kibotu.hexqrapp.domain.parser.QrCodeParserFactory
import net.kibotu.hexqrapp.domain.usecase.ExtractRawBytesUseCase
import net.kibotu.hexqrapp.domain.usecase.ParseQrCodeContentUseCase
import com.google.mlkit.vision.barcode.common.Barcode
import javax.inject.Inject

class QrCodeRepository @Inject constructor(
    private val parserFactory: QrCodeParserFactory,
    private val parseQrCodeContentUseCase: ParseQrCodeContentUseCase,
    private val extractRawBytesUseCase: ExtractRawBytesUseCase
) {
    fun processBarcode(barcode: Barcode): QrCodeData {
        val rawText = barcode.rawValue ?: ""
        val rawBytes = extractRawBytesUseCase(barcode) ?: rawText.toByteArray(Charsets.UTF_8)
        val format = detectFormat(rawText)
        
        return QrCodeData(
            rawBytes = rawBytes,
            rawText = rawText,
            format = format
        )
    }
    
    fun parseContent(rawText: String, rawBytes: ByteArray) = 
        parseQrCodeContentUseCase(rawText, rawBytes)
    
    private fun detectFormat(rawText: String): QrCodeFormat {
        val parser = parserFactory.getParser(rawText)
        return parser?.getFormat() ?: QrCodeFormat.UNKNOWN
    }
}

