package net.kibotu.hexqrapp.domain.parser

import net.kibotu.hexqrapp.data.model.ParsedContent
import net.kibotu.hexqrapp.data.model.QrCodeFormat
import net.kibotu.hexqrapp.domain.parser.impl.*

interface QrCodeParser {
    fun parse(rawText: String, rawBytes: ByteArray): ParsedContent?
    fun canParse(rawText: String): Boolean
    fun getFormat(): QrCodeFormat
}

class QrCodeParserFactory {
    private val parsers = listOf(
        UrlParser(),
        ContactParser(),
        WifiParser(),
        EmailParser(),
        SmsParser(),
        GeoParser(),
        CalendarParser()
    )
    
    fun getParser(rawText: String): QrCodeParser? {
        return parsers.firstOrNull { it.canParse(rawText) }
    }
    
    fun getAllParsers(): List<QrCodeParser> = parsers
}

