# StudySync AI

StudySync AI is a JavaFX desktop application created for CSC325. It helps students upload course documents, organize study materials, generate AI-assisted summaries and quizzes, and access saved study content.

## Project Purpose

StudySync AI,  is a desktop application designed to help students organize study materials, generate AI summaries and quizzes, and track study sessions. 

## Main Features

- Student and guest access
- Login and registration
- Firebase Authentication
- Firebase Firestore data storage
- Firebase Storage document upload support
- Course-based document organization
- AI-generated study guides using Gemini
- AI-generated multiple-choice questions using Gemini
- Quiz and summary management
- Usage/quota controls for AI requests
- Offline/local cache support
- JavaFX desktop user interface

## Technology Stack
- Java
- JavaFX
- Maven
- Firebase Authentication
- Firebase Firestore
- Firebase Storage
- Gemini API
- CSS

## Project Structure

```text
src/
 ├── com/app/
 │    ├── Main.java
 │    ├── LoginPage.java
 │    ├── RegisterPage.java
 │    ├── AuthService.java
 │    ├── FirebaseService.java
 │    ├── FirestoreService.java
 │    ├── StorageService.java
 │    ├── SceneManager.java
 │    ├── DashboardView.java
 │    ├── UploadView.java
 │    ├── SummaryView.java
 │    ├── QuizView.java
 │    ├── OfflineModeService.java
 │    ├── LocalCacheService.java
 │    └── QuotaTrackingService.java
 │
 └── org/example/
      ├── QuizManager.java
      ├── SummaryManager.java
      ├── QuotaManager.java
      └── StudyController.java
