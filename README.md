# 📚 BookCase Online

**BookCase Online**, yerel bir kütüphane yönetim sistemini mobil platforma taşıyan, Android için geliştirilmiş modern bir mobil uygulamadır. Uygulama, kullanıcıların kitap arayabilmesini, favorilere eklemesini, kitap stoklarını takip etmesini ve kendi profillerini yönetmesini sağlar.

## 🎯 Proje Amacı

Bu proje, Düzce Üniversitesi Bilgisayar Mühendisliği bölümü "Mobil Programlama" dersi kapsamında geliştirilmiştir. Kullanıcı dostu arayüzü ve Firebase altyapısıyla, küçük-orta ölçekli kütüphanelerin dijitalleşmesini hedefler.

## 👥 Geliştiriciler

- **Eren Sönmez**
- **Eray Özgür**

## 🚀 Özellikler

- 🔐 Kullanıcı Kayıt & Giriş (Firebase Auth)
- 🔍 Gerçek zamanlı kitap arama (`TextWatcher` desteği)
- ⭐ Favorilere kitap ekleme ve görüntüleme
- 📦 Kitap stoklarının takibi
- 📤 Kitap iade işlemleri
- 👤 Profil bilgilerini düzenleme
- 🌙 Koyu tema desteği
- 🌐 İngilizce ve Türkçe dil desteği (`SharedPreferences`)
- 🧠 GeminiAI destekli öneri sistemi *(yalnızca geliştirme sürümünde aktiftir)*
- 🔄 `Intent` yapısıyla ekranlar arası geçiş ve veri aktarımı

## 🧱 Kullanılan Teknolojiler

- **Java** (Android SDK)
- **Firebase Authentication** – kullanıcı yönetimi
- **Firebase Realtime Database** – veri okuma/yazma
- **Firebase Cloud Storage** – kitap kapaklarını saklama
- **SharedPreferences** – tema/dil ayarları
- **RecyclerView** – listeleme işlemleri
- **Material3** – modern UI bileşenleri

## 📱 Ekran Görüntüleri

> *Görseller buraya eklenecek. (Screenshots klasörüne .png veya .jpg dosyalarını ekleyip burada referans verin.)*

## 🔒 Play Store Sürümü Hakkında

Uygulamanın geliştirme ve demo sürümlerinde **GeminiAI destekli öneri sistemi** aktif olarak çalışmaktadır. Ancak Play Store sürümünde, Google Play güvenlik politikaları nedeniyle bu özellik geçici olarak devre dışı bırakılmıştır. Geliştirme süreci devam etmektedir.

📲 Play Store'dan indir:  
[BookCase Online on Google Play](https://play.google.com/store/apps/details?id=com.duzceuni.denemeapplication)

## 📝 Kurulum (Geliştirici için)

```bash
git clone https://github.com/kullaniciAdi/bookcase-online.git
