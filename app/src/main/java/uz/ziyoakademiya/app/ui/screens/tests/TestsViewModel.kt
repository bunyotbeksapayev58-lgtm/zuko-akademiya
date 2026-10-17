package uz.ziyoakademiya.app.ui.screens.tests

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import uz.ziyoakademiya.app.core.data.repository.CurriculumRepository
import uz.ziyoakademiya.app.core.data.repository.ExamRepository
import uz.ziyoakademiya.app.core.data.repository.QuestionRepository
import uz.ziyoakademiya.app.core.domain.engine.MathEngine
import uz.ziyoakademiya.app.core.domain.model.*

data class ShuffledQuestion(
    val originalQuestion: Question,
    val displayOptions: List<QuestionOption>, // Shuffled options
    val optionMapping: Map<String, String> // displayOptionId -> originalOptionId
)

data class TestActiveState(
    val sessionId: String = "",
    val title: String = "",
    val examType: ExamType = ExamType.MOCK_EXAM,
    val questions: List<ShuffledQuestion> = emptyList(),
    val currentIndex: Int = 0,
    val userAnswers: Map<String, String> = emptyMap(), // questionId -> selectedOptionId
    val remainingSeconds: Int = 3600,
    val totalSeconds: Int = 3600,
    val isSubmitted: Boolean = false,
    val completedSession: ExamSession? = null,
    val bookmarkedQuestionIds: Set<String> = emptySet(),
    val isLoading: Boolean = false
)

class TestsViewModel(
    private val questionRepository: QuestionRepository,
    private val examRepository: ExamRepository,
    private val curriculumRepository: CurriculumRepository
) : ViewModel() {

    private val _activeTestState = MutableStateFlow(TestActiveState())
    val activeTestState: StateFlow<TestActiveState> = _activeTestState.asStateFlow()

    private var timerJob: Job? = null

    val bookmarks: StateFlow<List<Bookmark>> = questionRepository.getAllBookmarks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unresolvedMistakes: StateFlow<List<Mistake>> = questionRepository.getUnresolvedMistakes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTopics: StateFlow<List<Topic>> = curriculumRepository.getAllTopics()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun startTopicQuiz(topicId: String) {
        viewModelScope.launch {
            _activeTestState.update { it.copy(isLoading = true) }
            val topic = curriculumRepository.getTopicById(topicId)
            val questions = questionRepository.getQuestionsForTopic(topicId).first()
            val selected = questions.shuffled().take(5.coerceAtMost(questions.size))

            val session = examRepository.createSession(
                type = ExamType.TOPIC_TEST,
                title = "${topic?.title ?: "Mavzu"} testi",
                questionIds = selected.map { it.id },
                totalTimeSeconds = selected.size * 120
            )

            setupTestSession(session, selected)
        }
    }

    fun startDiagnosticTest() {
        viewModelScope.launch {
            _activeTestState.update { it.copy(isLoading = true) }
            val allQuestions = questionRepository.getAllQuestions().first()
            // Pick balanced 15 questions across topics
            val selected = allQuestions.groupBy { it.topicId }
                .values
                .mapNotNull { it.shuffled().firstOrNull() }
                .shuffled()
                .take(15)

            val session = examRepository.createSession(
                type = ExamType.DIAGNOSTIC,
                title = "Matematika bo'yicha diagnostik test",
                questionIds = selected.map { it.id },
                totalTimeSeconds = 15 * 120
            )

            setupTestSession(session, selected)
        }
    }

    fun startMockExam() {
        viewModelScope.launch {
            _activeTestState.update { it.copy(isLoading = true) }
            val allQuestions = questionRepository.getAllQuestions().first()
            val mathQuestions = allQuestions.filter { it.subjectId == "subj_math" }
            val selected = mathQuestions.shuffled().take(30.coerceAtMost(mathQuestions.size))

            val session = examRepository.createSession(
                type = ExamType.MOCK_EXAM,
                title = "Milliy sertifikat namunaviy imtihoni (Mock)",
                questionIds = selected.map { it.id },
                totalTimeSeconds = 3600 // 60 minutes
            )

            setupTestSession(session, selected)
        }
    }

    private fun setupTestSession(session: ExamSession, questions: List<Question>) {
        val shuffledQuestions = questions.map { q ->
            // Deterministic shuffle using question ID hashCode so it doesn't change on recomposition
            val shuffledOpts = q.options.shuffled()
            val mapping = mutableMapOf<String, String>()
            val displayOpts = shuffledOpts.mapIndexed { idx, opt ->
                val displayId = ('A' + idx).toString()
                mapping[displayId] = opt.id
                QuestionOption(id = displayId, text = opt.text)
            }
            ShuffledQuestion(
                originalQuestion = q,
                displayOptions = displayOpts,
                optionMapping = mapping
            )
        }

        _activeTestState.value = TestActiveState(
            sessionId = session.sessionId,
            title = session.title,
            examType = session.type,
            questions = shuffledQuestions,
            currentIndex = 0,
            userAnswers = emptyMap(),
            remainingSeconds = session.totalTimeSeconds,
            totalSeconds = session.totalTimeSeconds,
            isSubmitted = false,
            completedSession = null,
            isLoading = false
        )

        startTimer()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_activeTestState.value.remainingSeconds > 0 && !_activeTestState.value.isSubmitted) {
                delay(1000)
                _activeTestState.update { current ->
                    val newRemaining = (current.remainingSeconds - 1).coerceAtLeast(0)
                    current.copy(remainingSeconds = newRemaining)
                }
            }
            if (_activeTestState.value.remainingSeconds == 0 && !_activeTestState.value.isSubmitted) {
                submitActiveTest()
            }
        }
    }

    fun selectAnswer(questionId: String, displayOptionId: String) {
        val current = _activeTestState.value
        val qItem = current.questions.find { it.originalQuestion.id == questionId } ?: return
        // Map displayed option ID back to original option ID
        val originalOptionId = qItem.optionMapping[displayOptionId] ?: displayOptionId

        val updatedAnswers = current.userAnswers.toMutableMap()
        updatedAnswers[questionId] = originalOptionId

        _activeTestState.update { it.copy(userAnswers = updatedAnswers) }

        // Persist session progress
        viewModelScope.launch {
            examRepository.updateSessionProgress(
                sessionId = current.sessionId,
                userAnswers = updatedAnswers,
                remainingTimeSeconds = current.remainingSeconds
            )
        }
    }

    fun goToQuestion(index: Int) {
        if (index in 0 until _activeTestState.value.questions.size) {
            _activeTestState.update { it.copy(currentIndex = index) }
        }
    }

    fun submitActiveTest() {
        timerJob?.cancel()
        viewModelScope.launch {
            val session = examRepository.submitSession(_activeTestState.value.sessionId)
            _activeTestState.update {
                it.copy(
                    isSubmitted = true,
                    completedSession = session
                )
            }
        }
    }

    fun toggleBookmark(questionId: String, topicId: String) {
        viewModelScope.launch {
            questionRepository.toggleBookmark(questionId, topicId)
        }
    }

    class Factory(
        private val questionRepository: QuestionRepository,
        private val examRepository: ExamRepository,
        private val curriculumRepository: CurriculumRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return TestsViewModel(questionRepository, examRepository, curriculumRepository) as T
        }
    }
}
