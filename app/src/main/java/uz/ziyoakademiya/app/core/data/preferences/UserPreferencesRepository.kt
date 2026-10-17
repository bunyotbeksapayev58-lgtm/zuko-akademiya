package uz.ziyoakademiya.app.core.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

val Context.userDataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

data class UserPreferences(
    val isOnboardingCompleted: Boolean = false,
    val grade: String = "10-sinf",
    val targetExam: String = "Milliy sertifikat va DXX Akademiyasi",
    val targetExamDate: String = "2026-11-28",
    val dailyStudyGoalMinutes: Int = 45,
    val selfAssessedLevel: String = "O'rta daraja",
    val isDarkMode: Boolean = false,
    val useSystemTheme: Boolean = true,
    val lastActiveTopicId: String = "topic_prisms",
    val completedPlanDays: Set<Int> = emptySet(),
    val isOnlineAiEnabled: Boolean = false,
    val customApiEndpoint: String = ""
)

class UserPreferencesRepository(private val context: Context) {

    private object PreferencesKeys {
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val GRADE = stringPreferencesKey("grade")
        val TARGET_EXAM = stringPreferencesKey("target_exam")
        val TARGET_EXAM_DATE = stringPreferencesKey("target_exam_date")
        val DAILY_STUDY_GOAL_MINUTES = intPreferencesKey("daily_study_goal_minutes")
        val SELF_ASSESSED_LEVEL = stringPreferencesKey("self_assessed_level")
        val IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")
        val USE_SYSTEM_THEME = booleanPreferencesKey("use_system_theme")
        val LAST_ACTIVE_TOPIC_ID = stringPreferencesKey("last_active_topic_id")
        val COMPLETED_PLAN_DAYS = stringSetPreferencesKey("completed_plan_days")
        val IS_ONLINE_AI_ENABLED = booleanPreferencesKey("is_online_ai_enabled")
        val CUSTOM_API_ENDPOINT = stringPreferencesKey("custom_api_endpoint")
    }

    val userPreferencesFlow: Flow<UserPreferences> = context.userDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val completedDays = preferences[PreferencesKeys.COMPLETED_PLAN_DAYS]
                ?.mapNotNull { it.toIntOrNull() }?.toSet() ?: emptySet()

            UserPreferences(
                isOnboardingCompleted = preferences[PreferencesKeys.ONBOARDING_COMPLETED] ?: false,
                grade = preferences[PreferencesKeys.GRADE] ?: "10-sinf",
                targetExam = preferences[PreferencesKeys.TARGET_EXAM] ?: "Milliy sertifikat va DXX Akademiyasi",
                targetExamDate = preferences[PreferencesKeys.TARGET_EXAM_DATE] ?: "2026-11-28",
                dailyStudyGoalMinutes = preferences[PreferencesKeys.DAILY_STUDY_GOAL_MINUTES] ?: 45,
                selfAssessedLevel = preferences[PreferencesKeys.SELF_ASSESSED_LEVEL] ?: "O'rta daraja",
                isDarkMode = preferences[PreferencesKeys.IS_DARK_MODE] ?: false,
                useSystemTheme = preferences[PreferencesKeys.USE_SYSTEM_THEME] ?: true,
                lastActiveTopicId = preferences[PreferencesKeys.LAST_ACTIVE_TOPIC_ID] ?: "topic_prisms",
                completedPlanDays = completedDays,
                isOnlineAiEnabled = preferences[PreferencesKeys.IS_ONLINE_AI_ENABLED] ?: false,
                customApiEndpoint = preferences[PreferencesKeys.CUSTOM_API_ENDPOINT] ?: ""
            )
        }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.userDataStore.edit { preferences ->
            preferences[PreferencesKeys.ONBOARDING_COMPLETED] = completed
        }
    }

    suspend fun updateExamProfile(
        grade: String,
        targetExam: String,
        targetExamDate: String,
        dailyMinutes: Int,
        level: String
    ) {
        context.userDataStore.edit { preferences ->
            preferences[PreferencesKeys.GRADE] = grade
            preferences[PreferencesKeys.TARGET_EXAM] = targetExam
            preferences[PreferencesKeys.TARGET_EXAM_DATE] = targetExamDate
            preferences[PreferencesKeys.DAILY_STUDY_GOAL_MINUTES] = dailyMinutes
            preferences[PreferencesKeys.SELF_ASSESSED_LEVEL] = level
        }
    }

    suspend fun updateTargetExamDate(newDate: String) {
        context.userDataStore.edit { preferences ->
            preferences[PreferencesKeys.TARGET_EXAM_DATE] = newDate
        }
    }

    suspend fun updateDailyGoal(minutes: Int) {
        context.userDataStore.edit { preferences ->
            preferences[PreferencesKeys.DAILY_STUDY_GOAL_MINUTES] = minutes
        }
    }

    suspend fun setThemePreference(isDark: Boolean, useSystem: Boolean) {
        context.userDataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_DARK_MODE] = isDark
            preferences[PreferencesKeys.USE_SYSTEM_THEME] = useSystem
        }
    }

    suspend fun setLastActiveTopic(topicId: String) {
        context.userDataStore.edit { preferences ->
            preferences[PreferencesKeys.LAST_ACTIVE_TOPIC_ID] = topicId
        }
    }

    suspend fun toggleCompletedPlanDay(dayNumber: Int) {
        context.userDataStore.edit { preferences ->
            val current = preferences[PreferencesKeys.COMPLETED_PLAN_DAYS] ?: emptySet()
            val dayStr = dayNumber.toString()
            val updated = if (current.contains(dayStr)) {
                current - dayStr
            } else {
                current + dayStr
            }
            preferences[PreferencesKeys.COMPLETED_PLAN_DAYS] = updated
        }
    }

    suspend fun updateOnlineAiSettings(enabled: Boolean, endpoint: String) {
        context.userDataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_ONLINE_AI_ENABLED] = enabled
            preferences[PreferencesKeys.CUSTOM_API_ENDPOINT] = endpoint
        }
    }

    suspend fun clearAllPreferences() {
        context.userDataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
