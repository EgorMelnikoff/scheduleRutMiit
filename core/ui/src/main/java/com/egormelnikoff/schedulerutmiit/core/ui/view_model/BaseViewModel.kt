package com.egormelnikoff.schedulerutmiit.core.ui.view_model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

abstract class BaseViewModel<State, UiState, Action>(initialState: State) : ViewModel() {
    private var isInitialized = false

    private val _state = MutableStateFlow(initialState)

    val state: StateFlow<UiState> by lazy {
        _state
            .map { it.toUiState() }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = _state.value.toUiState()
            )
    }

    protected val screenState: State
        get() = _state.value

    protected fun updateState(block: State.() -> State) {
        _state.update { it.block() }
    }

    @Suppress("UNCHECKED_CAST")
    protected open fun State.toUiState(): UiState = this as UiState

    fun initScreen() {
        if (!isInitialized) {
            isInitialized = true
            onScreenInitialized()
        }
    }

    protected open fun onScreenInitialized() {}

    protected fun launchToCoroutine(
        handler: CoroutineExceptionHandler,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        job: Job? = null,
        block: suspend CoroutineScope.() -> Unit
    ): Job {
        val context = if (job != null) {
            dispatcher + handler + job
        } else {
            dispatcher + handler
        }

        return viewModelScope.launch(context) {
            block(this)
        }
    }

    abstract fun onAction(action: Action)
}

@Composable
fun BaseViewModel<*, *, *>.Initialize() {
    LaunchedEffect(this) {
        initScreen()
    }
}
