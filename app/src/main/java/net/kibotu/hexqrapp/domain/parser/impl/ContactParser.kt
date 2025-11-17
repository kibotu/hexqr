package net.kibotu.hexqrapp.domain.parser.impl

import net.kibotu.hexqrapp.data.model.ParsedContent
import net.kibotu.hexqrapp.data.model.QrCodeFormat
import net.kibotu.hexqrapp.domain.parser.QrCodeParser

class ContactParser : QrCodeParser {
    override fun parse(rawText: String, rawBytes: ByteArray): ParsedContent? {
        if (!canParse(rawText)) return null
        
        val lines = rawText.lines()
        var name: String? = null
        val phones = mutableListOf<String>()
        val emails = mutableListOf<String>()
        var address: String? = null
        var organization: String? = null
        var website: String? = null
        
        for (line in lines) {
            when {
                line.startsWith("FN:", ignoreCase = true) -> {
                    name = line.substring(3).trim()
                }
                line.startsWith("TEL", ignoreCase = true) -> {
                    val phone = extractValue(line)
                    if (phone.isNotEmpty()) phones.add(phone)
                }
                line.startsWith("EMAIL", ignoreCase = true) -> {
                    val email = extractValue(line)
                    if (email.isNotEmpty()) emails.add(email)
                }
                line.startsWith("ADR", ignoreCase = true) -> {
                    address = extractValue(line).replace(";", ", ")
                }
                line.startsWith("ORG:", ignoreCase = true) -> {
                    organization = line.substring(4).trim()
                }
                line.startsWith("URL:", ignoreCase = true) -> {
                    website = line.substring(4).trim()
                }
            }
        }
        
        return ParsedContent.ContactContent(
            name = name,
            phones = phones,
            emails = emails,
            address = address,
            organization = organization,
            website = website
        )
    }
    
    override fun canParse(rawText: String): Boolean {
        return rawText.trim().startsWith("BEGIN:VCARD", ignoreCase = true) &&
               rawText.contains("END:VCARD", ignoreCase = true)
    }
    
    override fun getFormat(): QrCodeFormat = QrCodeFormat.VCARD
    
    private fun extractValue(line: String): String {
        val colonIndex = line.indexOf(':')
        return if (colonIndex >= 0 && colonIndex < line.length - 1) {
            line.substring(colonIndex + 1).trim()
        } else {
            ""
        }
    }
}

