package uz.ziyoakademiya.app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import uz.ziyoakademiya.app.core.data.preferences.UserPreferences
import uz.ziyoakademiya.app.core.data.preferences.UserPreferencesRepository
import uz.ziyoakademiya.app.core.data.repository.CurriculumRepository
import uz.ziyoakademiya.app.core.data.repository.QuestionRepository
import uz.ziyoakademiya.app.core.domain.engine.DiagnosticEngine
import uz.ziyoakademiya.app.core.domain.engine.StudyPlannerEngine
import uz.ziyoakademiya.app.core.domain.model.Attempt
import uz.ziyoakademiya.app.core.domain.model.Topic

data class HomeUiState(
    val daysRemaining: Long = 0,
    val targetExamDate: String = "2026-11-28",
    val targetExamTitle: String = "Milliy sertifikat va DXX Akademiyasi",
    val todayStudyMinutes: Int = 0,
    val streakDays: Int = 1,
    val totalQuestionsAnswered: Int = 0,
    val totalCorrect: Int = 0,
    val overallMasteryPercentage: Int = 0,
    val continueTopic: Topic? = null,
    val weakTopics: List<Topic> = emptyList(),
    val weeklyDayCounts: List<Int> = List(7) { 0 }, // Mon..Sun attempts
    val isLoading: Boolean = true
)

class HomeViewModel(
    private val preferencesRepository: UserPreferencesRepository,
    private val curriculumRepository: CurriculumRepository,
    private val questionRepository: QuestionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    private fun loadDashboardData() {
        viewModelScope.launch {
            combine(
                preferencesRepository.userPreferencesFlow,
                curriculumRepository.getAllTopics(),
                questionRepository.getAllAttempts()
            ) { prefs: UserPreferences, topics: List<Topic>, attempts: List<Attempt> ->
                val days = StudyPlannerEngine.calculateDaysRemaining(prefs.targetExamDate)
                val totalAnswered = attempts.size
                val correctCount = attempts.count { it.isCorrect }

                // Calculate mastery per topic
                val masteryMap = DiagnosticEngine.calculateTopicMastery(topics, attempts)
                val weak = DiagnosticEngine.identifyWeakTopics(topics, masteryMap)

                val overallMastery = if (topics.isNotEmpty()) {
                    masteryMap.values.sum() / topics.size
                } else 0

                val continueTopic = topics.find { it.id == prefs.lastActiveTopicId }
                    ?: topics.find { it.id == "topic_prisms" }
                    ?: topics.firstOrNull()

                // Weekly attempt activity distribution
                val weeklyCounts = calculateWeeklyActivity(attempts)

                // Today minutes (calculated from real attempts duration)
                val todayStart = System.currentTimeMillis() - 24 * 60 * 60 * 1000
                val todayAttempts = attempts.filter { it.timestamp >= todayStart }
                val todayMinutes = (todayAttempts.sumOf { it.timeSpentSeconds } / 60).coerceAtLeast(0)

                HomeUiState(
                    daysRemaining = days,
                    targetExamDate = prefs.targetExamDate,
                    targetExamTitle = prefs.targetExam,
                    todayStudyMinutes = todayMinutes,
                    streakDays = if (attempts.isNotEmpty()) 1 else 0,
                    totalQuestionsAnswered = totalAnswered,
                    totalCorrect = correctCount,
                    overallMasteryPercentage = overallMastery,
                    continueTopic = continueTopic,
                    weakTopics = weak.take(3),
                    weeklyDayCounts = weeklyCounts,
                    isLoading = false
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    private fun calculateWeeklyActivity(attempts: List<Attempt>): List<Int> {
        val counts = MutableList(7) { 0 }
        val now = System.currentTimeMillis()
        val oneDayMs = 24 * 60 * 60 * 1000L
        for (att in attempts) {
            val dayOffset = ((now - att.timestamp) / oneDayMs).toInt()
            if (dayOffset in 0..6) {
                counts[6 - dayOffset]++
            }
        }
        return counts
    }

    class Factory(
        private val preferencesRepository: UserPreferencesRepository,
        private val curriculumRepository: CurriculumRepository,
        private val questionRepository: QuestionRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HomeViewModel(preferencesRepository, curriculumRepository, questionRepository) as T
        }
    }
}
