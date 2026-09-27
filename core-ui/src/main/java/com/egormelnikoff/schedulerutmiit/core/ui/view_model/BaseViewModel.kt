package com.egormelnikoff.schedulerutmiit.core.ui.view_model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

abstract class BaseViewModel<State, Action>(initialState: State) : ViewModel() {
    private var isInitialized = false

    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<State> = _state.asStateFlow()

    protected val screenState: State
        get() = _state.value

    protected fun updateState(block: State.() -> State) {
        _state.update { it.block() }
    }

    fun initScreen() {
        if (!isInitialized) {
            isInitialized = true
            onScreenInitialized()
        }
    }

    protected open fun onScreenInitialized() {}

    abstract fun onAction(action: Action)
}

@Composable
fun BaseViewModel<*, *>.initialize() {
    LaunchedEffect(this) {
        initScreen()
    }
}
