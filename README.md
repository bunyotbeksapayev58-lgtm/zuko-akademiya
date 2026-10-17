# Ziyo Akademiya (uz.ziyoakademiya.app)

**Ziyo Akademiya** — O‘zbekiston milliy matematika sertifikati hamda Davlat xavfsizlik xizmati (DXX) akademiyasiga kirish imtihonlariga tayyorlanuvchi o‘quvchilar va abituriyentlar uchun maxsus ishlab chiqilgan zamonaviy, to‘liq mahalliy (offline-first) Android ta’lim ilovasi.

---

## 📌 Rasmiy eslatma va halollik bayonoti (Disclaimer)

> **DIQQAT:** Ushbu ilova mustaqil ta’limiy loyiha bo‘lib, O‘zbekiston Respublikasi Davlat xavfsizlik xizmati (DXX), DXX Akademiyasi, Bilim va malakalarni baholash agentligi (sobiq DTM) yoki biron-bir davlat organi tomonidan rasman tasdiqlanmagan va ularga aloqador emas.
> 
> Ilovada keltirilgan DXX akademiyasiga qabul jarayoni, tibbiy ko‘rik, jismoniy tayyorgarlik va hujjatlar talablari ochiq qonunchilik hujjatlari (lex.uz) va namunaviy nizomlarga asoslangan bo‘lib, rasmiy tasdiq talab etadi deb belgilangan. Milliy yoki xalqaro sertifikatlar qabulni kafolatlamaydi.

---

## 🚀 Texnologik stek va arxitektura

- **Dasturlash tili:** Kotlin (1.9.24)
- **Foydalanuvchi interfeysi (UI):** Jetpack Compose, Material 3, Single-Activity arxitekturasi
- **Arxitektura:** MVVM (Model-View-ViewModel), Repository pattern, Kotlin Coroutines & Flow
- **Mahalliy ma'lumotlar bazasi:** Room (2.6.1) — 320 ta original savollar bazasi, darslar, urinishlar, xatolar daftari va saqlangan savollar
- **Sozlamalar va holat:** Jetpack DataStore Preferences (1.1.1)
- **Navigatsiya:** Navigation Compose (2.7.7)
- **Oflayn ta’lim:** Internet aloqasisiz to‘liq ishlaydigan lokal bilimlar bazasi va matematika repetitori
- **Minimal Android talabi:** Android 8.0 (API 26)
- **Target SDK:** Android 14 (API 34)

---

## 📚 O‘quv dasturi va Savollar banki

Ilovada **320 ta original, matematik jihatdan to‘liq tekshirilgan** mashq savollari joylashtirilgan bo‘lib, quyidagi yo‘nalishlarni qamrab oladi:
1. **Arifmetika, kasrlar va foizlar**
2. **Darajalar, ildizlar va ko‘rsatkichli ifodalar**
3. **Algebraik ayniyatlar va qisqa ko‘paytirish formulalari**
4. **Chiziqli va kvadrat tenglamalar, tengsizliklar**
5. **Ratsional va irrasional tenglamalar**
6. **Ko‘rsatkichli tenglamalar**
7. **Logarifmlar va logarifmik tenglamalar** (`log_2(32) = 5`, `log_3(x - 1) = 2` va aniqlanish sohasi)
8. **Tenglamalar sistemalari**
9. **Funksiyalar, aniqlanish sohasi va grafiklar** (`f(x) = sqrt(2x - 6)` uchun `x >= 3`, `f(x) = x^2 - 3x + 2` uchun `f(2) = 0`)
10. **Arifmetik va geometrik progressiyalar**
11. **Trigonometriya** (`sin(30°) + cos(60°) = 1`, `2sin(x) = 1` tenglama yechimlari)
12. **Hosilalar va urinma tenglamasi** (`d/dx(x^3 - 4x) = 3x^2 - 4`, `y'(2) = 6`)
13. **Integrallar va Nyuton-Leybnis formulasi** (`int (2x + 3)dx = x^2 + 3x + C`, `int_0^2 x dx = 2`)
14. **Planimetriya (Uchburchaklar, to‘rtburchaklar, aylanalar)**
15. **Fazoviy geometriya: Prizmalar** (`V = 18 * 7 = 126 sm^3`, parallelepiped `S = 94 sm^2`)
16. **Aylanish jismlari va piramidalar**
17. **Koordinatalar sistemasi va vektorlar**
18. **Kombinatorika va ehtimollar nazariyasi**
19. **DXX harbiy-mantiqiy va analitik fikrlash sinovlari**
20. **O‘zbekiston tarixi va umumiy savodxonlik**

---

## 🛠 Loyihani yig‘ish va ishga tushirish (Build & Run)

### 1. Talablar
- **JDK:** OpenJDK 17 (Temurin tavsiya etiladi)
- **Android SDK:** Platform 34, Build-Tools 34.0.0
- **Gradle:** 8.7 (Gradle wrapper orqali qo‘shilgan)

### 2. Unit testlarni ishga tushirish
Barcha matematik modellar, formula hisoblagichlari, savollar banki tekshiruvi va xavfsiz aralashtirish testlarini bajarish uchun:
```bash
./gradlew testDebugUnitTest
```

### 3. Debug APK faylini yig‘ish
Ilovaning tayyor `.apk` faylini yaratish uchun:
```bash
./gradlew assembleDebug
```
Yaratilgan APK fayl manzili:
```
app/build/outputs/apk/debug/app-debug.apk
```

---

## 📦 GitHub Actions CI/CD

Loyiha `.github/workflows/android-build.yml` fayli orqali GitHub-ga joylashtirilganda avtomatik ravishda:
1. Loyiha kodini yuklab oladi.
2. JDK 17 o‘rnatadi.
3. Barcha testlarni (`testDebugUnitTest`) ishga tushiradi.
4. Debug APK faylini (`assembleDebug`) yig‘adi.
5. Tayyor APK faylni workflow artifact sifatida yuklaydi (`ziyo-akademiya-debug-apk`).

---

## 🔒 Maxfiylik va Xavfsizlik

- Ilova hech qanday shaxsiy ma'lumotlarni (ism-sharif, telefon raqam, joylashuv, kontaktlar) yig'maydi.
- Reklama yoki begona kuzatuv SDK lari mavjud emas.
- Barcha natijalar va testlar tarixi faqat qurilma xotirasida (Room ma'lumotlar bazasida) saqlanadi.
- Ma'lumotlarni eksport qilish (JSON formatida) va to'liq tozalash (Reset) imkoniyati mavjud.
