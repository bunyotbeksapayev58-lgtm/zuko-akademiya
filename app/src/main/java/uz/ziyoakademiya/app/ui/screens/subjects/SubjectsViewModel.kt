package uz.ziyoakademiya.app.ui.screens.subjects

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import uz.ziyoakademiya.app.core.data.repository.CurriculumRepository
import uz.ziyoakademiya.app.core.data.repository.QuestionRepository
import uz.ziyoakademiya.app.core.domain.engine.DiagnosticEngine
import uz.ziyoakademiya.app.core.domain.model.Lesson
import uz.ziyoakademiya.app.core.domain.model.Subject
import uz.ziyoakademiya.app.core.domain.model.Topic

data class SubjectsUiState(
    val subjects: List<Subject> = emptyList(),
    val selectedSubjectId: String = "subj_math",
    val topics: List<Topic> = emptyList(),
    val topicMastery: Map<String, Int> = emptyMap(),
    val currentTopic: Topic? = null,
    val currentLesson: Lesson? = null,
    val isLoading: Boolean = true
)

class SubjectsViewModel(
    private val curriculumRepository: CurriculumRepository,
    private val questionRepository: QuestionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SubjectsUiState())
    val uiState: StateFlow<SubjectsUiState> = _uiState.asStateFlow()

    init {
        loadSubjectsAndTopics()
    }

    private fun loadSubjectsAndTopics() {
        viewModelScope.launch {
            combine(
                curriculumRepository.getSubjects(),
                curriculumRepository.getAllTopics(),
                questionRepository.getAllAttempts()
            ) { subjects, allTopics, attempts ->
                val currentSubjectId = _uiState.value.selectedSubjectId
                val filteredTopics = allTopics.filter { it.subjectId == currentSubjectId }
                val mastery = DiagnosticEngine.calculateTopicMastery(allTopics, attempts)

                SubjectsUiState(
                    subjects = subjects,
                    selectedSubjectId = currentSubjectId,
                    topics = filteredTopics,
                    topicMastery = mastery,
                    isLoading = false
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun selectSubject(subjectId: String) {
        viewModelScope.launch {
            curriculumRepository.getTopicsForSubject(subjectId).collect { topics ->
                _uiState.update { current ->
                    current.copy(
                        selectedSubjectId = subjectId,
                        topics = topics
                    )
                }
            }
        }
    }

    fun loadTopicDetail(topicId: String) {
        viewModelScope.launch {
            val topic = curriculumRepository.getTopicById(topicId)
            curriculumRepository.getLessonForTopic(topicId).collect { lesson ->
                _uiState.update { current ->
                    current.copy(
                        currentTopic = topic,
                        currentLesson = lesson
                    )
                }
            }
        }
    }

    class Factory(
        private val curriculumRepository: CurriculumRepository,
        private val questionRepository: QuestionRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SubjectsViewModel(curriculumRepository, questionRepository) as T
        }
    }
}
