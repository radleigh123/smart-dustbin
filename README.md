# Smart Dustbin Project

A simple IoT-based smart trash bin that automatically opens its lid using a proximity sensor. Designed as a beginner-friendly project to explore the intersection of hardware, mobile cloud integration, and Internet of Things (IoT).

## 🚀 Features

- Feature 1
- Feature 2
- Feature 3

## 🧰 Tech Stack

| Component         | Description                          |
|------------------|--------------------------------------|
| Microcontroller   | ESP32 / Arduino Uno                  |
| Sensor            | Ultrasonic (HC-SR04) or IR sensor    |
| Actuator          | Servo motor                          |
| Cloud Platform    | Firebase (Realtime DB, Notifications)|
| Mobile App        | Android Studio (Java/Kotlin)         |

## 📱 Mobile Cloud Integration

This project explores **Mobile Cloud Computing** by:
- Sending bin usage data to the cloud
- Allowing remote control via mobile app
- Triggering notifications when bin is full or lid is stuck (OPTIONAL)

## 👥 Team Members

- Keane Radleigh Inting  
- Went Ruzel Igot  
- Jose Carlitos Kintanar  
- Francine Duarte  
- Jacob Mary Tapere  
- John Michael Eborda

## 🏗️ Architecture Documentation

### MVP Architecture Implementation

This project follows the **Model-View-Presenter (MVP)** architecture pattern to ensure separation of concerns and maintainable code structure:

#### 1. Model Layer
- **Repositories**: 
  - `AuthRepository`: Handles Firebase Authentication operations (login, registration, password reset)
  - `TrashBinRepository`: Manages trash bin data CRUD operations via Firebase Realtime Database
- **Entities**: 
  - `TrashBin`: Data class representing trash bin information
  - `User`: Data class for user profile information

#### 2. View Layer
- **Activities**:
  - `IndexActivity`: Container for authentication fragments
  - `MainActivity`: Main application container
- **Fragments**:
  - Authentication: `LoginFragment`, `RegisterFragment`, `ForgotPasswordFragment`
  - Main App: `DashboardFragment`

#### 3. Presenter Layer
- **Contracts**:
  - `AuthContract`: Defines interfaces for authentication views and presenters
  - `MainContract`: Defines interfaces for main activity functionality
- **Presenters**:
  - `AuthPresenter`: Implements authentication business logic
  - `MainPresenter`: Implements main app functionality like logout
