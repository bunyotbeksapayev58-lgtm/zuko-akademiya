package uz.ziyoakademiya.app.core.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import uz.ziyoakademiya.app.core.data.local.entity.*

@Dao
interface SubjectDao {
    @Query("SELECT * FROM subjects ORDER BY orderIndex ASC")
    fun getAllSubjects(): Flow<List<SubjectEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(subjects: List<SubjectEntity>)
}

@Dao
interface TopicDao {
    @Query("SELECT * FROM topics WHERE subjectId = :subjectId ORDER BY orderIndex ASC")
    fun getTopicsForSubject(subjectId: String): Flow<List<TopicEntity>>

    @Query("SELECT * FROM topics ORDER BY orderIndex ASC")
    fun getAllTopics(): Flow<List<TopicEntity>>

    @Query("SELECT * FROM topics WHERE id = :id LIMIT 1")
    suspend fun getTopicById(id: String): TopicEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(topics: List<TopicEntity>)
}

@Dao
interface LessonDao {
    @Query("SELECT * FROM lessons WHERE topicId = :topicId LIMIT 1")
    fun getLessonForTopic(topicId: String): Flow<LessonEntity?>

    @Query("SELECT * FROM lessons WHERE id = :id LIMIT 1")
    suspend fun getLessonById(id: String): LessonEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(lessons: List<LessonEntity>)
}

@Dao
interface QuestionDao {
    @Query("SELECT * FROM questions WHERE topicId = :topicId")
    fun getQuestionsForTopic(topicId: String): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM questions WHERE id = :id LIMIT 1")
    suspend fun getQuestionById(id: String): QuestionEntity?

    @Query("SELECT * FROM questions WHERE id IN (:ids)")
    suspend fun getQuestionsByIds(ids: List<String>): List<QuestionEntity>

    @Query("SELECT * FROM questions")
    fun getAllQuestions(): Flow<List<QuestionEntity>>

    @Query("SELECT COUNT(*) FROM questions")
    suspend fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(questions: List<QuestionEntity>)
}

@Dao
interface AttemptDao {
    @Query("SELECT * FROM attempts ORDER BY timestamp DESC")
    fun getAllAttempts(): Flow<List<AttemptEntity>>

    @Query("SELECT * FROM attempts WHERE topicId = :topicId ORDER BY timestamp DESC")
    fun getAttemptsForTopic(topicId: String): Flow<List<AttemptEntity>>

    @Query("SELECT * FROM attempts WHERE questionId = :questionId ORDER BY timestamp DESC")
    suspend fun getAttemptsForQuestion(questionId: String): List<AttemptEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(attempt: AttemptEntity): Long

    @Query("DELETE FROM attempts")
    suspend fun clearAll()
}

@Dao
interface MistakeDao {
    @Query("SELECT * FROM mistakes WHERE isResolved = 0 ORDER BY lastAttemptTimestamp DESC")
    fun getUnresolvedMistakes(): Flow<List<MistakeEntity>>

    @Query("SELECT * FROM mistakes ORDER BY lastAttemptTimestamp DESC")
    fun getAllMistakes(): Flow<List<MistakeEntity>>

    @Query("SELECT * FROM mistakes WHERE questionId = :questionId LIMIT 1")
    suspend fun getMistakeByQuestionId(questionId: String): MistakeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(mistake: MistakeEntity): Long

    @Query("UPDATE mistakes SET isResolved = 1 WHERE questionId = :questionId")
    suspend fun markResolved(questionId: String)

    @Query("DELETE FROM mistakes")
    suspend fun clearAll()
}

@Dao
interface BookmarkDao {
    @Query("SELECT * FROM bookmarks ORDER BY timestamp DESC")
    fun getAllBookmarks(): Flow<List<BookmarkEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE questionId = :questionId)")
    fun isBookmarked(questionId: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(bookmark: BookmarkEntity)

    @Query("DELETE FROM bookmarks WHERE questionId = :questionId")
    suspend fun delete(questionId: String)

    @Query("DELETE FROM bookmarks")
    suspend fun clearAll()
}

@Dao
interface ExamSessionDao {
    @Query("SELECT * FROM exam_sessions ORDER BY timestamp DESC")
    fun getAllSessions(): Flow<List<ExamSessionEntity>>

    @Query("SELECT * FROM exam_sessions WHERE sessionId = :sessionId LIMIT 1")
    suspend fun getSessionById(sessionId: String): ExamSessionEntity?

    @Query("SELECT * FROM exam_sessions WHERE isCompleted = 0 ORDER BY timestamp DESC LIMIT 1")
    suspend fun getActiveSession(): ExamSessionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(session: ExamSessionEntity)

    @Query("DELETE FROM exam_sessions")
    suspend fun clearAll()
}
