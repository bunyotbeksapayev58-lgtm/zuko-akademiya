package uz.ziyoakademiya.app.ui.screens.planner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import uz.ziyoakademiya.app.core.data.preferences.UserPreferencesRepository
import uz.ziyoakademiya.app.core.data.repository.CurriculumRepository
import uz.ziyoakademiya.app.core.domain.engine.StudyPlannerEngine
import uz.ziyoakademiya.app.core.domain.model.StudyPlanDay

data class PlannerUiState(
    val daysRemaining: Long = 0,
    val targetExamDate: String = "2026-11-28",
    val dailyMinutes: Int = 45,
    val planDays: List<StudyPlanDay> = emptyList(),
    val completedCount: Int = 0,
    val isLoading: Boolean = true
)

class PlannerViewModel(
    private val preferencesRepository: UserPreferencesRepository,
    private val curriculumRepository: CurriculumRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlannerUiState())
    val uiState: StateFlow<PlannerUiState> = _uiState.asStateFlow()

    init {
        loadPlan()
    }

    private fun loadPlan() {
        viewModelScope.launch {
            combine(
                preferencesRepository.userPreferencesFlow,
                curriculumRepository.getAllTopics()
            ) { prefs, topics ->
                val days = StudyPlannerEngine.calculateDaysRemaining(prefs.targetExamDate)
                val generated = StudyPlannerEngine.generate42DayPlan(
                    topics = topics,
                    dailyMinutes = prefs.dailyStudyGoalMinutes,
                    completedDays = prefs.completedPlanDays
                )
                val completed = generated.count { it.isCompleted }

                PlannerUiState(
                    daysRemaining = days,
                    targetExamDate = prefs.targetExamDate,
                    dailyMinutes = prefs.dailyStudyGoalMinutes,
                    planDays = generated,
                    completedCount = completed,
                    isLoading = false
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun toggleDayCompleted(dayNumber: Int) {
        viewModelScope.launch {
            preferencesRepository.toggleCompletedPlanDay(dayNumber)
        }
    }

    class Factory(
        private val preferencesRepository: UserPreferencesRepository,
        private val curriculumRepository: CurriculumRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PlannerViewModel(preferencesRepository, curriculumRepository) as T
        }
    }
}
