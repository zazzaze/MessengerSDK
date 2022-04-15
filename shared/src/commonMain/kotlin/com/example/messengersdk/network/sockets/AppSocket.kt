@file:OptIn(FlowPreview::class)

package com.example.messengersdk.network.sockets

import co.touchlab.kermit.Logger
import co.touchlab.kermit.LoggerConfig
import com.example.messengersdk.dispatchers.ioDispatcher
import com.example.messengersdk.dispatchers.uiDispatcher
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.koin.core.component.KoinComponent

class AppSocket(url: String, private val scope: CoroutineScope = CoroutineScope(uiDispatcher)): KoinComponent {
    private val ws = PlatformSocket(url, logger = Logger(config = LoggerConfig.default, tag = "Platform Socket"))

    private val _stateSharedFlow = MutableStateFlow<State>(State.CLOSED)
    val stateSharedFlow : StateFlow<State> = _stateSharedFlow

    private val _messagesFlow = MutableSharedFlow<ByteArray>()
    val messagesFlow: SharedFlow<ByteArray> = _messagesFlow

    private val currentState: State
        get() = _stateSharedFlow.value

    fun connect() {
        if (currentState!= State.CLOSED) {
            throw IllegalStateException("The socket is available.")
        }

        ws.openSocket(socketListener)

        scope.launch {
            _stateSharedFlow.emit(State.CONNECTING)
        }
    }

    fun disconnect() {
        if (currentState != State.CLOSED) {
            scope.launch {
                _stateSharedFlow.emit(State.CLOSING)
            }
            ws.closeSocket(1000, "The user has closed the connection.")
        }
    }

    fun sendBytes(bytes: ByteArray) {
        if (currentState != State.CONNECTED) throw IllegalStateException("The connection is lost.")
        ws.sendBytes(bytes = bytes)
    }

    fun sendPingAndReceivePong(callback: (Throwable?) -> Unit) {
        ws.sendPingAndReceivePong {
            if (it != null) {
                scope.launch {
                    _stateSharedFlow.emit(AppSocket.State.LostConnection)
                }
            }
            callback(it)
        }
    }

    private val socketListener: PlatformSocketListener = object : PlatformSocketListener {
        override fun onOpen() {
            scope.launch {
                _stateSharedFlow.emit(State.CONNECTED)
            }
        }
        override fun onFailure(t: Throwable) {
            scope.launch {
                _stateSharedFlow.emit(State.CLOSED)
            }
        }

        override fun onMessage(msgBytes: ByteArray) {
            scope.launch {
                _messagesFlow.emit(msgBytes)
            }
        }
        override fun onClosing(code: Int, reason: String) {
            scope.launch {
                _stateSharedFlow.emit(State.CLOSING)
            }
        }
        override fun onClosed(code: Int, reason: String) {
            scope.launch {
                _stateSharedFlow.emit(State.CLOSED)
            }
        }
    }
    enum class State {
        CONNECTING,
        CONNECTED,
        LostConnection,
        CLOSING,
        CLOSED
    }
}