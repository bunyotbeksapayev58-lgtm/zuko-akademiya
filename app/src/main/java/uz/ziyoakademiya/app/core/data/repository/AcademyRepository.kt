package uz.ziyoakademiya.app.core.data.repository

import uz.ziyoakademiya.app.core.domain.model.AcademyRequirement

class AcademyRepository {

    fun getRequirements(): List<AcademyRequirement> {
        return listOf(
            AcademyRequirement(
                id = "req_1",
                title = "Nomzodlarga qo'yiladigan umumiy talablar",
                category = "Qabul tartibi",
                description = "O'zbekiston Respublikasi fuqarosi bo'lishi, umumiy o'rta (11-sinf) yoki o'rta maxsus ma'lumotga ega bo'lishi, belgilangan yosh chegarasiga (odatda 17 yoshdan 23 yoshgacha) to'g'ri kelishi lozim.",
                officialSource = "O'zbekiston Respublikasi qonunchiligi (lex.uz)",
                sourceUrl = "https://lex.uz",
                isVerified = true,
                lastVerifiedDate = "2026-10-15",
                verificationNotice = "Umumiy me'yorlar qonunchilikka asoslangan. Joriy qabul mavsumi uchun aniq yosh va kvotalar rasmiy e'lon orqali tasdiqlanishi kerak."
            ),
            AcademyRequirement(
                id = "req_2",
                title = "Ariza topshirish va dastlabki saralash muddatlari",
                category = "Qabul tartibi",
                description = "Nomzodlar o'z yashash joyidagi hududiy DXX boshqarmalariga belgilangan muddatlarda (odatda qabul yili boshidan mart-aprel oylariga qadar) ariza bilan murojaat qiladilar.",
                officialSource = "Ochiq qabul yo'riqnomalari",
                sourceUrl = "https://lex.uz",
                isVerified = false,
                lastVerifiedDate = "2026-10-15",
                verificationNotice = "DIQQAT: Ariza qabul qilish muddatlari har yili rasmiy ravishda o'zgarishi mumkin. Hududiy bo'limdan shaxsan aniqlashtirish shart."
            ),
            AcademyRequirement(
                id = "req_3",
                title = "Kerakli hujjatlar to'plami (Checklist)",
                category = "Hujjatlar",
                description = "Fuqarolik pasporti yoki ID-karta nusxasi; tug'ilganlik haqida guvohnoma; chaqiriluvchining qayd etilganligi to'g'risidagi guvohnoma (yoki harbiy guvohnoma); ma'lumoti to'g'risidagi attestat/diplom yoki o'qish joyidan ma'lumotnoma; tarjimai hol va tavsifnoma; belgilangan o'lchamdagi fotosuratlar.",
                officialSource = "Namunaviy harbiy-ta'limiy nizomlar",
                sourceUrl = "https://lex.uz",
                isVerified = true,
                lastVerifiedDate = "2026-10-15",
                verificationNotice = "Hujjatlarning to'liq ro'yxati va shakli qabul komissiyasi tomonidan beriladigan yo'riqnomada aniq ko'rsatiladi."
            ),
            AcademyRequirement(
                id = "req_4",
                title = "Tibbiy ko'rik va harbiy-tibbiy komissiya",
                category = "Tibbiy ko'rik",
                description = "Harbiy xizmatga va maxsus ta'lim muassasasida o'qishga yaroqlilik darajasini aniqlash. Ko'rish o'tkirligi, yurak-qon tomir tizimi, bo'y-vazn mutanosibligi va surunkali kasalliklarning yo'qligi qat'iy tekshiriladi.",
                officialSource = "Harbiy-tibbiy ekspertiza to'g'risidagi nizom",
                sourceUrl = "https://lex.uz",
                isVerified = true,
                lastVerifiedDate = "2026-10-15",
                verificationNotice = "Tibbiy ko'rik qat'iy standartlarga ega bo'lib, har qanday sertifikat mavjudligidan qat'i nazar nomzod shaxsan ko'rikdan o'tishi shart."
            ),
            AcademyRequirement(
                id = "req_5",
                title = "Jismoniy tayyorgarlik sinovlari",
                category = "Jismoniy tayyorgarlik",
                description = "Nomzodlarning jismoniy chidamliligi va kuchini baholash: turnikda tortilish (kamida 12-14 marta), 100 metrga yugurish (tezlik) va 3000 metrga yugurish (chidamlilik).",
                officialSource = "Jismoniy tayyorgarlik me'yorlari",
                sourceUrl = "https://lex.uz",
                isVerified = false,
                lastVerifiedDate = "2026-10-15",
                verificationNotice = "Ball berish mezonlari va normativlar yildan-yilga o'zgartirilishi mumkin. Muntazam shug'ullanish tavsiya etiladi."
            ),
            AcademyRequirement(
                id = "req_6",
                title = "Psixologik test va individual suhbat",
                category = "Psixologik saralash",
                description = "Nomzodning aqliy faolligi, stressga chidamliligi, mantiqiy fikrlash tezligi, harbiy-vatanparvarlik ruhi va axloqiy fazilatlarini o'rganish.",
                officialSource = "Kasbiy tanlov metodikasi",
                sourceUrl = "https://lex.uz",
                isVerified = true,
                lastVerifiedDate = "2026-10-15",
                verificationNotice = "Psixologik saralash natijalari nomzodning qabul jarayonidagi eng muhim bosqichlaridan biridir."
            ),
            AcademyRequirement(
                id = "req_7",
                title = "Fanlar bo'yicha test sinovlari (Matematika va Chet tili)",
                category = "Fan imtihonlari",
                description = "Matematika, ona tili va adabiyot, O'zbekiston tarixi hamda chet tili bo'yicha bilim darajasi tekshiriladi. Matematika va chet tili bo'yicha milliy yoki xalqaro sertifikatlar qonunchilikda belgilangan tartibda imtiyoz berishi mumkin.",
                officialSource = "Davlat test markazi / Ta'lim sohasidagi qarorlar",
                sourceUrl = "https://lex.uz",
                isVerified = false,
                lastVerifiedDate = "2026-10-15",
                verificationNotice = "Sertifikat mavjudligi boshqa bosqichlardan (tibbiy, jismoniy, maxsus tekshiruv) ozod qilmaydi va avtomatik qabulni kafolatlamaydi."
            )
        )
    }
}
