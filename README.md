# WomenSafetyApp (WhoSafe / Nirbhaya-Who Safe)

Android emergency safety application focused on fast SOS activation, trusted contact and group alerting, location sharing, and resilient background behavior.

## App Summary

- Package name: com.womensafety.app
- Module: app
- Min SDK: 26
- Target SDK: 35
- Compile SDK: 35
- Version: 1.0.6 (versionCode 7)

## Core Features

### SOS and Emergency Handling

- Manual SOS trigger from the app UI.
- Automatic SOS trigger from incoming IoT SIM call detection.
- Broadcast SOS trigger receiver with signature-level permission protection.
- Foreground emergency service with high-priority notification and full-screen emergency screen.
- Optional siren playback and optional location attachment.
- SOS cooldown protection to reduce rapid repeated triggers.

### Contacts and Groups

- Local emergency contacts using Room database.
- Sync contacts from backend.
- Add contact via backend createcontact API.
- Global contact delete via backend delete/{c_id} API.
- Create/list/delete groups via backend APIs.

### Auth and User State

- OTP mobile verification flow.
- Registration flow with backend userregister API.
- Encrypted local persistence of user profile and secure app settings.

### Reliability and OEM Support

- Runtime permission management for emergency flows.
- Battery optimization guidance.
- Android 12 exact alarm flow support.
- Android 13 notification permission support.
- Android 14 full-screen intent permission support.
- Xiaomi/MIUI autostart and background-restriction setup helpers.

### Logging and Observability

- Activity logging to local file.
- Activity logging to Firebase Realtime Database.

## SOS Flow

1. Trigger arrives from manual tap, broadcast, or IoT call receiver.
2. EmergencySOSService starts in foreground.
3. High-priority emergency notification is shown with stop action.
4. Siren may start (based on settings).
5. Location link is attempted.
6. Backend flow executes in order:
   - POST /sendemergencymessages
   - GET /getallgroups/{u_id}
   - POST /sendgroupmessage for each group
7. User can stop SOS from notification or emergency screen.

## Backend Base URL and Endpoints

Base URL:

- https://app.whosafeglobal.com

Main endpoints used:

- POST /verifymobilenumber
- POST /verifymobileotp
- POST /userregister
- GET /getallcontacts/{u_id}
- POST /createcontact
- DELETE /delete/{c_id}
- POST /creategroup
- GET /getallgroups/{u_id}
- DELETE /deletegroup/{g_id}
- POST /sendemergencymessages
- POST /sendgroupmessage

## Permissions

The app declares and/or requests:

- INTERNET
- SEND_SMS
- READ_PHONE_STATE
- READ_CALL_LOG
- READ_CONTACTS
- VIBRATE
- MODIFY_AUDIO_SETTINGS
- ACCESS_NOTIFICATION_POLICY
- ACCESS_FINE_LOCATION
- ACCESS_COARSE_LOCATION
- FOREGROUND_SERVICE
- FOREGROUND_SERVICE_SPECIAL_USE
- POST_NOTIFICATIONS
- USE_FULL_SCREEN_INTENT
- WAKE_LOCK
- SCHEDULE_EXACT_ALARM
- REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
- RECEIVE_BOOT_COMPLETED

Custom permission:

- com.womensafety.app.permission.SOS_TRIGGER (signature)

## Architecture

- UI: Jetpack Compose
- State: ViewModel + StateFlow
- Local DB: Room
- Secure storage: EncryptedSharedPreferences
- Networking: OkHttp and Retrofit
- Background: Foreground service and broadcast receivers
- Logs: Firebase Realtime Database + local file logs

## Project Structure

Only app module is packaged by settings.gradle.kts.

```text
app/
  src/main/
    AndroidManifest.xml
    java/com/womensafety/app/
      data/
      logging/
      network/
      receiver/
      ui/
```

## Build

From repository root:

```bash
gradlew.bat clean
gradlew.bat assembleDebug
```

Release builds:

```bash
gradlew.bat assembleRelease
gradlew.bat bundleRelease
```

## Signing Setup

Create keystore.properties from keystore.properties.template:

```properties
storeFile=../release-keystore.jks
storePassword=YOUR_STORE_PASSWORD
keyAlias=womensafety
keyPassword=YOUR_KEY_PASSWORD
```

Never commit real keystore credentials.

## Install APK

### Scripted install

```bat
INSTALLATION_SCRIPT.bat
```

### Manual install

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb shell am start -n com.womensafety.app/.MainActivity
```

## First-Run Checklist

1. Grant runtime permissions.
2. Enable location services.
3. Complete login/registration and OTP.
4. Add emergency contacts.
5. Configure IoT SIM number if needed.
6. Disable battery optimization for reliability.
7. Complete MIUI setup if on Xiaomi/Redmi/POCO devices.

## Security Notes

- Sensitive local data is encrypted.
- App backup is disabled in active manifest.
- SOS trigger broadcast is protected by custom signature permission.
- Review Firebase rules and privacy compliance for deployment region.

## Troubleshooting

### SOS not delivered

- Verify backend reachability and valid user session.
- Confirm contacts/groups exist on backend.

### IoT trigger not firing

- Verify IoT SIM number format and permissions.
- Ensure call log and phone state permissions are granted.

### Background reliability issues

- Disable battery optimization.
- Complete MIUI autostart/background setup.

### Build issues

- Ensure JDK 11+ and Android SDK 35 are installed.
- Verify keystore path and credentials for release.

## Disclaimer

This app is an assistive safety tool. In real emergencies, contact local emergency services immediately.
