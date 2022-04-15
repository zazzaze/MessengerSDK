package com.example.messengersdk.network

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
import org.koin.core.component.KoinComponent

@OptIn(ObsoleteCoroutinesApi::class)
abstract class BaseReducer<Action, Event> : KoinComponent {

    protected abstract val eventsFlow: MutableSharedFlow<Event>

    protected val scope: CoroutineScope = CoroutineScope(ioDispatcher + SupervisorJob())

    abstract protected fun dispatchAction(action: Action)

    fun dispatch(action: Action) {
        Logger.d("[Reducer]: ${this::class.simpleName} dispatch action ${action!!::class.qualifiedName}")
        dispatchAction(action)
    }

    fun observeSideEffects(): Flow<Event> = eventsFlow
}