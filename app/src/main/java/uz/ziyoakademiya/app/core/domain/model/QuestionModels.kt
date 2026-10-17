package uz.ziyoakademiya.app.core.domain.model

enum class QuestionType {
    SINGLE_CHOICE,
    NUMERIC,
    TRUE_FALSE,
    MULTI_SELECT
}

data class QuestionOption(
    val id: String, // e.g. "A", "B", "C", "D"
    val text: String
)

data class Question(
    val id: String,
    val subjectId: String,
    val topicId: String,
    val gradeRange: String,
    val type: QuestionType,
    val difficulty: String, // Oson, O'rta, Murakkab
    val questionText: String,
    val options: List<QuestionOption>,
    val correctAnswer: String,
    val solutionSteps: List<String>,
    val explanation: String,
    val formulas: List<String> = emptyList(),
    val tags: List<String> = emptyList(),
    val estimatedSeconds: Int = 120,
    val contentVersion: Int = 1,
    val sourceType: String = "Mualliflik asosidagi mashq savoli",
    val validationStatus: String = "VERIFIED"
)

data class Attempt(
    val id: Long = 0,
    val questionId: String,
    val topicId: String,
    val subjectId: String,
    val selectedAnswer: String,
    val isCorrect: Boolean,
    val timeSpentSeconds: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val examSessionId: String? = null
)

data class Mistake(
    val id: Long = 0,
    val questionId: String,
    val topicId: String,
    val subjectId: String,
    val lastWrongAnswer: String,
    val attemptCount: Int,
    val isResolved: Boolean = false,
    val lastAttemptTimestamp: Long = System.currentTimeMillis()
)

data class Bookmark(
    val questionId: String,
    val topicId: String,
    val timestamp: Long = System.currentTimeMillis()
)

enum class ExamType {
    DIAGNOSTIC,
    TOPIC_TEST,
    MOCK_EXAM
}

data class ExamSession(
    val sessionId: String,
    val type: ExamType,
    val title: String,
    val questionIds: List<String>,
    val userAnswers: Map<String, String>, // questionId -> answer
    val totalTimeSeconds: Int,
    val remainingTimeSeconds: Int,
    val isCompleted: Boolean,
    val score: Int = 0,
    val totalQuestions: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
)
