package net.kibotu.hexqrapp.domain.parser.impl

import net.kibotu.hexqrapp.data.model.ParsedContent
import net.kibotu.hexqrapp.data.model.QrCodeFormat
import net.kibotu.hexqrapp.domain.parser.QrCodeParser

class CalendarParser : QrCodeParser {
    override fun parse(rawText: String, rawBytes: ByteArray): ParsedContent? {
        if (!canParse(rawText)) return null
        
        val lines = rawText.lines()
        var title: String? = null
        var startDate: String? = null
        var endDate: String? = null
        var location: String? = null
        var description: String? = null
        
        for (line in lines) {
            when {
                line.startsWith("SUMMARY:", ignoreCase = true) -> {
                    title = line.substring(8).trim()
                }
                line.startsWith("DTSTART", ignoreCase = true) -> {
                    startDate = extractValue(line)
                }
                line.startsWith("DTEND", ignoreCase = true) -> {
                    endDate = extractValue(line)
                }
                line.startsWith("LOCATION:", ignoreCase = true) -> {
                    location = line.substring(9).trim()
                }
                line.startsWith("DESCRIPTION:", ignoreCase = true) -> {
                    description = line.substring(12).trim()
                }
            }
        }
        
        return ParsedContent.CalendarContent(
            title = title,
            startDate = startDate,
            endDate = endDate,
            location = location,
            description = description
        )
    }
    
    override fun canParse(rawText: String): Boolean {
        return rawText.trim().startsWith("BEGIN:VEVENT", ignoreCase = true) &&
               rawText.contains("END:VEVENT", ignoreCase = true)
    }
    
    override fun getFormat(): QrCodeFormat = QrCodeFormat.CALENDAR
    
    private fun extractValue(line: String): String {
        val colonIndex = line.indexOf(':')
        return if (colonIndex >= 0 && colonIndex < line.length - 1) {
            line.substring(colonIndex + 1).trim()
        } else {
            ""
        }
    }
}

