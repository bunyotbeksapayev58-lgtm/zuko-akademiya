package uz.ziyoakademiya.app.ui.screens.aitutor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import uz.ziyoakademiya.app.core.data.preferences.UserPreferencesRepository
import uz.ziyoakademiya.app.core.data.repository.CurriculumRepository
import uz.ziyoakademiya.app.core.data.repository.QuestionRepository
import uz.ziyoakademiya.app.core.domain.engine.MathEngine
import uz.ziyoakademiya.app.core.domain.model.Lesson
import uz.ziyoakademiya.app.core.domain.model.Topic

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val sender: String, // "user" or "tutor"
    val text: String,
    val hintText: String? = null,
    val isAiGenerated: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

data class AiTutorUiState(
    val messages: List<ChatMessage> = emptyList(),
    val isOnlineEnabled: Boolean = false,
    val currentTopicTitle: String = "Matematika",
    val isThinking: Boolean = false
)

class AiTutorViewModel(
    private val preferencesRepository: UserPreferencesRepository,
    private val curriculumRepository: CurriculumRepository,
    private val questionRepository: QuestionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AiTutorUiState())
    val uiState: StateFlow<AiTutorUiState> = _uiState.asStateFlow()

    private var cachedLessons: List<Lesson> = emptyList()

    init {
        viewModelScope.launch {
            preferencesRepository.userPreferencesFlow.collect { prefs ->
                _uiState.update { it.copy(isOnlineEnabled = prefs.isOnlineAiEnabled) }
            }
        }

        // Welcome message in Uzbek
        _uiState.update {
            it.copy(
                messages = listOf(
                    ChatMessage(
                        sender = "tutor",
                        text = "Assalomu alaykum! Men sizning shaxsiy matematika repetitoringizman. Logarifmlar, trigonometriya, funksiyalar, hosila, integrallar yoki prizma masalalari bo'yicha savolingiz bormi? Yechim bosqichini tekshirishim yoki maslahat (shpora) berishim mumkin.",
                        isAiGenerated = false
                    )
                )
            )
        }
    }

    fun sendMessage(userText: String) {
        val trimmed = userText.trim()
        if (trimmed.isEmpty()) return

        val userMsg = ChatMessage(sender = "user", text = trimmed)
        val currentMsgs = _uiState.value.messages + userMsg
        _uiState.update { it.copy(messages = currentMsgs, isThinking = true) }

        viewModelScope.launch {
            val reply = generateOfflineGroundedResponse(trimmed)
            _uiState.update {
                it.copy(
                    messages = it.messages + reply,
                    isThinking = false
                )
            }
        }
    }

    private suspend fun generateOfflineGroundedResponse(query: String): ChatMessage {
        val lower = query.lowercase()

        // 1. Logarithm queries
        if (lower.contains("log") || lower.contains("logarifm")) {
            return ChatMessage(
                sender = "tutor",
                text = "Logarifm qoidasi: log_a(b) = c bu a^c = b deganidir (a > 0, a != 1, b > 0).\n\n" +
                        "Asosiy namunaviy misollar:\n" +
                        "1) log_2(32) = 5 (chunki 2^5 = 32)\n" +
                        "2) log_3(x - 1) = 2 => x - 1 = 3^2 = 9 => x = 10 (aniqlanish sohasi x > 1 bajariladi)\n\n" +
                        "Muhim eslatma: log_a(x) + log_a(y) = log_a(x * y) bo'ladi, log_a(x + y) emas!",
                hintText = "Logarifmli tenglamalarda har doim dastlab x > 0 cheklovini yozib oling.",
                isAiGenerated = false
            )
        }

        // 2. Trigonometry queries
        if (lower.contains("sin") || lower.contains("cos") || lower.contains("trig") || lower.contains("tg")) {
            return ChatMessage(
                sender = "tutor",
                text = "Trigonometriya asoslari:\n\n" +
                        "• sin^2(x) + cos^2(x) = 1 (har qanday burchak uchun)\n" +
                        "• sin(30°) + cos(60°) = 0.5 + 0.5 = 1\n" +
                        "• 2*sin(x) = 1 tenglamaning [0°; 360°] oraliqdagi ildizlari: x = 30° va x = 150° (sinus I va II chorakda musbat).\n\n" +
                        "Ikkilangan burchak: sin(2x) = 2*sin(x)*cos(x).",
                hintText = "Oraliq berilganida sinus I va II choraklarda, kosinus esa I va IV choraklarda musbat ekanligini unutmang.",
                isAiGenerated = false
            )
        }

        // 3. Functions & Domain
        if (lower.contains("funksiya") || lower.contains("aniqlanish") || lower.contains("f(x)")) {
            return ChatMessage(
                sender = "tutor",
                text = "Funksiyalar va Aniqlanish sohasi:\n\n" +
                        "• Kvadrat ildiz: f(x) = sqrt(2x - 6) da ildiz osti nomanfiy bo'lishi shart: 2x - 6 >= 0 => x >= 3.\n" +
                        "• Funksiya qiymatini hisoblash: f(x) = x^2 - 3x + 2 bo'lsa, f(2) = 2^2 - 3*(2) + 2 = 4 - 6 + 2 = 0.\n" +
                        "• Kasr maxraji: Agar maxrajda x bo'lsa, u 0 ga teng bo'la olmaydi.",
                hintText = "Aniqlanish sohasida 3 ta holatni tekshiring: maxraj != 0, ildiz >= 0, logarifm > 0.",
                isAiGenerated = false
            )
        }

        // 4. Derivatives
        if (lower.contains("hosila") || lower.contains("deriv") || lower.contains("urinma")) {
            return ChatMessage(
                sender = "tutor",
                text = "Hosila olish qoidalari:\n\n" +
                        "• (x^n)' = n * x^(n-1). Masalan: (x^3 - 4x)' = 3x^2 - 4.\n" +
                        "• Urinma burchak koeffitsiyenti: k = y'(x_0). Agar y = 3x^2 - 6x + 1 bo'lsa, y' = 6x - 6. x = 2 nuqtada y'(2) = 6(2) - 6 = 6.\n" +
                        "• Ekstremum nuqtada hosila nolga teng bo'ladi: f'(x) = 0.",
                hintText = "O'zgarmas sonning hosilasi doimo 0 ga teng: (C)' = 0.",
                isAiGenerated = false
            )
        }

        // 5. Integrals
        if (lower.contains("integral") || lower.contains("boshlang'ich") || lower.contains("yuza")) {
            return ChatMessage(
                sender = "tutor",
                text = "Integrallar va Nyuton-Leybnis formulasi:\n\n" +
                        "• Aniqmas integral: int (2x + 3) dx = x^2 + 3x + C (+C o'zgarmasini unutmang).\n" +
                        "• Aniq integral: int_0^2 x dx = [x^2 / 2]_0^2 = 2^2/2 - 0 = 2.\n" +
                        "• Egri chiziqli trapetsiya yuzi musbat bo'lib, aniq integral orqali topiladi.",
                hintText = "Aniq integralni hisoblashda avval yuqori chegarani, so'ngra quyi chegarani qo'yib ayirasiz: F(b) - F(a).",
                isAiGenerated = false
            )
        }

        // 6. Geometry & Prisms
        if (lower.contains("prizma") || lower.contains("hajm") || lower.contains("sirt") || lower.contains("parallelepiped")) {
            return ChatMessage(
                sender = "tutor",
                text = "Fazoviy geometriya (Prizmalar):\n\n" +
                        "• Prizma hajmi: V = S_asos * h. Masalan: S_asos = 18 sm^2 va h = 7 sm bo'lsa, V = 18 * 7 = 126 sm^3.\n" +
                        "• To'g'ri to'rtburchakli parallelepiped to'la sirti: S_to'la = 2*(ab + bc + ac). Masalan 3, 4 va 5 sm bo'lsa: 2*(12 + 20 + 15) = 94 sm^2.\n" +
                        "• Fazoviy diagonal: d^2 = a^2 + b^2 + c^2.",
                hintText = "Prizmaning 2 ta asosi borligini unutmang: S_to'la = S_yon + 2 * S_asos.",
                isAiGenerated = false
            )
        }

        // Default guidance
        return ChatMessage(
            sender = "tutor",
            text = "Savolingiz tushunarli. Mavzuni chuqurroq o'rganish uchun aniq misol yoki formulani yozing. Masalan:\n" +
                    "• 'Logarifm xossalarini tushuntir'\n" +
                    "• 'sin(30) + cos(60) qanday hisoblanadi?'\n" +
                    "• 'Hosila olish qoidalarini ko'rsat'\n" +
                    "• 'Prizma hajmi formulasi qanday?'",
            isAiGenerated = false
        )
    }

    class Factory(
        private val preferencesRepository: UserPreferencesRepository,
        private val curriculumRepository: CurriculumRepository,
        private val questionRepository: QuestionRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AiTutorViewModel(preferencesRepository, curriculumRepository, questionRepository) as T
        }
    }
}
