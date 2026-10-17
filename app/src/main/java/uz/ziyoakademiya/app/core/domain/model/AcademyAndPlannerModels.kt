package uz.ziyoakademiya.app.core.domain.model

data class AcademyRequirement(
    val id: String,
    val title: String,
    val category: String, // "Hujjatlar", "Tibbiy ko'rik", "Jismoniy tayyorgarlik", "Fan imtihonlari", "Suhbat"
    val description: String,
    val officialSource: String,
    val sourceUrl: String,
    val isVerified: Boolean,
    val lastVerifiedDate: String,
    val verificationNotice: String
)

data class StudyPlanDay(
    val dayNumber: Int,
    val title: String,
    val topicId: String?,
    val topicTitle: String,
    val description: String,
    val targetMinutes: Int,
    val targetQuestionsCount: Int,
    val isRestDay: Boolean = false,
    val isReviewDay: Boolean = false,
    val isCompleted: Boolean = false
)

data class UserProgressSummary(
    val streakDays: Int,
    val totalStudyMinutes: Int,
    val totalQuestionsAnswered: Int,
    val totalCorrectAnswers: Int,
    val topicMasteryPercentage: Map<String, Int>, // topicId -> mastery %
    val weakTopicIds: List<String>
)
