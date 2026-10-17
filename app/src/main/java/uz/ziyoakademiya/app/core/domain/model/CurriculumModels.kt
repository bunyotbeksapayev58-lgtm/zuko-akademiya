package uz.ziyoakademiya.app.core.domain.model

data class Subject(
    val id: String,
    val title: String,
    val description: String,
    val iconName: String,
    val orderIndex: Int
)

data class Topic(
    val id: String,
    val subjectId: String,
    val title: String,
    val description: String,
    val gradeRange: String,
    val difficulty: String, // Oson, O'rta, Murakkab
    val estimatedMinutes: Int,
    val orderIndex: Int,
    val isPriorityRevision: Boolean = false
)

data class Lesson(
    val id: String,
    val topicId: String,
    val title: String,
    val definition: String,
    val prerequisites: List<String>,
    val formulas: List<FormulaItem>,
    val workedExamples: List<WorkedExample>,
    val commonMistakes: List<CommonMistake>,
    val guidedPractice: List<GuidedPracticeItem>,
    val revisionRecommendation: String
)

data class FormulaItem(
    val title: String,
    val formula: String,
    val explanation: String
)

data class WorkedExample(
    val id: String,
    val difficulty: String, // Oson, O'rta, Murakkab
    val problemText: String,
    val solutionSteps: List<String>,
    val finalAnswer: String
)

data class CommonMistake(
    val mistakeTitle: String,
    val wrongApproach: String,
    val correctApproach: String,
    val reason: String
)

data class GuidedPracticeItem(
    val id: String,
    val questionText: String,
    val hint: String,
    val answer: String,
    val explanation: String
)
