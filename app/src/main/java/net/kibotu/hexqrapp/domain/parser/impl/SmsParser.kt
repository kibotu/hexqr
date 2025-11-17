package net.kibotu.hexqrapp.domain.parser.impl

import net.kibotu.hexqrapp.data.model.ParsedContent
import net.kibotu.hexqrapp.data.model.QrCodeFormat
import net.kibotu.hexqrapp.domain.parser.QrCodeParser

class SmsParser : QrCodeParser {
    override fun parse(rawText: String, rawBytes: ByteArray): ParsedContent? {
        if (!canParse(rawText)) return null
        
        return try {
            val parts = rawText.substring(4).split("?", limit = 2)
            val phoneNumber = parts[0]
            var message: String? = null
            
            if (parts.size > 1 && parts[1].startsWith("body=", ignoreCase = true)) {
                message = parts[1].substring(5)
            }
            
            ParsedContent.SmsContent(
                phoneNumber = phoneNumber,
                message = message
            )
        } catch (e: Exception) {
            null
        }
    }
    
    override fun canParse(rawText: String): Boolean {
        return rawText.trim().startsWith("sms:", ignoreCase = true) ||
               rawText.trim().startsWith("smsto:", ignoreCase = true)
    }
    
    override fun getFormat(): QrCodeFormat = QrCodeFormat.SMS
}

