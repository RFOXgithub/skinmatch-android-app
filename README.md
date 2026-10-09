# SkinMatch (Blushly) – Aplikasi Android

Aplikasi Android untuk mencari dan merekomendasikan produk kecantikan yang cocok dengan jenis kulit pengguna. Dibuat sebagai proyek skripsi.

## Fitur

- **Pengguna**: registrasi, login, reset password, beranda, pencarian produk, detail produk, profil.
- **Admin**: kelola pengguna, produk, dan kategori (tambah, ubah, hapus), serta profil admin.

## Teknologi

- Kotlin + Jetpack Compose (Material 3)
- Navigation Compose
- Firebase Authentication, Realtime Database, Storage, Functions
- Retrofit, Coil, DataStore, WorkManager

## Persyaratan

- Android Studio (versi terbaru) dengan JDK 11+
- Android SDK 35; perangkat/emulator minimal Android 10 (API 29)

## Cara Menjalankan

1. Clone repositori:
   ```bash
   git clone https://github.com/RFOXgithub/skinmatch-android-app.git
   ```
2. Buka folder di Android Studio, tunggu Gradle sync selesai.
3. Pastikan file `app/google-services.json` milik proyek Firebase Anda tersedia.
4. Jalankan dengan tombol **Run**, atau lewat terminal:
   ```bash
   ./gradlew assembleDebug
   ```

## Struktur Proyek

```
app/src/main/java/com/dicoding/skripsirevisi/
├── data/viewmodel/   # ViewModel (login, register, pencarian)
├── navigation/       # Rute & NavGraph
└── ui/
    ├── components/   # Komponen UI bersama
    ├── screens/      # Layar: auth, general, admin*, splash
    └── theme/        # Warna, tema, tipografi
```
