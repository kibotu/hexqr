package net.kibotu.hexqrapp.domain.parser.impl

import net.kibotu.hexqrapp.data.model.ParsedContent
import net.kibotu.hexqrapp.data.model.QrCodeFormat
import net.kibotu.hexqrapp.domain.parser.QrCodeParser

class EmailParser : QrCodeParser {
    override fun parse(rawText: String, rawBytes: ByteArray): ParsedContent? {
        if (!canParse(rawText)) return null
        
        return try {
            val parts = rawText.substring(7).split("?", limit = 3)
            val email = parts[0]
            var subject: String? = null
            var body: String? = null
            
            if (parts.size > 1) {
                val params = parts[1].split("&")
                for (param in params) {
                    val keyValue = param.split("=", limit = 2)
                    if (keyValue.size == 2) {
                        when (keyValue[0].uppercase()) {
                            "SUBJECT" -> subject = keyValue[1]
                            "BODY" -> body = keyValue[1]
                        }
                    }
                }
            }
            
            ParsedContent.EmailContent(
                email = email,
                subject = subject,
                body = body
            )
        } catch (e: Exception) {
            null
        }
    }
    
    override fun canParse(rawText: String): Boolean {
        return rawText.trim().startsWith("mailto:", ignoreCase = true)
    }
    
    override fun getFormat(): QrCodeFormat = QrCodeFormat.EMAIL
}

