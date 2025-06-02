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


## 🔒 Play Store Sürümü Hakkında

Uygulamanın geliştirme ve demo sürümlerinde **GeminiAI destekli öneri sistemi** aktif olarak çalışmaktadır. Ancak Play Store sürümünde, Google Play güvenlik politikaları nedeniyle bu özellik geçici olarak devre dışı bırakılmıştır. Geliştirme süreci devam etmektedir.

📲 Play Store'dan indir:  
[BookCase Online on Google Play](https://play.google.com/store/apps/details?id=com.duzceuni.denemeapplication)

## 📝 Kurulum (Geliştirici için)

```bash
git clone https://github.com/kullaniciAdi/bookcase-online.git
```

## 📱 Ekran Görüntüleri

### Giriş Ekranı
![activity_login](https://github.com/user-attachments/assets/d7b583db-47bc-43bd-a428-348a0e4e3709)

### Kayıt Ekranı
![activity_signup](https://github.com/user-attachments/assets/6ff477b5-52cc-4248-bf4b-b6e03221639c)

### Anasayfa
![activity_main](https://github.com/user-attachments/assets/aeac0f07-e319-45e9-9c82-605b7c3e6aa7)

### Arama
![activity_book_list_category](https://github.com/user-attachments/assets/41bfd138-1e5c-4ab6-aa77-39523e886c23)

![activity_book_search_by_bar](https://github.com/user-attachments/assets/fb2a0f1c-40aa-4c58-bcef-e305abd2f911)


### Kitap Detayı
![activity_book_detail](https://github.com/user-attachments/assets/1925d6aa-8540-4dba-bb44-9aacf8070e96)

### Sepet

![activity_cart_empty](https://github.com/user-attachments/assets/35951fa7-dbaf-4c17-bb0e-67ad0974bd56)


![activity_cart](https://github.com/user-attachments/assets/c8d31e08-6eca-4f82-9f29-c0ce8be1cef2)

### Profil
![activity_profile_home](https://github.com/user-attachments/assets/0983ef4e-9704-4509-922c-0e2c760f1da0)

### Kullanıcı Bilgileri
![activity_edit_user](https://github.com/user-attachments/assets/3b3b149d-c64c-44c9-8670-b5c608ad6573)

### Favoriler
![activity_favorites](https://github.com/user-attachments/assets/1e3c912d-cfb7-427e-93aa-85107cc3a18d)

### İade Edilenler
![activity_profile_return_books](https://github.com/user-attachments/assets/f2ed22b3-ebf4-4f82-ba3f-d9718052a8ae)

### Satın Alınanlar
![activity_profile_rent_books](https://github.com/user-attachments/assets/1481169f-78db-4576-a3a4-602e72763a3b)

### Gemini
![ai_activity_ask](https://github.com/user-attachments/assets/1164ea61-18cd-4d8b-9fbc-8fedc04e0ee9)


