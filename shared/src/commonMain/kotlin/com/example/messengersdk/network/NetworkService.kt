package com.example.messengersdk.network

import com.example.messengersdk.dispatchers.ioDispatcher
import com.example.messengersdk.dispatchers.uiDispatcher
import com.example.messengersdk.network.sockets.AppSocket
import com.example.messengersdk.proto.ProtoMessageConverter
import com.example.messengersdk.proto.models.ProtoChatMessage
import com.example.messengersdk.proto.models.ProtoMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.protobuf.ProtoBuf

@OptIn(ExperimentalSerializationApi::class)
class NetworkService(private val scope: CoroutineScope = CoroutineScope(uiDispatcher + SupervisorJob())) {
    private var socket: AppSocket? = null

    private val _protoModelsFlow: MutableSharedFlow<ProtoMessage?> = MutableSharedFlow()
    val protoModelsFlow: SharedFlow<ProtoMessage?> = _protoModelsFlow

    private val _socketStateFlow: MutableSharedFlow<AppSocket.State> = MutableSharedFlow()
    val socketStateFlow: SharedFlow<AppSocket.State> = _socketStateFlow

    private var pingPongJob = flow { while (true) { emit(Unit) } }

    fun start(url: String) {
        scope.launch(ioDispatcher) { socket = AppSocket(url, scope) }
        scope.launch(ioDispatcher) {
            if (socket?.stateSharedFlow?.value != AppSocket.State.CLOSED) { return@launch }
            socket?.connect()
            socket?.messagesFlow
                ?.collect { message ->
                    handleMessage(messageData = message)
                }
        }

        scope.launch(ioDispatcher) {
            socket?.stateSharedFlow
                ?.collect { state ->
                    handleStateChange(state = state)
                }
        }
    }

    fun sendBytes(byteArray: ByteArray) {
        if (socket == null || socket?.stateSharedFlow?.value != AppSocket.State.CONNECTED) {
            return
        }
        scope.launch(ioDispatcher) {
            socket?.sendBytes(byteArray)
        }
    }

    private fun handleMessage(messageData: ByteArray) {
        val model = ProtoMessageConverter.decodeFromByteArray(messageData)
        print(model)
        scope.launch {
            _protoModelsFlow.emit(model)
        }
    }

    private fun handleStateChange(state: AppSocket.State) {
        scope.launch(ioDispatcher) {
            _socketStateFlow.emit(state)
        }
        when (state) {
            AppSocket.State.CONNECTED -> {

            }
            AppSocket.State.LostConnection -> {

            }
            AppSocket.State.CLOSED -> {

            }
        }
    }
}