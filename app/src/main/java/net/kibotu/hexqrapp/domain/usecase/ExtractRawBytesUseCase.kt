package net.kibotu.hexqrapp.domain.usecase

import com.google.mlkit.vision.barcode.common.Barcode
import javax.inject.Inject

class ExtractRawBytesUseCase @Inject constructor() {
    operator fun invoke(barcode: Barcode): ByteArray? {
        return barcode.rawBytes
    }
}

