package uz.ziyoakademiya.app.core.data.seed

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import uz.ziyoakademiya.app.core.data.local.Converters
import uz.ziyoakademiya.app.core.data.local.ZiyoDatabase
import uz.ziyoakademiya.app.core.data.local.entity.LessonEntity
import uz.ziyoakademiya.app.core.data.local.entity.QuestionEntity
import uz.ziyoakademiya.app.core.data.local.entity.SubjectEntity
import uz.ziyoakademiya.app.core.data.local.entity.TopicEntity
import uz.ziyoakademiya.app.core.domain.model.*
import java.io.InputStreamReader

object DatabaseSeeder {

    suspend fun seedDatabaseIfEmpty(context: Context, database: ZiyoDatabase) = withContext(Dispatchers.IO) {
        val gson = Gson()
        val assetManager = context.assets

        // 1. Seed Subjects
        try {
            assetManager.open("subjects.json").use { inputStream ->
                InputStreamReader(inputStream).use { reader ->
                    val type = object : TypeToken<List<Subject>>() {}.type
                    val subjects: List<Subject> = gson.fromJson(reader, type)
                    val entities = subjects.map { s ->
                        SubjectEntity(
                            id = s.id,
                            title = s.title,
                            description = s.description,
                            iconName = s.iconName,
                            orderIndex = s.orderIndex
                        )
                    }
                    database.subjectDao().insertAll(entities)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // 2. Seed Topics
        try {
            assetManager.open("topics.json").use { inputStream ->
                InputStreamReader(inputStream).use { reader ->
                    val type = object : TypeToken<List<Topic>>() {}.type
                    val topics: List<Topic> = gson.fromJson(reader, type)
                    val entities = topics.map { t ->
                        TopicEntity(
                            id = t.id,
                            subjectId = t.subjectId,
                            title = t.title,
                            description = t.description,
                            gradeRange = t.gradeRange,
                            difficulty = t.difficulty,
                            estimatedMinutes = t.estimatedMinutes,
                            orderIndex = t.orderIndex,
                            isPriorityRevision = t.isPriorityRevision
                        )
                    }
                    database.topicDao().insertAll(entities)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // 3. Seed Lessons
        try {
            assetManager.open("lessons.json").use { inputStream ->
                InputStreamReader(inputStream).use { reader ->
                    val type = object : TypeToken<List<Lesson>>() {}.type
                    val lessons: List<Lesson> = gson.fromJson(reader, type)
                    val entities = lessons.map { l ->
                        LessonEntity(
                            id = l.id,
                            topicId = l.topicId,
                            title = l.title,
                            definition = l.definition,
                            prerequisitesJson = Converters.fromStringList(l.prerequisites),
                            formulasJson = Converters.fromFormulaList(l.formulas),
                            workedExamplesJson = Converters.fromWorkedExampleList(l.workedExamples),
                            commonMistakesJson = Converters.fromCommonMistakeList(l.commonMistakes),
                            guidedPracticeJson = Converters.fromGuidedPracticeList(l.guidedPractice),
                            revisionRecommendation = l.revisionRecommendation
                        )
                    }
                    database.lessonDao().insertAll(entities)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // 4. Seed Questions
        try {
            assetManager.open("questions.json").use { inputStream ->
                InputStreamReader(inputStream).use { reader ->
                    val type = object : TypeToken<List<Question>>() {}.type
                    val questions: List<Question> = gson.fromJson(reader, type)
                    val entities = questions.map { q ->
                        QuestionEntity(
                            id = q.id,
                            subjectId = q.subjectId,
                            topicId = q.topicId,
                            gradeRange = q.gradeRange,
                            type = q.type.name,
                            difficulty = q.difficulty,
                            questionText = q.questionText,
                            optionsJson = Converters.fromOptionList(q.options),
                            correctAnswer = q.correctAnswer,
                            solutionStepsJson = Converters.fromStringList(q.solutionSteps),
                            explanation = q.explanation,
                            formulasJson = Converters.fromStringList(q.formulas),
                            tagsJson = Converters.fromStringList(q.tags),
                            estimatedSeconds = q.estimatedSeconds,
                            contentVersion = q.contentVersion,
                            sourceType = q.sourceType,
                            validationStatus = q.validationStatus
                        )
                    }
                    database.questionDao().insertAll(entities)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
