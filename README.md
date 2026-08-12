# 🚨 Nirbhaya – Women Safety Application (WhoSafe)

## 📌 Overview

**Nirbhaya (WhoSafe)** is a real-time emergency response Android application designed to improve personal safety through **instant SOS activation, multi-channel alerting, and resilient background execution**.

Unlike basic safety apps, this system is built to work reliably in **real-world conditions** such as locked screens, background restrictions, and device restarts.

---

## 🎯 Problem Statement

Most safety apps fail when they are needed the most due to:

* Background restrictions by Android OEMs
* Delayed or failed SOS triggering
* Lack of real-time alert distribution
* No reliability during screen lock or low interaction

---

## 💡 Solution

Nirbhaya solves these issues by providing a **multi-layer emergency system**:

* Manual SOS trigger
* Automatic SOS via IoT call trigger
* Secure broadcast-based SOS activation
* Foreground emergency service with persistent notification
* Real-time API-based alert delivery to contacts and groups

---

## 🚀 Key Features

### 🔴 Emergency SOS System

* One-tap SOS activation
* Full-screen emergency interface (even on lock screen)
* High-priority persistent notification with STOP control
* Optional siren alert

### 📡 Multi-Trigger Emergency Activation

* Manual trigger from app
* IoT SIM-based automatic trigger (incoming call detection)
* Secure broadcast trigger (`SOS_TRIGGER`)

### 📍 Real-Time Location Sharing

* GPS-based location capture
* Google Maps link generation
* Sent to contacts and groups during SOS

### 👥 Contacts & Groups

* Add/manage emergency contacts (Room DB)
* Group creation and alert broadcasting
* Backend sync for reliable communication

### 🔄 Reliable Background Execution

* Foreground service for SOS handling
* Boot receiver for restart recovery
* Battery optimization handling
* OEM-specific handling (MIUI/Xiaomi autostart fixes)

### 🔐 Security & Privacy

* EncryptedSharedPreferences for sensitive data
* Signature-level protected broadcast receiver
* Controlled data sharing only during emergency
* App backup disabled

### 📊 Activity Logging

* Local file logging (`activity_logs.txt`)
* Firebase Realtime Database logging for audit tracking

---

## ⚙️ Tech Stack

| Layer          | Technology                  |
| -------------- | --------------------------- |
| Language       | Kotlin                      |
| UI             | Jetpack Compose + Material3 |
| Architecture   | MVVM + StateFlow            |
| Database       | Room                        |
| Secure Storage | EncryptedSharedPreferences  |
| Networking     | OkHttp + Retrofit           |
| Location       | Google Play Services        |
| Backend        | REST APIs                   |
| Logging        | Firebase Realtime Database  |

---

## 🧠 System Architecture

```
User Action (SOS)
        ↓
Foreground Emergency Service
        ↓
Location Fetch (GPS)
        ↓
Backend API Calls
   ├── Send Emergency Messages
   ├── Fetch Groups
   └── Send Group Alerts
        ↓
Contacts / Groups Receive Alerts
        ↓
Activity Logs (Local + Firebase)
```

---

## 🔁 SOS Execution Flow

1. User triggers SOS (manual / IoT / broadcast)
2. Foreground service starts
3. High-priority notification + emergency UI shown
4. Location is captured
5. API calls executed:

   * Send alerts to contacts
   * Fetch groups
   * Send group alerts
6. Optional siren is activated
7. User can stop SOS anytime

---

## 🌐 Backend Integration

Base URL:

```
https://app.whosafeglobal.com
```

### Key APIs

* `POST /verifymobilenumber`
* `POST /verifymobileotp`
* `POST /userregister`
* `GET /getallcontacts/{u_id}`
* `POST /createcontact`
* `DELETE /delete/{c_id}`
* `POST /creategroup`
* `GET /getallgroups/{u_id}`
* `POST /sendemergencymessages`
* `POST /sendgroupmessage`

---

## 🔐 Permissions Used

* Location (Fine + Coarse)
* SMS & Call
* Contacts
* Notifications
* Foreground Service
* Exact Alarm
* Battery Optimization Ignore
* Boot Completed

> ⚠️ Required for real-time emergency reliability

---

## 📱 Build & Installation

### Build APK

```bash
gradlew clean
gradlew assembleDebug
```

### Install via ADB

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb shell am start -n com.womensafety.app/.MainActivity
```

---

## 🛠️ Setup (First Launch)

1. Grant all permissions
2. Complete OTP registration
3. Add emergency contacts
4. (Optional) Configure IoT trigger number
5. Disable battery optimization

---

## ⚠️ Important Notes

* This app **does not replace emergency services**
* Always contact **112 / police** in critical situations
* Requires:

  * Network for API alerts
  * GPS enabled
* Background features depend on device OEM behavior

---

## 📈 Future Enhancements

* AI-based danger detection (sound/motion analysis)
* Offline SOS fallback (SMS-only mode)
* Live tracking dashboard (web)
* Wearable device integration
* Smart safe-route suggestions

---

## 🎯 Target Users

* Women and students
* Solo travelers
* Working professionals
* Emergency preparedness use cases

---

## 📌 Project Status

* ✅ Core SOS system implemented
* ✅ Backend integration active
* ✅ Real-world reliability features added
* 🔄 Ongoing improvements and optimization

---

## ⚖️ Disclaimer

This application is a **supportive safety tool**.
It should be used along with awareness, precaution, and official emergency services.

---

## 🌟 Why Nirbhaya?

Inspired by the need for stronger safety systems, this app focuses on **reliability over appearance**, ensuring that emergency features work even under critical conditions.

---

## Output Images 

| | |
|---|---|
| ![1](https://raw.githubusercontent.com/Henilshingala/Output-images/master/Nirbhaya-who-safe/1.jpg) | ![2](https://raw.githubusercontent.com/Henilshingala/Output-images/master/Nirbhaya-who-safe/2.jpg) |
| ![3](https://raw.githubusercontent.com/Henilshingala/Output-images/master/Nirbhaya-who-safe/3.jpg) | ![4](https://raw.githubusercontent.com/Henilshingala/Output-images/master/Nirbhaya-who-safe/4.jpg) |
| ![5](https://raw.githubusercontent.com/Henilshingala/Output-images/master/Nirbhaya-who-safe/5.jpg) | ![6](https://raw.githubusercontent.com/Henilshingala/Output-images/master/Nirbhaya-who-safe/6.jpg) |
| ![7](https://raw.githubusercontent.com/Henilshingala/Output-images/master/Nirbhaya-who-safe/7.jpg) | ![8](https://raw.githubusercontent.com/Henilshingala/Output-images/master/Nirbhaya-who-safe/8.jpg) |
| ![9](https://raw.githubusercontent.com/Henilshingala/Output-images/master/Nirbhaya-who-safe/9.jpg) | ![10](https://raw.githubusercontent.com/Henilshingala/Output-images/master/Nirbhaya-who-safe/10.jpg) |
| ![11](https://raw.githubusercontent.com/Henilshingala/Output-images/master/Nirbhaya-who-safe/11.jpg) | ![12](https://raw.githubusercontent.com/Henilshingala/Output-images/master/Nirbhaya-who-safe/12.jpg) |
| ![13](https://raw.githubusercontent.com/Henilshingala/Output-images/master/Nirbhaya-who-safe/13.jpg) | ![14](https://raw.githubusercontent.com/Henilshingala/Output-images/master/Nirbhaya-who-safe/14.jpg) |
| ![15](https://raw.githubusercontent.com/Henilshingala/Output-images/master/Nirbhaya-who-safe/15.jpg) | ![16](https://raw.githubusercontent.com/Henilshingala/Output-images/master/Nirbhaya-who-safe/16.jpg) |
| ![17](https://raw.githubusercontent.com/Henilshingala/Output-images/master/Nirbhaya-who-safe/17.jpg) | ![18](https://raw.githubusercontent.com/Henilshingala/Output-images/master/Nirbhaya-who-safe/18.jpg) |
| ![19](https://raw.githubusercontent.com/Henilshingala/Output-images/master/Nirbhaya-who-safe/19.jpg) | ![20](https://raw.githubusercontent.com/Henilshingala/Output-images/master/Nirbhaya-who-safe/20.jpg) |
