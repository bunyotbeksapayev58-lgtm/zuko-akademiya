package uz.ziyoakademiya.app.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import uz.ziyoakademiya.app.core.data.preferences.UserPreferences
import uz.ziyoakademiya.app.core.data.preferences.UserPreferencesRepository
import uz.ziyoakademiya.app.core.data.repository.ExamRepository
import uz.ziyoakademiya.app.core.data.repository.QuestionRepository

data class ProfileUiState(
    val grade: String = "10-sinf",
    val targetExam: String = "Milliy sertifikat va DXX Akademiyasi",
    val targetExamDate: String = "2026-11-28",
    val dailyMinutes: Int = 45,
    val isDarkMode: Boolean = false,
    val useSystemTheme: Boolean = true,
    val totalQuestionsAnswered: Int = 0,
    val totalCorrect: Int = 0,
    val exportJsonString: String? = null,
    val isResetSuccess: Boolean = false
)

class ProfileViewModel(
    private val preferencesRepository: UserPreferencesRepository,
    private val questionRepository: QuestionRepository,
    private val examRepository: ExamRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                preferencesRepository.userPreferencesFlow,
                questionRepository.getAllAttempts()
            ) { prefs: UserPreferences, attempts ->
                ProfileUiState(
                    grade = prefs.grade,
                    targetExam = prefs.targetExam,
                    targetExamDate = prefs.targetExamDate,
                    dailyMinutes = prefs.dailyStudyGoalMinutes,
                    isDarkMode = prefs.isDarkMode,
                    useSystemTheme = prefs.useSystemTheme,
                    totalQuestionsAnswered = attempts.size,
                    totalCorrect = attempts.count { it.isCorrect }
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun updateTargetExamDate(date: String) {
        viewModelScope.launch {
            preferencesRepository.updateTargetExamDate(date)
        }
    }

    fun updateDailyGoal(minutes: Int) {
        viewModelScope.launch {
            preferencesRepository.updateDailyGoal(minutes)
        }
    }

    fun updateGrade(grade: String) {
        viewModelScope.launch {
            val current = _uiState.value
            preferencesRepository.updateExamProfile(
                grade = grade,
                targetExam = current.targetExam,
                targetExamDate = current.targetExamDate,
                dailyMinutes = current.dailyMinutes,
                level = "O'rta daraja"
            )
        }
    }

    fun setTheme(isDark: Boolean, useSystem: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setThemePreference(isDark, useSystem)
        }
    }

    fun exportProgressJson() {
        viewModelScope.launch {
            val attempts = questionRepository.getAllAttempts().first()
            val bookmarks = questionRepository.getAllBookmarks().first()
            val exportData = mapOf(
                "version" to "1.0",
                "app" to "Ziyo Akademiya",
                "targetExamDate" to _uiState.value.targetExamDate,
                "grade" to _uiState.value.grade,
                "totalAttempts" to attempts.size,
                "bookmarksCount" to bookmarks.size,
                "exportedAt" to System.currentTimeMillis()
            )
            val json = Gson().toJson(exportData)
            _uiState.update { it.copy(exportJsonString = json) }
        }
    }

    fun clearExportDialog() {
        _uiState.update { it.copy(exportJsonString = null) }
    }

    fun resetAllUserData() {
        viewModelScope.launch {
            questionRepository.clearAllUserData()
            examRepository.clearAllSessions()
            preferencesRepository.clearAllPreferences()
            _uiState.update { it.copy(isResetSuccess = true) }
        }
    }

    class Factory(
        private val preferencesRepository: UserPreferencesRepository,
        private val questionRepository: QuestionRepository,
        private val examRepository: ExamRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ProfileViewModel(preferencesRepository, questionRepository, examRepository) as T
        }
    }
}
