package uz.ziyoakademiya.app.core.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import uz.ziyoakademiya.app.core.data.local.Converters
import uz.ziyoakademiya.app.core.data.local.dao.LessonDao
import uz.ziyoakademiya.app.core.data.local.dao.SubjectDao
import uz.ziyoakademiya.app.core.data.local.dao.TopicDao
import uz.ziyoakademiya.app.core.data.local.entity.LessonEntity
import uz.ziyoakademiya.app.core.data.local.entity.SubjectEntity
import uz.ziyoakademiya.app.core.data.local.entity.TopicEntity
import uz.ziyoakademiya.app.core.domain.model.*

class CurriculumRepository(
    private val subjectDao: SubjectDao,
    private val topicDao: TopicDao,
    private val lessonDao: LessonDao
) {

    fun getSubjects(): Flow<List<Subject>> =
        subjectDao.getAllSubjects().map { list -> list.map { it.toDomain() } }

    fun getTopicsForSubject(subjectId: String): Flow<List<Topic>> =
        topicDao.getTopicsForSubject(subjectId).map { list -> list.map { it.toDomain() } }

    fun getAllTopics(): Flow<List<Topic>> =
        topicDao.getAllTopics().map { list -> list.map { it.toDomain() } }

    suspend fun getTopicById(id: String): Topic? =
        topicDao.getTopicById(id)?.toDomain()

    fun getLessonForTopic(topicId: String): Flow<Lesson?> =
        lessonDao.getLessonForTopic(topicId).map { it?.toDomain() }

    suspend fun getLessonById(id: String): Lesson? =
        lessonDao.getLessonById(id)?.toDomain()
}

fun SubjectEntity.toDomain() = Subject(
    id = id,
    title = title,
    description = description,
    iconName = iconName,
    orderIndex = orderIndex
)

fun TopicEntity.toDomain() = Topic(
    id = id,
    subjectId = subjectId,
    title = title,
    description = description,
    gradeRange = gradeRange,
    difficulty = difficulty,
    estimatedMinutes = estimatedMinutes,
    orderIndex = orderIndex,
    isPriorityRevision = isPriorityRevision
)

fun LessonEntity.toDomain() = Lesson(
    id = id,
    topicId = topicId,
    title = title,
    definition = definition,
    prerequisites = Converters.toStringList(prerequisitesJson),
    formulas = Converters.toFormulaList(formulasJson),
    workedExamples = Converters.toWorkedExampleList(workedExamplesJson),
    commonMistakes = Converters.toCommonMistakeList(commonMistakesJson),
    guidedPractice = Converters.toGuidedPracticeList(guidedPracticeJson),
    revisionRecommendation = revisionRecommendation
)
