package com.egormelnikoff.schedulerutmiit.core.common.utils

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

abstract class BaseViewModel<State, Action>(initialState: State) : ViewModel() {
    private var isInitialized = false

    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<State> = _state.asStateFlow()

    protected val currentState: State
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
