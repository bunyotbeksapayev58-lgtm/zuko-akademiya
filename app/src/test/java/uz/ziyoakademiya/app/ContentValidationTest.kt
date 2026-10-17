package uz.ziyoakademiya.app

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.junit.Assert.*
import org.junit.Test
import uz.ziyoakademiya.app.core.domain.model.Lesson
import uz.ziyoakademiya.app.core.domain.model.Question
import uz.ziyoakademiya.app.core.domain.model.Subject
import uz.ziyoakademiya.app.core.domain.model.Topic
import java.io.File

class ContentValidationTest {

    private val gson = Gson()
    private val assetsDir = File("src/main/assets")

    @Test
    fun testQuestionsJson_integrityAndCounts() {
        val file = File(assetsDir, "questions.json")
        assertTrue("questions.json file must exist in assets", file.exists())

        val type = object : TypeToken<List<Question>>() {}.type
        val questions: List<Question> = gson.fromJson(file.readText(Charsets.UTF_8), type)

        // 1. Minimum counts
        assertTrue("Total questions must be at least 400, found ${questions.size}", questions.size >= 400)

        val mathQuestions = questions.filter { it.subjectId == "subj_math" }
        assertTrue("Math questions must be at least 350, found ${mathQuestions.size}", mathQuestions.size >= 350)

        val dxxQuestions = questions.filter { it.subjectId == "subj_dxx" }
        assertTrue("DXX questions must be at least 30, found ${dxxQuestions.size}", dxxQuestions.size >= 30)

        // 2. Duplicate ID check
        val seenIds = mutableSetOf<String>()
        for (q in questions) {
            assertFalse("Duplicate question ID found: ${q.id}", seenIds.contains(q.id))
            seenIds.add(q.id)

            // 3. Question structure check
            assertTrue("Question text cannot be empty for ${q.id}", q.questionText.isNotBlank())
            assertEquals("Every question must have exactly 4 options for ${q.id}", 4, q.options.size)

            val optionIds = q.options.map { it.id }.toSet()
            assertEquals("Option IDs must be unique for ${q.id}", 4, optionIds.size)
            assertTrue("Correct answer '${q.correctAnswer}' must exist in options for ${q.id}", optionIds.contains(q.correctAnswer))

            assertTrue("Explanation cannot be empty for ${q.id}", q.explanation.isNotBlank())
            assertEquals("Source type must be specified", "Mualliflik asosidagi mashq savoli", q.sourceType)
        }
    }

    @Test
    fun testTopicsAndSubjects_referentialIntegrity() {
        val topicsFile = File(assetsDir, "topics.json")
        val subjectsFile = File(assetsDir, "subjects.json")
        val questionsFile = File(assetsDir, "questions.json")

        val topicType = object : TypeToken<List<Topic>>() {}.type
        val subjectType = object : TypeToken<List<Subject>>() {}.type
        val questionType = object : TypeToken<List<Question>>() {}.type

        val topics: List<Topic> = gson.fromJson(topicsFile.readText(Charsets.UTF_8), topicType)
        val subjects: List<Subject> = gson.fromJson(subjectsFile.readText(Charsets.UTF_8), subjectType)
        val questions: List<Question> = gson.fromJson(questionsFile.readText(Charsets.UTF_8), questionType)

        val subjectIds = subjects.map { it.id }.toSet()
        val topicIds = topics.map { it.id }.toSet()

        assertEquals("Topics count must be exactly 27", 27, topics.size)

        // Every topic belongs to an existing subject
        for (topic in topics) {
            assertTrue("Topic ${topic.id} has invalid subject ${topic.subjectId}", subjectIds.contains(topic.subjectId))
        }

        // Every question belongs to an existing topic and subject
        for (q in questions) {
            assertTrue("Question ${q.id} has invalid topicId ${q.topicId}", topicIds.contains(q.topicId))
            assertTrue("Question ${q.id} has invalid subjectId ${q.subjectId}", subjectIds.contains(q.subjectId))
        }

        // Every topic has at least 10 questions
        for (topic in topics) {
            val count = questions.count { it.topicId == topic.id }
            assertTrue("Topic ${topic.id} must have at least 10 questions, found $count", count >= 10)
        }
    }

    @Test
    fun testLessonsJson_workedExamplesAndFormulas() {
        val lessonsFile = File(assetsDir, "lessons.json")
        val topicsFile = File(assetsDir, "topics.json")

        val lessonType = object : TypeToken<List<Lesson>>() {}.type
        val topicType = object : TypeToken<List<Topic>>() {}.type

        val lessons: List<Lesson> = gson.fromJson(lessonsFile.readText(Charsets.UTF_8), lessonType)
        val topics: List<Topic> = gson.fromJson(topicsFile.readText(Charsets.UTF_8), topicType)

        assertEquals("Lessons count must match topics count (27)", 27, lessons.size)

        val lessonTopicIds = lessons.map { it.topicId }.toSet()
        for (topic in topics) {
            assertTrue("Topic ${topic.id} must have a corresponding lesson", lessonTopicIds.contains(topic.id))
        }

        for (lesson in lessons) {
            assertTrue("Lesson definition must not be empty for ${lesson.id}", lesson.definition.isNotBlank())
            assertTrue("Lesson ${lesson.id} must have at least 3 worked examples", lesson.workedExamples.size >= 3)
            assertTrue("Lesson ${lesson.id} must have formulas", lesson.formulas.isNotEmpty())
            assertTrue("Lesson ${lesson.id} must have common mistakes", lesson.commonMistakes.isNotEmpty())
        }
    }
}
