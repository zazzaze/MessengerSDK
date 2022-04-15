package com.example.messengersdk.network.chatsService

import com.example.messengersdk.database.AppDatabaseRepository
import com.example.messengersdk.dispatchers.ioDispatcher
import com.example.messengersdk.dto.ChatDTO
import com.example.messengersdk.dto.MessageDTO
import com.example.messengersdk.dto.UserDTO
import com.example.messengersdk.network.BaseReducer
import com.example.messengersdk.network.NetworkService
import com.example.messengersdk.proto.models.*
import io.ktor.client.request.forms.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import org.koin.core.component.inject

class ChatsReducer: BaseReducer<ChatsReducerAction, ChatsReducerEvent>() {
    override val eventsFlow: MutableSharedFlow<ChatsReducerEvent> = MutableSharedFlow()

    private val networkService: NetworkService by inject()
    private val appDatabaseRepository: AppDatabaseRepository by inject()

    override fun dispatchAction(action: ChatsReducerAction) {
        when (action) {
            is ChatsReducerAction.Init -> {
                scope.launch(ioDispatcher) {
                    networkService.protoModelsFlow.collect {
                        it?.let { message ->
                            handleProtoMessage(message)
                        }
                    }
                }
            }
        }
    }

    private fun handleProtoMessage(message: ProtoMessage) {
        val currentUser = appDatabaseRepository.getCurrentUser()?.let { currentUser ->
            when (message) {
                is ProtoChatsList -> {
                    message.chats.map { protoChat ->
                        val chat = protoChat.toChatDTO(currentUser = currentUser)
                        val messages = protoChat.messages.map { it.toMessageDTO(currentUser, chat) }
                        val users = protoChat.members.map { it.toUserDTO(currentUser) }
                        messages.forEach { message ->
                            appDatabaseRepository.addMessage(message = message)
                        }
                        appDatabaseRepository.addChat(chat)
                        users.forEach {
                            appDatabaseRepository.addUser(it)
                        }
                    }
                }
                else -> {}
            }
        }
    }

    private fun ProtoChat.toChatDTO(currentUser: UserDTO): ChatDTO {
        return when (this.chatType) {
            "MULTIPLE" -> {
                ChatDTO.MultipleChatDTO(
                    id = this.uid,
                    members = this.members.map { it.toUserDTO(currentUser) }.filter { !it.isCurrent }
                )
            }
            else -> {
                ChatDTO.SimpleChatDTO(
                    id = this.uid,
                    withUser = members.first { it.uid != currentUser.id }.toUserDTO(currentUser)
                )
            }
        }
    }

    private fun ProtoUser.toUserDTO(currentUser: UserDTO): UserDTO = UserDTO(
        id = this.uid,
        name = this.name,
        isCurrent = this.uid == currentUser.id
    )

    private fun ProtoChatMessage.toMessageDTO(currentUser: UserDTO, chat: ChatDTO): MessageDTO {
        return MessageDTO.TextMessageDTO(
            id = this.uid,
            from = this.holder.toUserDTO(currentUser),
            sendDate = this.sendTime,
            chat = chat,
            status = MessageDTO.MessageDTOStatus.RECEIVED,
            content = this.text
        )
    }
}