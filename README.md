# eduroam WiFi Connection App

A simple Android application for connecting to eduroam WiFi networks using EAP-TTLS authentication with PAP.

## Features

- **Simple Interface**: One-page app with logo, email input, password input, and forgot password link
- **EAP-TTLS Authentication**: Configured for TTLS with PAP phase 2 authentication
- **No CA Validation**: As per requirements, CA certificate validation is disabled
- **Material Design**: Modern UI with proper input validation
- **Android 7+ Support**: Targets Android API 24 and above

## Technical Specifications

### WiFi Configuration
- **SSID**: eduroam
- **EAP Method**: TTLS  
- **Phase 2 Authentication**: PAP
- **CA Certificate**: Not validated (as requested)
- **Identity**: User email input
- **Password**: User password input

### Android Requirements
- **Minimum SDK**: API 24 (Android 7.0)
- **Target SDK**: API 33 (Android 13)
- **Permissions Required**:
  - `ACCESS_WIFI_STATE`
  - `CHANGE_WIFI_STATE`
  - `ACCESS_NETWORK_STATE`
  - `INTERNET`

## Installation

1. Clone this repository
2. Open in Android Studio
3. Build and run on an Android device or emulator with WiFi capability

## Usage

1. Launch the app
2. Enter your institutional email address
3. Enter your password
4. Tap "Connect to eduroam"
5. Grant WiFi permissions if prompted
6. The app will configure and connect to the eduroam network

## File Structure

```
app/src/main/
├── java/com/example/eduroam/
│   └── MainActivity.java          # Main activity with WiFi logic
├── res/
│   ├── layout/
│   │   └── activity_main.xml      # UI layout
│   ├── drawable/
│   │   ├── eduroam_logo.xml       # App logo
│   │   ├── ic_email.xml           # Email icon
│   │   ├── ic_lock.xml            # Password icon
│   │   └── ic_wifi.xml            # WiFi icon
│   └── values/
│       ├── colors.xml             # Color definitions
│       └── strings.xml            # String resources
└── AndroidManifest.xml            # App manifest with permissions
```

## Key Implementation Details

### WiFi Configuration
The app uses `WifiManager` and `WifiEnterpriseConfig` to configure eduroam:

```java
WifiEnterpriseConfig enterpriseConfig = new WifiEnterpriseConfig();
enterpriseConfig.setEapMethod(WifiEnterpriseConfig.Eap.TTLS);
enterpriseConfig.setPhase2Method(WifiEnterpriseConfig.Phase2.PAP);
enterpriseConfig.setIdentity(email);
enterpriseConfig.setPassword(password);
enterpriseConfig.setCaCertificate(null); // No CA validation
```

### Input Validation
- Email format validation
- Empty field checks
- User-friendly error messages

### Permissions
The app requests WiFi permissions at runtime for Android 6.0+ compatibility.

## Customization

To customize for your institution:

1. **Forgot Password URL**: Update the URL in `MainActivity.java`:
   ```java
   String url = "https://your-institution.edu/forgot-password";
   ```

2. **Logo**: Replace `eduroam_logo.xml` with your institutional logo

3. **Colors**: Modify colors in `res/values/colors.xml`

## Security Note

This app is configured to not validate CA certificates as per the requirements. In production environments, it's recommended to use proper certificate validation for enhanced security.

## Troubleshooting

### Common Issues

1. **Permission Denied**: Ensure WiFi permissions are granted
2. **Connection Failed**: Verify credentials and that eduroam is available
3. **Network Already Configured**: The app removes existing eduroam configurations before adding new ones

### Debug Information

The app provides toast messages for:
- Input validation errors
- Connection status
- Permission requirements

## Development

### Building
```bash
./gradlew build
```

### Running Tests
```bash
./gradlew test
```

## License

This project is intended for educational and institutional use for eduroam WiFi connectivity.