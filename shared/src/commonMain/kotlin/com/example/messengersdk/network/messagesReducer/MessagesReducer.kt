package com.example.messengersdk.network.messagesReducer

import com.example.messengersdk.database.AppDatabaseRepository
import com.example.messengersdk.dispatchers.ioDispatcher
import com.example.messengersdk.dto.MessageDTO
import com.example.messengersdk.dto.UserDTO
import com.example.messengersdk.network.BaseReducer
import com.example.messengersdk.network.NetworkService
import com.example.messengersdk.proto.models.ProtoChatMessage
import com.example.messengersdk.proto.models.ProtoMessage
import com.example.messengersdk.proto.models.ProtoUser
import com.example.messengersdk.utils.safeLet
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import org.koin.core.component.inject

class MessagesReducer: BaseReducer<MessagesReducerAction, MessagesReducerEvent>() {
    private val appDatabaseRepository: AppDatabaseRepository by inject()
    private val networkService: NetworkService by inject()

    override val eventsFlow: MutableSharedFlow<MessagesReducerEvent> = MutableSharedFlow()

    override fun dispatchAction(action: MessagesReducerAction) {
        when(action) {
            is MessagesReducerAction.Init -> {
                scope.launch(ioDispatcher) {
                    networkService.protoModelsFlow.collect {
                        handleProtoMessage(it)
                    }
                }
            }
            is MessagesReducerAction.SendMessage -> {
                sendMessage(action.messageDTO)
            }
        }
    }

    private fun sendMessage(message: MessageDTO) {
        when (message) {
            is MessageDTO.TextMessageDTO -> {
                appDatabaseRepository.addMessage(message)
                print(message)
                val protoMessage = ProtoChatMessage(
                    uid = message.id,
                    holder = ProtoUser(uid = message.from.id, name = message.from.name),
                    chatId = message.chat.id,
                    text = message.content,
                    sendTime = message.sendDate
                )
                val proto = ProtoBuf.encodeToByteArray(protoMessage)
                networkService.sendBytes(proto)
            }
        }
    }

    private fun handleProtoMessage(message: ProtoMessage?) {
        when (message) {
            is ProtoChatMessage -> {
                safeLet(appDatabaseRepository.getChatById(message.chatId), appDatabaseRepository.getCurrentUser()) { chat, user ->
                    print("MessagesReducer did receive proto message: $message")
                    appDatabaseRepository.addMessage(
                        MessageDTO.TextMessageDTO(
                            id = message.uid,
                            from = UserDTO(id = message.holder.uid, name = message.holder.name, isCurrent = user.id == message.holder.uid),
                            sendDate = message.sendTime,
                            chat = chat,
                            status = MessageDTO.MessageDTOStatus.RECEIVED,
                            content = message.text
                        )
                    )
                }
            }
        }
    }
}