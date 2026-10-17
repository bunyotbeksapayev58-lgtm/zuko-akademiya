package uz.ziyoakademiya.app.core.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import uz.ziyoakademiya.app.core.data.local.dao.*
import uz.ziyoakademiya.app.core.data.local.entity.*

@Database(
    entities = [
        SubjectEntity::class,
        TopicEntity::class,
        LessonEntity::class,
        QuestionEntity::class,
        AttemptEntity::class,
        MistakeEntity::class,
        BookmarkEntity::class,
        ExamSessionEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class ZiyoDatabase : RoomDatabase() {

    abstract fun subjectDao(): SubjectDao
    abstract fun topicDao(): TopicDao
    abstract fun lessonDao(): LessonDao
    abstract fun questionDao(): QuestionDao
    abstract fun attemptDao(): AttemptDao
    abstract fun mistakeDao(): MistakeDao
    abstract fun bookmarkDao(): BookmarkDao
    abstract fun examSessionDao(): ExamSessionDao

    companion object {
        @Volatile
        private var INSTANCE: ZiyoDatabase? = null

        fun getInstance(context: Context): ZiyoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ZiyoDatabase::class.java,
                    "ziyo_akademiya.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
