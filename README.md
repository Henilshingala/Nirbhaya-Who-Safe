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
