package name.faerytea.t.express.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import name.faerytea.t.express.App
import name.faerytea.t.express.repository.Repository
import javax.inject.Inject

class SearchVM: ViewModel() {
    private val _uiState: MutableStateFlow<ListState> = MutableStateFlow(ListState.Cities(emptyList()))
    val uiState: Flow<ListState>
        get() = _uiState

    init {
        App.component.inject(this)
    }

    @Inject
    lateinit var repository: Repository

    fun search(text: String) {
        _uiState.tryEmit(ListState.Loading)
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.tryEmit(ListState.Cities(repository.findCity(text)))
        }
    }
}