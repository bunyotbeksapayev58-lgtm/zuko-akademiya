package uz.ziyoakademiya.app.core.domain.engine

import uz.ziyoakademiya.app.core.domain.model.StudyPlanDay
import uz.ziyoakademiya.app.core.domain.model.Topic
import java.time.LocalDate
import java.time.temporal.ChronoUnit

object StudyPlannerEngine {

    const val DEFAULT_EXAM_DATE_STRING = "2026-11-28" // Late November 2026 default planning date

    /**
     * Calculates days remaining until the target examination date.
     * Uses LocalDate.now() or provided current date.
     * Never returns negative if exam is in the future.
     */
    fun calculateDaysRemaining(
        examDateStr: String = DEFAULT_EXAM_DATE_STRING,
        currentDate: LocalDate = LocalDate.now()
    ): Long {
        return try {
            val examDate = LocalDate.parse(examDateStr)
            val days = ChronoUnit.DAYS.between(currentDate, examDate)
            if (days < 0) 0 else days
        } catch (e: Exception) {
            0
        }
    }

    /**
     * Generates a personalized 42-day study plan covering mathematics curriculum,
     * prioritizing logarithms, trigonometry, functions, derivatives, integrals, and prisms.
     */
    fun generate42DayPlan(
        topics: List<Topic>,
        dailyMinutes: Int = 45,
        completedDays: Set<Int> = emptySet()
    ): List<StudyPlanDay> {
        val plan = mutableListOf<StudyPlanDay>()

        // Curated 42-day curriculum syllabus
        val roadmap = listOf(
            // Week 1: Logarifmlar va Ko'rsatkichli ifodalar
            Triple("topic_logarithms", "Logarifm tushunchasi va asosiy xossalari", false),
            Triple("topic_logarithms", "Logarifmik tenglamalar va aniqlanish sohasi", false),
            Triple("topic_logarithms", "Logarifmik tengsizliklar va murakkab masalalar", false),
            Triple("topic_powers_roots", "Darajalar va ildizlar bilan amallar", false),
            Triple("topic_powers_roots", "Irrasional tenglamalar va cheklovlar", false),
            Triple(null, "Haftalik mustahkamlash: Logarifmlar bo'yicha mini-test", false),
            Triple(null, "Dam olish va erkin takrorlash kuni", true), // Day 7: Rest

            // Week 2: Trigonometriya
            Triple("topic_trigonometry", "Birlik aylana va burchak o'lchovlari (gradus, radian)", false),
            Triple("topic_trigonometry", "Asosiy trigonometrik ayniyatlar va keltirish formulalari", false),
            Triple("topic_trigonometry", "Qo'shish formulalari va ko'paytmaga keltirish", false),
            Triple("topic_trigonometry", "Trigonometrik tenglamalar: sin(x), cos(x), tg(x)", false),
            Triple("topic_trigonometry", "Trigonometrik tenglamalar tizimlari va oraliqdagi ildizlar", false),
            Triple(null, "Trigonometriya bo'yicha diagnostik oraliq nazorat", false),
            Triple(null, "Dam olish kuni", true), // Day 14: Rest

            // Week 3: Funksiyalar va Grafiglar
            Triple("topic_functions", "Funksiya aniqlanish va qiymatlar sohasi", false),
            Triple("topic_functions", "Funksiya grafigi va uning geometrik almashtirishlari", false),
            Triple("topic_functions", "Murakkab va teskari funksiyalar", false),
            Triple("topic_functions", "Funksiyalarning o'sishi, kamayishi va nollari", false),
            Triple("topic_progressions", "Arifmetik va geometrik progressiyalar", false),
            Triple(null, "Funksiyalar va progressiyalar umumiy tahlili", false),
            Triple(null, "Dam olish kuni", true), // Day 21: Rest

            // Week 4: Hosila va uning tatbiqlari
            Triple("topic_derivatives", "Hosila tushunchasi va hisoblash qoidalari", false),
            Triple("topic_derivatives", "Urinma tenglamasi va geometrik ma'nosi", false),
            Triple("topic_derivatives", "Funksiyaning monotonlik oraliqlari va ekstremumlari", false),
            Triple("topic_derivatives", "Eng katta va eng kichik qiymatlarni topish masalalari", false),
            Triple("topic_derivatives", "Murakkab funksiya hosilasi amaliyoti", false),
            Triple(null, "Hosila tatbiqlari bo'yicha oraliq nazorat", false),
            Triple(null, "Dam olish kuni", true), // Day 28: Rest

            // Week 5: Integrallar va Fazoviy Geometriya (Prizmalar)
            Triple("topic_integrals", "Boshlang'ich funksiya va aniqmas integral", false),
            Triple("topic_integrals", "Aniq integral (Nyuton-Leybnis formulasi)", false),
            Triple("topic_integrals", "Egri chiziqli trapetsiya yuzi", false),
            Triple("topic_prisms", "Prizmalar: To'g'ri va og'ma prizmalar ta'rifi", false),
            Triple("topic_prisms", "Prizma hajmi va to'la sirti hisoblash formulalari", false),
            Triple("topic_prisms", "To'g'ri to'rtburchakli parallelepiped va kub masalalari", false),
            Triple(null, "Dam olish kuni", true), // Day 35: Rest

            // Week 6: DXX yo'nalishi va Milliy Sertifikat Katta Tayyorgarligi
            Triple("topic_geometry_plane", "Planimetriya: Uchburchaklar va aylanalar integratsiyasi", false),
            Triple("topic_vectors", "Koordinatalar va vektorlar metodidan foydalanish", false),
            Triple("topic_combinatorics", "Kombinatorika va ehtimollar nazariyasi", false),
            Triple(null, "Barcha zaif mavzular bo'yicha to'liq qaytarish", false),
            Triple(null, "Namunaviy Milliy Sertifikat Blok Testi (To'liq format)", false),
            Triple(null, "Natijalar tahlili va imtihon oldi maslahatlar", false),
            Triple(null, "Katta Yakuniy Sinov: DXX va Milliy Sertifikat Katta Mock", false) // Day 42
        )

        for (i in roadmap.indices) {
            val dayNum = i + 1
            val (topicId, title, isRest) = roadmap[i]
            val topicObj = topics.find { it.id == topicId }
            val topicTitle = topicObj?.title ?: if (isRest) "Dam olish kuni" else "Maxsus takrorlash moduli"
            val isReview = !isRest && topicId == null

            val targetQuestions = if (isRest) 0 else if (dayNum % 7 == 6 || dayNum == 42) 25 else 12

            plan.add(
                StudyPlanDay(
                    dayNumber = dayNum,
                    title = title,
                    topicId = topicId,
                    topicTitle = topicTitle,
                    description = if (isRest) {
                        "Miyani dam oldirish va o'rganilgan bilimlarni mustahkamlash kuni."
                    } else {
                        "Ushbu kunda nazariy qoidalar bilan tanishib, kamida $targetQuestions ta original mashq yechish tavsiya etiladi."
                    },
                    targetMinutes = if (isRest) 0 else dailyMinutes,
                    targetQuestionsCount = targetQuestions,
                    isRestDay = isRest,
                    isReviewDay = isReview,
                    isCompleted = completedDays.contains(dayNum)
                )
            )
        }

        return plan
    }
}
