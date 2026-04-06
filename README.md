
# Nirbhaya - Women Safety Application (Who Safe)

## 📋 Project Overview
**Nirbhaya (Who Safe)** is an **Android mobile application** designed to enhance women's safety through emergency alert features and .This app provides quick SOS functionality to help women in distress situations.

## 📱 Application Type
- **Platform**: Android (APK)
- **Type**: Safety & Emergency Application
- **Target Audience**: Women and their safety network

## ✨ Key Features

### 1. **SOS Emergency System**
- One-touch emergency alert button
- Quick access to emergency contacts
- Automatic notification sending

### 2. **Location Services**
- Real-time location tracking
- Location sharing with trusted contacts
- GPS-based emergency alerts

### 3. **Contact Management**
- Add emergency contacts
- Quick dial functionality
- Automated emergency messaging

### 4. **Security Features**
- Background service for continuous monitoring
- Permission-based access control
- Secure contact storage

## 🛠️ Technology Stack
- **Language**: Java/Kotlin (Android)
- **Platform**: Android
- **Build System**: Gradle
- **Minimum SDK**: Android 5.0 (API level 21) or higher
- **Target SDK**: Latest Android version

## 📁 Project Structure
```
Nirbhaya-Who-Safe-main/
└── apk/
    └── app/
        └── release/
            └── app-release.apk    # Installable Android application
```

## 📥 Installation

### On Android Device

1. **Enable Unknown Sources**
   - Go to Settings → Security
   - Enable "Install from Unknown Sources" or "Install Unknown Apps"
   
2. **Download the APK**
   - Transfer `app-release.apk` to your Android device
   
3. **Install the Application**
   - Locate the APK file using File Manager
   - Tap on the APK file
   - Follow installation prompts
   - Click "Install"

4. **Grant Permissions**
   - Location access (required for GPS tracking)
   - Contacts access (for emergency contacts)
   - Phone access (for emergency calls)
   - SMS access (for sending alerts)
   - Background location (for continuous monitoring)

## 📸 Output Screenshots

Experience the interface of the Nirbhaya application:


 ![Screen 1](https://raw.githubusercontent.com/Henilshingala/Output-images/master/Nirbhaya-who-safe/1.jpeg)  
 
 ![Screen 2](https://raw.githubusercontent.com/Henilshingala/Output-images/master/Nirbhaya-who-safe/2.jpeg)  
 
 ![Screen 3](https://raw.githubusercontent.com/Henilshingala/Output-images/master/Nirbhaya-who-safe/3.jpeg) 
 
 ![Screen 4](https://raw.githubusercontent.com/Henilshingala/Output-images/master/Nirbhaya-who-safe/4.jpeg) 
 
 ![Screen 5](https://raw.githubusercontent.com/Henilshingala/Output-images/master/Nirbhaya-who-safe/5.jpeg) 
 
 ![Screen 6](https://raw.githubusercontent.com/Henilshingala/Output-images/master/Nirbhaya-who-safe/6.jpeg) 
 
 ![Screen 7](https://raw.githubusercontent.com/Henilshingala/Output-images/master/Nirbhaya-who-safe/7.jpeg) 
 
 ![Screen 8](https://raw.githubusercontent.com/Henilshingala/Output-images/master/Nirbhaya-who-safe/8.jpeg) 
 
 ![Screen 9](https://raw.githubusercontent.com/Henilshingala/Output-images/master/Nirbhaya-who-safe/9.jpeg) 
 
 ![Screen 10](https://raw.githubusercontent.com/Henilshingala/Output-images/master/Nirbhaya-who-safe/10.jpeg) 
 
 ![Screen 11](https://raw.githubusercontent.com/Henilshingala/Output-images/master/Nirbhaya-who-safe/11.jpeg) 

---

## 🚀 How to Use

### First Time Setup
1. **Launch the App**: Open Nirbhaya after installation
2. **Grant Permissions**: Allow all requested permissions
3. **Add Emergency Contacts**: 
   - Navigate to contacts section
   - Add trusted contacts who will receive emergency alerts
4. **Configure Settings**: 
   - Set emergency message template
   - Configure alert preferences

### During Emergency
1. **Activate SOS**: Press the emergency/SOS button
2. **Automatic Actions**:
   - GPS location is captured
   - Emergency SMS sent to all contacts
   - Location shared with emergency contacts
   - Optional: Automatic call to primary contact

## 🎯 Use Cases

### Primary Use Cases
- **Personal Safety**: Quick emergency alert for dangerous situations
- **Travel Security**: Share location when traveling alone
- **Night Safety**: Continuous monitoring during night travel
- **Campus Safety**: Students can alert parents/friends instantly

### Emergency Scenarios
- Walking alone in unsafe areas
- Traveling at night
- Unexpected threatening situations
- Medical emergencies
- Accidents or incidents

## ⚙️ Required Permissions

The app requires the following Android permissions:
- ✅ **ACCESS_FINE_LOCATION**: For precise GPS location
- ✅ **ACCESS_COARSE_LOCATION**: For approximate location
- ✅ **SEND_SMS**: To send emergency text messages
- ✅ **READ_CONTACTS**: To access emergency contact list
- ✅ **CALL_PHONE**: For emergency calling
- ✅ **ACCESS_BACKGROUND_LOCATION**: For tracking while app is in background
- ✅ **FOREGROUND_SERVICE**: For continuous monitoring
- ✅ **VIBRATE**: For alert notifications

## 🔐 Privacy & Security

### Data Protection
- Contact information stored locally on device
- Location data only shared during emergencies
- No data sent to external servers without user action
- Secure storage of emergency contacts

### Security Features
- App lock/PIN protection (if implemented)
- Encrypted contact storage
- No location tracking without explicit activation
- User-controlled data sharing

## 📊 Features Breakdown

### Core Functionality
1. **Emergency Button**: Large, easily accessible SOS button
2. **GPS Tracking**: Real-time location capture and sharing
3. **Multi-contact Alerts**: Send alerts to multiple contacts simultaneously
4. **Background Service**: Continuous monitoring capability

### Additional Features (May Include)
- Shake to activate SOS
- Volume button shortcuts
- Audio/video recording during emergency
- Police station locator
- Safe zone alerts
- Fake call feature
- Safety tips and resources

## 🎨 User Interface
- Simple, intuitive design
- Large emergency button for quick access
- Easy contact management
- Clear permission requests
- Minimal learning curve

## 📱 Compatibility
- **Minimum Android Version**: Android 5.0 (Lollipop)
- **Recommended**: Android 8.0 or higher
- **Device Requirements**: GPS-enabled smartphone
- **Network**: Works with any cellular network

## 🚨 Important Safety Notes

### Usage Guidelines
- ⚠️ **Not a replacement for emergency services** - Always call local emergency numbers (112, 100) when possible
- ⚠️ **Battery Impact** - Background monitoring may affect battery life
- ⚠️ **Network Required** - SMS and location sharing require active network
- ⚠️ **Test Before Emergency** - Test the app with your contacts in a safe environment

### Best Practices
- Keep emergency contacts updated
- Ensure location services are always enabled
- Keep the app updated to the latest version
- Inform emergency contacts about the app
- Test SOS feature periodically

## 🔧 Troubleshooting

### Common Issues

**App not sending SMS**
- Check SMS permission is granted
- Verify network connectivity
- Ensure contacts have valid phone numbers

**Location not sharing**
- Enable GPS/Location services
- Grant location permissions
- Check if location accuracy is set to "High"

**App crashes**
- Clear app cache
- Reinstall the application
- Ensure device meets minimum requirements

**Contacts not saving**
- Grant contacts permission
- Check device storage availability

## 📈 Future Enhancements (Potential)
- Integration with local police departments
- Community safety features
- Safety route suggestions
- Emergency service hotline integration
- Multi-language support
- Wearable device integration

## 🌐 Emergency Contacts (India)
- **Women Helpline**: 1091
- **Police**: 100
- **Emergency**: 112
- **Ambulance**: 102

## 👥 Target Users
- Women of all ages
- College students
- Working professionals
- Solo travelers
- Parents monitoring children
- Elderly women

## 🎓 Educational Use
This app can serve as:
- Learning resource for Android development
- Safety awareness tool
- Community safety initiative
- Women empowerment technology

## 📝 Version Information
- **APK Name**: app-release.apk
- **Build Type**: Release
- **Installation Type**: Sideload (APK installation)

## ⚡ Quick Start Guide
1. Install APK → 2. Grant Permissions → 3. Add Contacts → 4. Ready for Emergency!

## 🤝 Support & Feedback
For issues, suggestions, or feedback:
- Test the app thoroughly
- Report bugs to developers
- Share safety experiences
- Suggest new features

## 🌟 Why Nirbhaya?
Named after the movement for women's safety, this app aims to empower women with technology to feel safer and more secure in their daily lives.

---

**Application Type**: Women Safety & Emergency Alert System
**Platform**: Android
**Status**: Release Build Available
**Purpose**: Empowering women's safety through technology

⚠️ **Disclaimer**: This app is a safety tool and should be used alongside other safety measures. Always contact local emergency services in critical situations.
=======
# WomenSafetyApp (WhoSafe / Nirbhaya-Who Safe)

Android emergency safety application focused on fast SOS activation, trusted contact/group alerting, location sharing, and resilient background behavior for real-world emergency scenarios.

This README is based on the current code in this repository and documents the actual app behavior, build setup, architecture, and operational details.

## 1) App Summary

- Package name: `com.womensafety.app`
- Primary module: `app`
- Min SDK: `26`
- Target SDK: `35`
- Compile SDK: `35`
- Current app version (from Gradle config): `versionCode 7`, `versionName 1.0.6`
- Main platform stack: Kotlin + Jetpack Compose + Room + DataStore/Encrypted prefs + OkHttp/Retrofit + Firebase Realtime Database (activity logs)

## 2) Core Capabilities

### Emergency/SOS

- Manual SOS trigger from in-app home flow.
- Automatic SOS trigger on incoming call from configured IoT SIM number.
- Broadcast-triggered SOS receiver path (`com.womensafety.app.SOS_TRIGGER`) protected by a signature-level custom permission.
- Foreground emergency service with high-priority lock-screen notification and full-screen emergency activity.
- Optional siren playback during SOS.
- Optional location attachment (Google Maps link) in SOS workflow.
- Cooldown logic to reduce accidental repeated manual triggers.

### Contacts and Circles

- Local emergency contact storage using Room.
- Backend sync of contacts.
- Add contact via backend API (`createcontact`) with local DB update.
- Global delete contact from backend (`delete/{c_id}`) and local store sync.
- Group creation and group listing via backend APIs.

### Authentication and User State

- OTP-based mobile verification flows (`verifymobilenumber`, `verifymobileotp`).
- Registration flow (`userregister`).
- Persistent registration state and profile data.
- Sender/receiver role tracking in secure preferences.

### Device Reliability and OEM Handling

- Runtime permission orchestration for emergency use-cases.
- Battery optimization exemptions workflow.
- Android 12+ exact alarm handling.
- Android 13+ notification permission handling.
- Android 14+ full-screen intent permission handling.
- MIUI/Xiaomi setup helpers for autostart/background restrictions.
- Boot receiver and direct-boot-aware receivers for resilience after restart.

### Audit/Observability

- Activity logging to local file (`activity_logs.txt` in app internal storage).
- Activity logging to Firebase Realtime Database (`activities/{userId}/{timestamp}`).

## 3) Real SOS Flow (Implemented)

When SOS is triggered (manual, broadcast, or IoT-call path):

1. App starts `EmergencySOSService` in foreground.
2. Service posts a high-priority ongoing emergency notification with STOP action.
3. Service may play siren (if enabled in app settings).
4. Service tries to fetch current location link.
5. Service executes API sequence:
	 - POST `/sendemergencymessages` (personal contacts)
	 - GET `/getallgroups/{u_id}`
	 - POST `/sendgroupmessage` for each group
6. Emergency full-screen activity can be shown over lock screen.
7. User can stop SOS via notification action or emergency screen button.

## 4) Backend/API Integration

Primary backend base URL in current implementation:

- `https://app.whosafeglobal.com`

Used endpoints across app flows include:

- `POST /verifymobilenumber`
- `POST /verifymobileotp`
- `POST /userregister`
- `GET /getallcontacts/{u_id}`
- `POST /createcontact`
- `DELETE /delete/{c_id}`
- `POST /creategroup`
- `GET /getallgroups/{u_id}`
- `DELETE /deletegroup/{g_id}`
- `POST /sendemergencymessages`
- `POST /sendgroupmessage`

Notes:

- A deprecated SMS-based manager still exists for backward compatibility, but emergency flow is currently API-first.
- Location-only sharing still has an SMS utility path in code for selected use-cases.

## 5) Permissions (Manifest + Runtime)

The app declares and/or requests permissions for:

- `INTERNET`
- `SEND_SMS`
- `READ_PHONE_STATE`
- `READ_CALL_LOG`
- `READ_CONTACTS`
- `VIBRATE`
- `MODIFY_AUDIO_SETTINGS`
- `ACCESS_NOTIFICATION_POLICY`
- `ACCESS_FINE_LOCATION`
- `ACCESS_COARSE_LOCATION`
- `FOREGROUND_SERVICE`
- `FOREGROUND_SERVICE_SPECIAL_USE`
- `POST_NOTIFICATIONS`
- `USE_FULL_SCREEN_INTENT`
- `WAKE_LOCK`
- `SCHEDULE_EXACT_ALARM`
- `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS`
- `RECEIVE_BOOT_COMPLETED`

Also defines custom permission:

- `com.womensafety.app.permission.SOS_TRIGGER` (protection level: `signature`)

## 6) Architecture

- UI: Jetpack Compose screens and components.
- State: ViewModel + `StateFlow` driven UI updates.
- Persistence:
	- Room for emergency contacts (`emergency_contacts`).
	- EncryptedSharedPreferences for user/secure app state.
- Networking:
	- Shared `OkHttpClient` for API calls.
	- Retrofit support for WhatsApp-related services.
- Background:
	- Foreground service for emergency handling.
	- Broadcast receivers for boot, SOS trigger, and phone state changes.
- Logging:
	- Firebase Realtime Database + local file append logging.

## 7) Project Layout (Important)

- Build includes only the `app` module (`settings.gradle.kts` includes `:app`).
- Active Android manifest for packaging: `app/src/main/AndroidManifest.xml`.
- The root-level `src/main/kotlin` tree exists but is not part of the packaged `app` module by default.

Main active folders:

```text
app/
	build.gradle.kts
	src/main/
		AndroidManifest.xml
		java/com/womensafety/app/
			data/
			logging/
			network/
			receiver/
			ui/
```

## 8) Tech Stack and Key Dependencies

- Android Gradle Plugin `8.5.0`
- Kotlin `1.9.10`
- Jetpack Compose UI + Material3
- Navigation Compose
- Lifecycle ViewModel + Runtime
- Room `2.6.1` + KSP
- DataStore Preferences
- AndroidX Security Crypto
- Coroutines
- Google Play Services Location
- OkHttp + Retrofit + Gson converter
- Firebase BOM + Realtime Database + Analytics

## 9) Build and Run

From repository root (Windows PowerShell / cmd):

```bash
gradlew.bat clean
gradlew.bat assembleDebug
```

Debug APK output (default):

- `app/build/outputs/apk/debug/app-debug.apk`

Release APK / AAB:

```bash
gradlew.bat assembleRelease
gradlew.bat bundleRelease
```

## 10) Signing Configuration

Create `keystore.properties` from `keystore.properties.template`:

```properties
storeFile=../release-keystore.jks
storePassword=YOUR_STORE_PASSWORD
keyAlias=womensafety
keyPassword=YOUR_KEY_PASSWORD
```

Security guidance:

- Never commit real keystore or passwords.
- Rotate and protect credentials in CI/CD secrets.
- Keep `google-services.json` environment-appropriate.

## 11) Install to Device

### Option A: ADB Script (provided)

Run:

```bat
INSTALLATION_SCRIPT.bat
```

Script behavior:

- Verifies ADB installation
- Detects connected device
- Installs `app/build/outputs/apk/release/app-release.apk`
- Launches `com.womensafety.app/.MainActivity`

### Option B: Manual ADB

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb shell am start -n com.womensafety.app/.MainActivity
```

## 12) Runtime Setup Checklist (First Launch)

1. Grant required runtime permissions.
2. Enable location services on device.
3. Complete registration/OTP flow.
4. Add emergency contacts.
5. Configure IoT SIM number (if using hardware trigger).
6. Disable battery optimization for the app.
7. On MIUI/Xiaomi devices, complete autostart/background settings.

## 13) Security and Privacy Notes

- Sensitive user data and secure settings use EncryptedSharedPreferences.
- App backup is disabled in active manifest (`allowBackup=false`).
- SOS broadcast entry is protected by signature-level permission.
- Activity logs include user-linked metadata and are uploaded to Firebase; ensure backend policies and legal/privacy compliance for deployment region.

## 14) Known Implementation Notes

- Some legacy/deprecated classes remain for compatibility (for example, older SMS-first SOS utility paths).
- Existing release artifacts under `app/release` may not always match the latest Gradle version metadata; treat `app/build.gradle.kts` as source of truth for current versioning.
- The app contains sender and receiver role concepts, but current packaged manifest and flow are optimized for sender-side emergency initiation plus backend fan-out.

## 15) Troubleshooting

### App does not receive/trigger reliably in background

- Check battery optimization exemption.
- Check MIUI autostart/background restrictions.
- Verify all runtime permissions are granted.

### SOS does not notify contacts/groups

- Verify user ID exists in secure user preferences.
- Check API reachability to `app.whosafeglobal.com`.
- Confirm contacts/groups exist for that user on backend.

### IoT-call trigger not firing

- Validate configured IoT SIM number format.
- Ensure `READ_PHONE_STATE` and `READ_CALL_LOG` are granted.
- Confirm incoming number normalization (last 10 digits) matches configured value.

### Build/signing failure

- Recheck `keystore.properties` path and passwords.
- Ensure Java 11+ is installed.
- Ensure Android SDK with API 35 platform is installed.

## 16) Disclaimer

This application is an assistive safety tool. It does not replace official emergency response services. In any real emergency, contact local police/ambulance services immediately.
