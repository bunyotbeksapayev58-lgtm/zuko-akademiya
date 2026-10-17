package uz.ziyoakademiya.app.ui.screens.academy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import uz.ziyoakademiya.app.core.data.repository.AcademyRepository
import uz.ziyoakademiya.app.core.domain.model.AcademyRequirement

data class AcademyUiState(
    val requirements: List<AcademyRequirement> = emptyList(),
    val selectedCategory: String = "Barchasi",
    val categories: List<String> = emptyList()
)

class AcademyViewModel(
    private val academyRepository: AcademyRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AcademyUiState())
    val uiState: StateFlow<AcademyUiState> = _uiState.asStateFlow()

    init {
        val all = academyRepository.getRequirements()
        val cats = listOf("Barchasi") + all.map { it.category }.distinct()
        _uiState.value = AcademyUiState(
            requirements = all,
            selectedCategory = "Barchasi",
            categories = cats
        )
    }

    fun selectCategory(category: String) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
    }

    class Factory(private val academyRepository: AcademyRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AcademyViewModel(academyRepository) as T
        }
    }
}
