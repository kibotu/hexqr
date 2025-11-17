package net.kibotu.hexqrapp.data.model

data class QrCodeData(
    val rawBytes: ByteArray,
    val rawText: String,
    val format: QrCodeFormat,
    val timestamp: Long = System.currentTimeMillis()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as QrCodeData

        if (!rawBytes.contentEquals(other.rawBytes)) return false
        if (rawText != other.rawText) return false
        if (format != other.format) return false
        if (timestamp != other.timestamp) return false

        return true
    }

    override fun hashCode(): Int {
        var result = rawBytes.contentHashCode()
        result = 31 * result + rawText.hashCode()
        result = 31 * result + format.hashCode()
        result = 31 * result + timestamp.hashCode()
        return result
    }
}

