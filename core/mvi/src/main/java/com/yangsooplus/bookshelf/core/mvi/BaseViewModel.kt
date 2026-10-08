package com.yangsooplus.bookshelf.core.mvi

import android.util.Log
import androidx.annotation.CallSuper
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

abstract class BaseViewModel<I : Intent, S : State, E : Effect, R : Reducer<S>>(
    initialState: S
) : ViewModel() {

    @CallSuper
    open fun intent(intent: I) {
        log("intent: ${intent::class.java.name}, $intent")
    }

    abstract suspend fun onHandleException(coroutineContext: CoroutineContext, throwable: Throwable)

    protected val internalExceptionHandler =
        CoroutineExceptionHandler { coroutineContext, throwable ->
            log("internalExceptionHandler: ${throwable.message}")
            log(throwable.stackTraceToString())
            viewModelScope.launch {
                onHandleException(coroutineContext, throwable)
            }
        }

    protected val _state = MutableStateFlow(initialState)
    val state: StateFlow<S> = _state.asStateFlow()

    protected val _effect = MutableSharedFlow<E>(replay = 0)
    val effect: SharedFlow<E> = _effect.asSharedFlow()

    suspend inline fun withCurrentState(block: suspend (S) -> Unit) {
        block(state.value)
    }

    suspend fun emitReducer(reducer: R) {
        log("emitReducer: ${reducer::class.java.name}, $reducer")
        _state.update { currentState -> reducer.reduce(currentState) }
    }

    suspend fun emitEffect(effect: E) {
        log("emitEffect: ${effect::class.java.name}, $effect")
        _effect.emit(effect)
    }

    protected fun log(message: String) {
        if (BuildConfig.DEBUG) {
            Log.d(this::class.java.simpleName, message)
        }
    }

    @VisibleForTesting
    fun setupState(state: S) {
        _state.value = state
    }
}
