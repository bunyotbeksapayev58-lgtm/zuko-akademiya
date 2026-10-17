package uz.ziyoakademiya.app

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import uz.ziyoakademiya.app.core.data.local.ZiyoDatabase
import uz.ziyoakademiya.app.core.data.preferences.UserPreferencesRepository
import uz.ziyoakademiya.app.core.data.repository.AcademyRepository
import uz.ziyoakademiya.app.core.data.repository.CurriculumRepository
import uz.ziyoakademiya.app.core.data.repository.ExamRepository
import uz.ziyoakademiya.app.core.data.repository.QuestionRepository
import uz.ziyoakademiya.app.core.data.seed.DatabaseSeeder

class ZiyoApplication : Application() {

    val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    val database by lazy { ZiyoDatabase.getInstance(this) }
    val userPreferencesRepository by lazy { UserPreferencesRepository(this) }
    val curriculumRepository by lazy {
        CurriculumRepository(
            database.subjectDao(),
            database.topicDao(),
            database.lessonDao()
        )
    }
    val questionRepository by lazy {
        QuestionRepository(
            database.questionDao(),
            database.attemptDao(),
            database.mistakeDao(),
            database.bookmarkDao()
        )
    }
    val examRepository by lazy {
        ExamRepository(
            database.examSessionDao(),
            questionRepository
        )
    }
    val academyRepository by lazy { AcademyRepository() }

    override fun onCreate() {
        super.onCreate()
        applicationScope.launch {
            DatabaseSeeder.seedDatabaseIfEmpty(this@ZiyoApplication, database)
        }
    }
}
