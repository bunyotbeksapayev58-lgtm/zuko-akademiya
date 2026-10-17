package uz.ziyoakademiya.app.core.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import uz.ziyoakademiya.app.core.data.local.Converters
import uz.ziyoakademiya.app.core.data.local.dao.ExamSessionDao
import uz.ziyoakademiya.app.core.data.local.entity.ExamSessionEntity
import uz.ziyoakademiya.app.core.domain.engine.MathEngine
import uz.ziyoakademiya.app.core.domain.model.*
import java.util.UUID

class ExamRepository(
    private val examSessionDao: ExamSessionDao,
    private val questionRepository: QuestionRepository
) {

    fun getAllSessions(): Flow<List<ExamSession>> =
        examSessionDao.getAllSessions().map { list -> list.map { it.toDomain() } }

    suspend fun getSessionById(sessionId: String): ExamSession? =
        examSessionDao.getSessionById(sessionId)?.toDomain()

    suspend fun getActiveSession(): ExamSession? =
        examSessionDao.getActiveSession()?.toDomain()

    suspend fun createSession(
        type: ExamType,
        title: String,
        questionIds: List<String>,
        totalTimeSeconds: Int
    ): ExamSession {
        val session = ExamSession(
            sessionId = UUID.randomUUID().toString(),
            type = type,
            title = title,
            questionIds = questionIds,
            userAnswers = emptyMap(),
            totalTimeSeconds = totalTimeSeconds,
            remainingTimeSeconds = totalTimeSeconds,
            isCompleted = false,
            score = 0,
            totalQuestions = questionIds.size,
            timestamp = System.currentTimeMillis()
        )
        examSessionDao.insertOrUpdate(session.toEntity())
        return session
    }

    suspend fun updateSessionProgress(
        sessionId: String,
        userAnswers: Map<String, String>,
        remainingTimeSeconds: Int
    ) {
        val session = examSessionDao.getSessionById(sessionId) ?: return
        val updated = session.copy(
            userAnswersJson = Converters.fromStringMap(userAnswers),
            remainingTimeSeconds = remainingTimeSeconds
        )
        examSessionDao.insertOrUpdate(updated)
    }

    suspend fun submitSession(sessionId: String): ExamSession? {
        val entity = examSessionDao.getSessionById(sessionId) ?: return null
        val questionIds = Converters.toStringList(entity.questionIdsJson)
        val userAnswers = Converters.toStringMap(entity.userAnswersJson)
        val questions = questionRepository.getQuestionsByIds(questionIds)

        var correctCount = 0
        for (q in questions) {
            val studentAns = userAnswers[q.id] ?: ""
            val isCorrect = MathEngine.checkAnswer(q.correctAnswer, studentAns)
            if (isCorrect) {
                correctCount++
            }
            // Record individual question attempt
            questionRepository.recordAttempt(
                Attempt(
                    questionId = q.id,
                    topicId = q.topicId,
                    subjectId = q.subjectId,
                    selectedAnswer = studentAns,
                    isCorrect = isCorrect,
                    timeSpentSeconds = (entity.totalTimeSeconds - entity.remainingTimeSeconds) / questionIds.size.coerceAtLeast(1),
                    timestamp = System.currentTimeMillis(),
                    examSessionId = sessionId
                )
            )
        }

        val completedEntity = entity.copy(
            isCompleted = true,
            score = correctCount,
            totalQuestions = questionIds.size
        )
        examSessionDao.insertOrUpdate(completedEntity)
        return completedEntity.toDomain()
    }

    suspend fun clearAllSessions() {
        examSessionDao.clearAll()
    }
}

fun ExamSessionEntity.toDomain() = ExamSession(
    sessionId = sessionId,
    type = try {
        ExamType.valueOf(type)
    } catch (e: Exception) {
        ExamType.MOCK_EXAM
    },
    title = title,
    questionIds = Converters.toStringList(questionIdsJson),
    userAnswers = Converters.toStringMap(userAnswersJson),
    totalTimeSeconds = totalTimeSeconds,
    remainingTimeSeconds = remainingTimeSeconds,
    isCompleted = isCompleted,
    score = score,
    totalQuestions = totalQuestions,
    timestamp = timestamp
)

fun ExamSession.toEntity() = ExamSessionEntity(
    sessionId = sessionId,
    type = type.name,
    title = title,
    questionIdsJson = Converters.fromStringList(questionIds),
    userAnswersJson = Converters.fromStringMap(userAnswers),
    totalTimeSeconds = totalTimeSeconds,
    remainingTimeSeconds = remainingTimeSeconds,
    isCompleted = isCompleted,
    score = score,
    totalQuestions = totalQuestions,
    timestamp = timestamp
)
