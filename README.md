# QR Code Hex Reader [![GitHub Release](https://img.shields.io/github/v/release/kibotu/hexqr)](https://github.com/kibotu/hexqr/releases)

A modern Android application that provides a comprehensive QR code scanning experience with advanced hex editor functionality. Unlike standard QR readers that only show interpreted content, **QR Code Hex Reader displays the actual raw bytes** of the QR code data, giving you complete transparency into what's encoded. This is essential for security analysis, detecting tracking redirects, debugging non-standard formats, and understanding exactly what data is stored in the QR code.

![QR Code Reader Demo](docs/record.gif)

## Features

### Core Functionality

- **Raw Byte Inspection** - View the actual bytes stored in the QR code, not just interpreted content
- **Real-time QR Code Scanning** - Continuous camera-based detection with visual feedback
- **Smart Content Parsing** - Automatically detects and formats common QR code types (while preserving raw data access)
- **Hex Editor** - Inspect raw byte data with hex representation and ASCII display
- **Security Analysis** - Detect tracking redirects, hidden data, and non-standard encodings
- **Modern UI** - Clean Material Design 3 interface with dark mode support
- **Offline-First** - All processing happens locally, no network required

### Why Raw Bytes Matter

Most QR code readers only show you the interpreted content (e.g., "https://example.com"). But QR codes can contain:
- **Tracking redirects** - URLs that redirect through tracking services before reaching the destination
- **Non-standard formats** - Custom encodings that standard parsers don't recognize
- **Hidden data** - Additional bytes that aren't part of the main content
- **Encoding variations** - Different character encodings that affect how content is displayed

By showing the actual bytes, you can:
- **Verify URLs** - See the exact destination before clicking, avoiding tracking redirects
- **Debug issues** - Understand why a QR code isn't working with standard readers
- **Security audit** - Inspect QR codes for suspicious content or hidden data
- **Learn** - Understand how QR codes encode different data types

### Supported Formats

The app intelligently parses and displays content from various QR code formats:

- **URLs** - Protocol, domain, path, query parameters, and fragments
- **Contacts (vCard)** - Name, phone numbers, emails, address, organization
- **WiFi Networks** - SSID, security type, password, hidden network flag
- **Email** - Address, subject, and body content
- **SMS** - Phone number and message body
- **Geographic Location** - Latitude, longitude, and altitude
- **Calendar Events** - Title, dates, location, and description
- **Plain Text** - Raw text content with encoding information

## Installation

### For End Users

1. Download the latest release APK from [GitHub Releases](https://github.com/yourusername/hexqrapp/releases)
2. Enable "Install from Unknown Sources" on your Android device (if needed)
3. Install the APK file
4. Grant camera permission when prompted

**System Requirements:**
- Android 6.0 (API 23) or higher
- Camera hardware
- Internet connection not required

### Permissions

The app requires the following permission:
- **Camera** - Required for QR code scanning

All camera data is processed locally on your device. No data is transmitted over the network.

## Usage

1. Open the app and grant camera permission
2. Point your camera at a QR code
3. The app will automatically detect and parse the content
4. View formatted content in the main display area
5. **Tap "Hex Editor" to inspect the actual raw bytes** - See exactly what's encoded in the QR code
6. Use copy/share buttons to export content

**Pro Tip**: Always check the hex editor view before clicking URLs to verify the destination and detect any tracking redirects or suspicious content.

## Building

### Prerequisites

- **Android Studio** - Hedgehog (2023.1.1) or later
- **JDK** - Version 21 or higher
- **Android SDK** - API 23+ (Android 6.0)
- **Gradle** - Included via wrapper

### Build Steps

1. **Clone the repository**
   ```bash
   git clone https://github.com/kibotu/hexqrapp.git
   cd hexqrapp
   ```

2. **Configure signing (for release builds)**
   ```bash
   cp keystore.properties.example keystore.properties
   # Edit keystore.properties with your release keystore credentials
   ```

3. **Open in Android Studio**
   - Open Android Studio
   - Select "Open an Existing Project"
   - Navigate to the cloned directory
   - Wait for Gradle sync to complete

4. **Build the project**
   ```bash
   # Debug build
   ./gradlew assembleDebug
   
   # Release build (requires keystore.properties)
   ./gradlew assembleRelease
   ```

   APK files will be generated in `app/build/outputs/apk/`

## Tech Stack

- **Language**: Kotlin 2.2.21
- **UI**: Jetpack Compose (BOM 2025.11.00)
- **Camera**: CameraX + ML Kit Barcode Scanning
- **DI**: Hilt
- **Architecture**: MVVM with StateFlow
- **Minimum SDK**: API 23 (Android 6.0)
- **Target SDK**: API 36 (Android 15)

## Privacy & Security

- **No Network Requests** - All processing happens offline
- **No Data Collection** - No analytics or tracking
- **Local Processing Only** - Camera data never leaves your device
- **No Storage** - Scanned content is not saved unless explicitly shared by the user
- **Privacy-First** - Respects user privacy and data protection


## Troubleshooting

### Camera Not Working

- Ensure camera permission is granted in device settings
- Check that no other app is using the camera
- Restart the app if issues persist

### QR Code Not Detected

- Ensure adequate lighting
- Hold the device steady
- Keep QR code within the camera frame
- Clean the camera lens

### Build Issues

- Ensure JDK 21 is installed and configured
- Sync Gradle files in Android Studio
- Clean and rebuild: `./gradlew clean build`
- Check that Android SDK API 23+ is installed

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request. For major changes, please open an issue first to discuss what you would like to change.

## Acknowledgments

Built with modern Android development tools and libraries:
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [CameraX](https://developer.android.com/training/camerax)
- [ML Kit](https://developers.google.com/ml-kit)
- [Hilt](https://developer.android.com/training/dependency-injection/hilt-android)

---

**Note**: This app is designed to be simple, fast, and privacy-focused. We believe in keeping things straightforward and user-friendly while providing powerful features for those who need them.
