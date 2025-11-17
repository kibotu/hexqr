package net.kibotu.hexqrapp.domain.parser.impl

import net.kibotu.hexqrapp.data.model.ParsedContent
import net.kibotu.hexqrapp.data.model.QrCodeFormat
import net.kibotu.hexqrapp.domain.parser.QrCodeParser
import java.net.URL

class UrlParser : QrCodeParser {
    override fun parse(rawText: String, rawBytes: ByteArray): ParsedContent? {
        return try {
            val url = URL(rawText)
            val protocol = url.protocol
            val domain = url.host
            val path = url.path.takeIf { it.isNotEmpty() }
            val fragment = url.ref
            val queryParams = url.query?.split("&")?.associate {
                val parts = it.split("=", limit = 2)
                if (parts.size == 2) {
                    parts[0] to parts[1]
                } else {
                    parts[0] to ""
                }
            }
            
            ParsedContent.UrlContent(
                url = rawText,
                protocol = protocol,
                domain = domain,
                path = path,
                queryParams = queryParams,
                fragment = fragment
            )
        } catch (e: Exception) {
            null
        }
    }
    
    override fun canParse(rawText: String): Boolean {
        return try {
            val url = URL(rawText)
            val protocol = url.protocol.lowercase()
            protocol == "http" || protocol == "https" || protocol == "ftp"
        } catch (e: Exception) {
            false
        }
    }
    
    override fun getFormat(): QrCodeFormat = QrCodeFormat.URL
}

