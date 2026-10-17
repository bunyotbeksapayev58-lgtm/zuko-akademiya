package uz.ziyoakademiya.app.core.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import uz.ziyoakademiya.app.core.domain.model.*

@Entity(tableName = "subjects")
data class SubjectEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val iconName: String,
    val orderIndex: Int
)

@Entity(tableName = "topics")
data class TopicEntity(
    @PrimaryKey val id: String,
    val subjectId: String,
    val title: String,
    val description: String,
    val gradeRange: String,
    val difficulty: String,
    val estimatedMinutes: Int,
    val orderIndex: Int,
    val isPriorityRevision: Boolean
)

@Entity(tableName = "lessons")
data class LessonEntity(
    @PrimaryKey val id: String,
    val topicId: String,
    val title: String,
    val definition: String,
    val prerequisitesJson: String,
    val formulasJson: String,
    val workedExamplesJson: String,
    val commonMistakesJson: String,
    val guidedPracticeJson: String,
    val revisionRecommendation: String
)

@Entity(tableName = "questions")
data class QuestionEntity(
    @PrimaryKey val id: String,
    val subjectId: String,
    val topicId: String,
    val gradeRange: String,
    val type: String, // SINGLE_CHOICE, NUMERIC, etc.
    val difficulty: String,
    val questionText: String,
    val optionsJson: String,
    val correctAnswer: String,
    val solutionStepsJson: String,
    val explanation: String,
    val formulasJson: String,
    val tagsJson: String,
    val estimatedSeconds: Int,
    val contentVersion: Int,
    val sourceType: String,
    val validationStatus: String
)

@Entity(tableName = "attempts")
data class AttemptEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val questionId: String,
    val topicId: String,
    val subjectId: String,
    val selectedAnswer: String,
    val isCorrect: Boolean,
    val timeSpentSeconds: Int,
    val timestamp: Long,
    val examSessionId: String?
)

@Entity(tableName = "mistakes")
data class MistakeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val questionId: String,
    val topicId: String,
    val subjectId: String,
    val lastWrongAnswer: String,
    val attemptCount: Int,
    val isResolved: Boolean,
    val lastAttemptTimestamp: Long
)

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey val questionId: String,
    val topicId: String,
    val timestamp: Long
)

@Entity(tableName = "exam_sessions")
data class ExamSessionEntity(
    @PrimaryKey val sessionId: String,
    val type: String,
    val title: String,
    val questionIdsJson: String,
    val userAnswersJson: String,
    val totalTimeSeconds: Int,
    val remainingTimeSeconds: Int,
    val isCompleted: Boolean,
    val score: Int,
    val totalQuestions: Int,
    val timestamp: Long
)
