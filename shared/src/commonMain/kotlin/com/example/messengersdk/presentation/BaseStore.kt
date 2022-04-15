package com.example.messengersdk.presentation

import co.touchlab.kermit.Logger
import com.example.messengersdk.dispatchers.ioDispatcher
import com.example.messengersdk.dispatchers.uiDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

@OptIn(ObsoleteCoroutinesApi::class)
abstract class BaseStore<State, Action, Effect> : KoinComponent {

    protected abstract val stateFlow: MutableStateFlow<State>
    protected abstract val sideEffectsFlow: MutableSharedFlow<Effect>

    protected val scope: CoroutineScope = CoroutineScope(uiDispatcher + SupervisorJob())

    protected suspend fun updateState(reduceState: (state: State) -> State) {
        Logger.d("[STORE]: ${this::class.simpleName} update state")
        stateFlow.emit(reduceState(stateFlow.value))
    }

    protected abstract suspend fun dispatch(action: Action, initialState: State)

    // TODO: rename to dispatch or accept
    fun dispatch(action: Action) {
        // TODO: need disable on prod build because used reflection
        Logger.d("[STORE]: ${this::class.simpleName} reduce action ${action!!::class.qualifiedName}")
        scope.launch(ioDispatcher) {
            dispatch(action, stateFlow.value)
        }
    }

    fun observeState(): StateFlow<State> = stateFlow

    fun observeSideEffects(): Flow<Effect> = sideEffectsFlow
}