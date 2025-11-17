package net.kibotu.hexqrapp.data.model

sealed class ParsedContent {
    data class UrlContent(
        val url: String,
        val protocol: String,
        val domain: String,
        val path: String?,
        val queryParams: Map<String, String>?,
        val fragment: String?
    ) : ParsedContent()
    
    data class ContactContent(
        val name: String?,
        val phones: List<String>,
        val emails: List<String>,
        val address: String?,
        val organization: String?,
        val website: String?
    ) : ParsedContent()
    
    data class WifiContent(
        val ssid: String,
        val security: WifiSecurity,
        val password: String?,
        val hidden: Boolean
    ) : ParsedContent()
    
    data class EmailContent(
        val email: String,
        val subject: String?,
        val body: String?
    ) : ParsedContent()
    
    data class SmsContent(
        val phoneNumber: String,
        val message: String?
    ) : ParsedContent()
    
    data class GeoContent(
        val latitude: Double,
        val longitude: Double,
        val altitude: Double?
    ) : ParsedContent()
    
    data class CalendarContent(
        val title: String?,
        val startDate: String?,
        val endDate: String?,
        val location: String?,
        val description: String?
    ) : ParsedContent()
    
    data class TextContent(
        val text: String,
        val encoding: String? = null
    ) : ParsedContent()
}

enum class WifiSecurity {
    WPA,
    WPA2,
    WEP,
    OPEN,
    UNKNOWN
}

data class ParsedContentItem(
    val label: String,
    val value: String,
    val icon: String? = null
)

