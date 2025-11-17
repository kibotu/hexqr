package net.kibotu.hexqrapp.domain.parser.impl

import net.kibotu.hexqrapp.data.model.ParsedContent
import net.kibotu.hexqrapp.data.model.QrCodeFormat
import net.kibotu.hexqrapp.data.model.WifiSecurity
import net.kibotu.hexqrapp.domain.parser.QrCodeParser

class WifiParser : QrCodeParser {
    override fun parse(rawText: String, rawBytes: ByteArray): ParsedContent? {
        if (!canParse(rawText)) return null
        
        val lines = rawText.lines()
        var ssid: String? = null
        var security: WifiSecurity = WifiSecurity.OPEN
        var password: String? = null
        var hidden: Boolean = false
        
        for (line in lines) {
            when {
                line.startsWith("S:", ignoreCase = true) || 
                line.startsWith("SSID:", ignoreCase = true) -> {
                    ssid = extractValue(line)
                }
                line.startsWith("T:", ignoreCase = true) || 
                line.startsWith("TYPE:", ignoreCase = true) -> {
                    val type = extractValue(line).uppercase()
                    security = when {
                        type.contains("WPA2") -> WifiSecurity.WPA2
                        type.contains("WPA") -> WifiSecurity.WPA
                        type.contains("WEP") -> WifiSecurity.WEP
                        type.contains("NOPASS") || type.contains("OPEN") -> WifiSecurity.OPEN
                        else -> WifiSecurity.UNKNOWN
                    }
                }
                line.startsWith("P:", ignoreCase = true) || 
                line.startsWith("PASSWORD:", ignoreCase = true) -> {
                    password = extractValue(line)
                }
                line.startsWith("H:", ignoreCase = true) || 
                line.startsWith("HIDDEN:", ignoreCase = true) -> {
                    hidden = extractValue(line).toBoolean()
                }
            }
        }
        
        // Also try parsing WIFI:T:WPA;S:NetworkName;P:Password;H:false; format
        if (ssid == null && rawText.contains("WIFI:", ignoreCase = true)) {
            val wifiIndex = rawText.indexOf("WIFI:", ignoreCase = true)
            val wifiPart = if (wifiIndex >= 0) rawText.substring(wifiIndex + 5) else rawText
            val parts = wifiPart.split(";")
            for (part in parts) {
                val keyValue = part.split(":", limit = 2)
                if (keyValue.size == 2) {
                    when (keyValue[0].uppercase()) {
                        "S", "SSID" -> ssid = keyValue[1]
                        "T", "TYPE" -> {
                            val type = keyValue[1].uppercase()
                            security = when {
                                type.contains("WPA2") -> WifiSecurity.WPA2
                                type.contains("WPA") -> WifiSecurity.WPA
                                type.contains("WEP") -> WifiSecurity.WEP
                                type.contains("NOPASS") || type.contains("OPEN") -> WifiSecurity.OPEN
                                else -> WifiSecurity.UNKNOWN
                            }
                        }
                        "P", "PASSWORD" -> password = keyValue[1]
                        "H", "HIDDEN" -> hidden = keyValue[1].toBoolean()
                    }
                }
            }
        }
        
        return ssid?.let {
            ParsedContent.WifiContent(
                ssid = it,
                security = security,
                password = password,
                hidden = hidden
            )
        }
    }
    
    override fun canParse(rawText: String): Boolean {
        return rawText.trim().startsWith("WIFI:", ignoreCase = true)
    }
    
    override fun getFormat(): QrCodeFormat = QrCodeFormat.WIFI
    
    private fun extractValue(line: String): String {
        val colonIndex = line.indexOf(':')
        return if (colonIndex >= 0 && colonIndex < line.length - 1) {
            line.substring(colonIndex + 1).trim()
        } else {
            ""
        }
    }
}

