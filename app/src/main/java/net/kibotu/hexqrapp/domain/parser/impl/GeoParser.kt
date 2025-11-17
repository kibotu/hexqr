package net.kibotu.hexqrapp.domain.parser.impl

import net.kibotu.hexqrapp.data.model.ParsedContent
import net.kibotu.hexqrapp.data.model.QrCodeFormat
import net.kibotu.hexqrapp.domain.parser.QrCodeParser

class GeoParser : QrCodeParser {
    override fun parse(rawText: String, rawBytes: ByteArray): ParsedContent? {
        if (!canParse(rawText)) return null
        
        return try {
            val geoIndex = rawText.indexOf("geo:", ignoreCase = true)
            val geoPart = if (geoIndex >= 0) rawText.substring(geoIndex + 4) else rawText
            val parts = geoPart.split(",")
            
            if (parts.size >= 2) {
                val latitude = parts[0].toDouble()
                val longitude = parts[1].toDouble()
                val altitude = if (parts.size >= 3) parts[2].toDoubleOrNull() else null
                
                ParsedContent.GeoContent(
                    latitude = latitude,
                    longitude = longitude,
                    altitude = altitude
                )
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
    
    override fun canParse(rawText: String): Boolean {
        return rawText.trim().startsWith("geo:", ignoreCase = true)
    }
    
    override fun getFormat(): QrCodeFormat = QrCodeFormat.GEO
}

