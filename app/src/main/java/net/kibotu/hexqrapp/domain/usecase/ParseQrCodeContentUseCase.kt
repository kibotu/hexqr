package net.kibotu.hexqrapp.domain.usecase

import net.kibotu.hexqrapp.data.model.ParsedContent
import net.kibotu.hexqrapp.data.model.ParsedContentItem
import net.kibotu.hexqrapp.domain.parser.QrCodeParserFactory
import javax.inject.Inject

class ParseQrCodeContentUseCase @Inject constructor(
    private val parserFactory: QrCodeParserFactory
) {
    operator fun invoke(rawText: String, rawBytes: ByteArray): List<ParsedContentItem> {
        val items = mutableListOf<ParsedContentItem>()
        
        // Try to parse with the primary parser
        val primaryParser = parserFactory.getParser(rawText)
        val parsedContent = primaryParser?.parse(rawText, rawBytes)
        
        // Add primary parsed content
        parsedContent?.let { content ->
            items.addAll(extractContentItems(content))
        }
        
        // Try all parsers to find additional formats
        val allParsers = parserFactory.getAllParsers()
        for (parser in allParsers) {
            if (parser.canParse(rawText) && parser != primaryParser) {
                parser.parse(rawText, rawBytes)?.let { content ->
                    items.addAll(extractContentItems(content))
                }
            }
        }
        
        // If no content was parsed, add as text
        if (items.isEmpty()) {
            items.add(ParsedContentItem("Text", rawText))
        }
        
        return items
    }
    
    private fun extractContentItems(content: ParsedContent): List<ParsedContentItem> {
        return when (content) {
            is ParsedContent.UrlContent -> {
                listOf(
                    ParsedContentItem("URL", content.url),
                    ParsedContentItem("Protocol", content.protocol.uppercase()),
                    ParsedContentItem("Domain", content.domain),
                    content.path?.let { ParsedContentItem("Path", it) },
                    content.fragment?.let { ParsedContentItem("Fragment", it) },
                    content.queryParams?.let { params ->
                        ParsedContentItem("Query Parameters", params.entries.joinToString(", ") { "${it.key}=${it.value}" })
                    }
                ).filterNotNull()
            }
            is ParsedContent.ContactContent -> {
                buildList {
                    content.name?.let { add(ParsedContentItem("Name", it)) }
                    content.phones.forEach { add(ParsedContentItem("Phone", it)) }
                    content.emails.forEach { add(ParsedContentItem("Email", it)) }
                    content.address?.let { add(ParsedContentItem("Address", it)) }
                    content.organization?.let { add(ParsedContentItem("Organization", it)) }
                    content.website?.let { add(ParsedContentItem("Website", it)) }
                }
            }
            is ParsedContent.WifiContent -> {
                buildList {
                    add(ParsedContentItem("SSID", content.ssid))
                    add(ParsedContentItem("Security", content.security.name))
                    content.password?.let { add(ParsedContentItem("Password", it)) }
                    if (content.hidden) add(ParsedContentItem("Hidden Network", "Yes"))
                }
            }
            is ParsedContent.EmailContent -> {
                buildList {
                    add(ParsedContentItem("Email", content.email))
                    content.subject?.let { add(ParsedContentItem("Subject", it)) }
                    content.body?.let { add(ParsedContentItem("Body", it)) }
                }
            }
            is ParsedContent.SmsContent -> {
                buildList {
                    add(ParsedContentItem("Phone Number", content.phoneNumber))
                    content.message?.let { add(ParsedContentItem("Message", it)) }
                }
            }
            is ParsedContent.GeoContent -> {
                buildList {
                    add(ParsedContentItem("Latitude", content.latitude.toString()))
                    add(ParsedContentItem("Longitude", content.longitude.toString()))
                    content.altitude?.let { add(ParsedContentItem("Altitude", it.toString())) }
                }
            }
            is ParsedContent.CalendarContent -> {
                buildList {
                    content.title?.let { add(ParsedContentItem("Title", it)) }
                    content.startDate?.let { add(ParsedContentItem("Start Date", it)) }
                    content.endDate?.let { add(ParsedContentItem("End Date", it)) }
                    content.location?.let { add(ParsedContentItem("Location", it)) }
                    content.description?.let { add(ParsedContentItem("Description", it)) }
                }
            }
            is ParsedContent.TextContent -> {
                listOf(ParsedContentItem("Text", content.text))
            }
        }
    }
}

