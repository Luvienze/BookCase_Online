# 📚 BookCase Online

**BookCase Online** is a modern mobile library management app designed for Android platforms. It allows users to search for books, manage favorites, track stock, return books, and edit profile information – all from a user-friendly interface.

## 🎯 Project Purpose

This project was developed as part of the **Mobile Programming** course at Duzce University, Computer Engineering Department. The main goal is to provide a digital solution for small to medium-scale local libraries.

## 👥 Developers

- **Eren Sönmez**
- **Eray Özgür**

## 🚀 Features

- 🔐 User registration & login via **Firebase Auth**
- 🔍 Real-time book search using `TextWatcher`
- ⭐ Add/remove books from favorites
- 📦 Track book stock and availability
- 📤 Book return functionality
- 👤 Edit and manage user profile
- 🌙 Dark mode support
- 🌐 English and Turkish language support using `SharedPreferences`
- 🧠 **GeminiAI-powered recommendation system** *(available only in development/demo version)*
- 🔄 Use of `Intents` for navigation and data transfer between activities

## 🧱 Technologies Used

- **Java** (Android SDK)
- **Firebase Authentication** – user management
- **Firebase Realtime Database** – real-time data handling
- **Firebase Cloud Storage** – storing book cover images
- **SharedPreferences** – saving theme and language preferences
- **RecyclerView** – listing and displaying data
- **Material3** – modern UI components

## 📱 Screenshots

> *Add screenshots in a `screenshots/` folder and link them here.*

## 🔒 About the Play Store Version

The **GeminiAI-powered recommendation feature** is active in the development/demo versions and can be seen in the demonstration video. However, due to Google Play security restrictions, it is currently disabled in the Play Store version. We are working to integrate it in future updates.

📲 Download on Google Play:  
[BookCase Online – Play Store Link](https://play.google.com/store/apps/details?id=com.duzceuni.denemeapplication)

## 🛠️ Installation (For Developers)

```bash
git clone https://github.com/yourusername/bookcase-online.git
