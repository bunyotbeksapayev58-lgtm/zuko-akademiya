package uz.ziyoakademiya.app.core.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import uz.ziyoakademiya.app.core.data.local.Converters
import uz.ziyoakademiya.app.core.data.local.dao.AttemptDao
import uz.ziyoakademiya.app.core.data.local.dao.BookmarkDao
import uz.ziyoakademiya.app.core.data.local.dao.MistakeDao
import uz.ziyoakademiya.app.core.data.local.dao.QuestionDao
import uz.ziyoakademiya.app.core.data.local.entity.AttemptEntity
import uz.ziyoakademiya.app.core.data.local.entity.BookmarkEntity
import uz.ziyoakademiya.app.core.data.local.entity.MistakeEntity
import uz.ziyoakademiya.app.core.data.local.entity.QuestionEntity
import uz.ziyoakademiya.app.core.domain.model.*

class QuestionRepository(
    private val questionDao: QuestionDao,
    private val attemptDao: AttemptDao,
    private val mistakeDao: MistakeDao,
    private val bookmarkDao: BookmarkDao
) {

    fun getQuestionsForTopic(topicId: String): Flow<List<Question>> =
        questionDao.getQuestionsForTopic(topicId).map { list -> list.map { it.toDomain() } }

    fun getAllQuestions(): Flow<List<Question>> =
        questionDao.getAllQuestions().map { list -> list.map { it.toDomain() } }

    suspend fun getQuestionById(id: String): Question? =
        questionDao.getQuestionById(id)?.toDomain()

    suspend fun getQuestionsByIds(ids: List<String>): List<Question> =
        questionDao.getQuestionsByIds(ids).map { it.toDomain() }

    suspend fun getQuestionCount(): Int =
        questionDao.getCount()

    suspend fun recordAttempt(attempt: Attempt): Long {
        val rowId = attemptDao.insert(
            AttemptEntity(
                questionId = attempt.questionId,
                topicId = attempt.topicId,
                subjectId = attempt.subjectId,
                selectedAnswer = attempt.selectedAnswer,
                isCorrect = attempt.isCorrect,
                timeSpentSeconds = attempt.timeSpentSeconds,
                timestamp = attempt.timestamp,
                examSessionId = attempt.examSessionId
            )
        )

        // Handle Mistake notebook entry
        if (!attempt.isCorrect) {
            val existing = mistakeDao.getMistakeByQuestionId(attempt.questionId)
            val updated = if (existing != null) {
                existing.copy(
                    lastWrongAnswer = attempt.selectedAnswer,
                    attemptCount = existing.attemptCount + 1,
                    isResolved = false,
                    lastAttemptTimestamp = attempt.timestamp
                )
            } else {
                MistakeEntity(
                    questionId = attempt.questionId,
                    topicId = attempt.topicId,
                    subjectId = attempt.subjectId,
                    lastWrongAnswer = attempt.selectedAnswer,
                    attemptCount = 1,
                    isResolved = false,
                    lastAttemptTimestamp = attempt.timestamp
                )
            }
            mistakeDao.insertOrUpdate(updated)
        } else {
            // If answered correctly, mark any previous mistake as resolved
            mistakeDao.markResolved(attempt.questionId)
        }

        return rowId
    }

    fun getAllAttempts(): Flow<List<Attempt>> =
        attemptDao.getAllAttempts().map { list ->
            list.map {
                Attempt(
                    id = it.id,
                    questionId = it.questionId,
                    topicId = it.topicId,
                    subjectId = it.subjectId,
                    selectedAnswer = it.selectedAnswer,
                    isCorrect = it.isCorrect,
                    timeSpentSeconds = it.timeSpentSeconds,
                    timestamp = it.timestamp,
                    examSessionId = it.examSessionId
                )
            }
        }

    fun getUnresolvedMistakes(): Flow<List<Mistake>> =
        mistakeDao.getUnresolvedMistakes().map { list ->
            list.map {
                Mistake(
                    id = it.id,
                    questionId = it.questionId,
                    topicId = it.topicId,
                    subjectId = it.subjectId,
                    lastWrongAnswer = it.lastWrongAnswer,
                    attemptCount = it.attemptCount,
                    isResolved = it.isResolved,
                    lastAttemptTimestamp = it.lastAttemptTimestamp
                )
            }
        }

    suspend fun resolveMistake(questionId: String) =
        mistakeDao.markResolved(questionId)

    fun isBookmarked(questionId: String): Flow<Boolean> =
        bookmarkDao.isBookmarked(questionId)

    suspend fun toggleBookmark(questionId: String, topicId: String) {
        val currentlyBookmarked = bookmarkDao.isBookmarked(questionId).first()
        if (currentlyBookmarked) {
            bookmarkDao.delete(questionId)
        } else {
            bookmarkDao.insert(BookmarkEntity(questionId, topicId, System.currentTimeMillis()))
        }
    }

    fun getAllBookmarks(): Flow<List<Bookmark>> =
        bookmarkDao.getAllBookmarks().map { list ->
            list.map { Bookmark(it.questionId, it.topicId, it.timestamp) }
        }

    suspend fun clearAllUserData() {
        attemptDao.clearAll()
        mistakeDao.clearAll()
        bookmarkDao.clearAll()
    }
}

fun QuestionEntity.toDomain() = Question(
    id = id,
    subjectId = subjectId,
    topicId = topicId,
    gradeRange = gradeRange,
    type = try {
        QuestionType.valueOf(type)
    } catch (e: Exception) {
        QuestionType.SINGLE_CHOICE
    },
    difficulty = difficulty,
    questionText = questionText,
    options = Converters.toOptionList(optionsJson),
    correctAnswer = correctAnswer,
    solutionSteps = Converters.toStringList(solutionStepsJson),
    explanation = explanation,
    formulas = Converters.toStringList(formulasJson),
    tags = Converters.toStringList(tagsJson),
    estimatedSeconds = estimatedSeconds,
    contentVersion = contentVersion,
    sourceType = sourceType,
    validationStatus = validationStatus
)
