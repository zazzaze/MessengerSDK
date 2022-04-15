package com.example.messengersdk.presentation.chatList

import com.example.messengersdk.core.toResource
import com.example.messengersdk.database.AppDatabaseRepository
import com.example.messengersdk.dto.ChatDTO
import com.example.messengersdk.models.Chat
import com.example.messengersdk.models.User
import com.example.messengersdk.network.chatsService.ChatsReducer
import com.example.messengersdk.network.chatsService.ChatsReducerAction
import com.example.messengersdk.presentation.BaseStore
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.ExperimentalSerializationApi
import org.koin.core.component.inject


class ChatListStore() : BaseStore<ChatListState, ChatListAction, ChatListEffect>(){
    override val stateFlow: MutableStateFlow<ChatListState> = MutableStateFlow(ChatListState())
    override val sideEffectsFlow: MutableSharedFlow<ChatListEffect> = MutableSharedFlow()

    private val database: AppDatabaseRepository by inject()

    private val chatsReducer: ChatsReducer by inject()

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun dispatch(action: ChatListAction, initialState: ChatListState) {
       when (action) {
           is ChatListAction.Init -> {
               observeSimpleChats()
               chatsReducer.dispatch(ChatsReducerAction.Init)
           }
       }
    }

    private suspend fun observeSimpleChats() {
        database.observeChatsList()
            .collect { chats ->
                updateState { state ->
                    state.copy(
                        chatListResource = chats.map { it.toChat() }.sortedByDescending { it.lastMessage?.sendDate }.toResource()
                    )
                }
            }
    }

    private fun ChatDTO.toChat(): Chat {
        return when (this) {
            is ChatDTO.SimpleChatDTO -> {
                Chat.SimpleChat(
                    id = this.id,
                    title = this.withUser.name,
                    lastMessage = database.getLastMessageFromChat(id = this.id)?.toMessage(drawTail = true)
                )
            }
            is ChatDTO.MultipleChatDTO -> {
                Chat.MultipleChat(
                    id = this.id,
                    title = buildTitleForMultipleChatByMembers(this.members.map { it.toUser() }),
                    lastMessage = database.getLastMessageFromChat(id = this.id)?.toMessage(drawTail = true)
                )
            }
        }
    }

    private fun buildTitleForMultipleChatByMembers(members: List<User>): String {
        return if (members.count() < 3) {
            members.joinToString(", ") { it.name }
        } else {
            members.subList(0, 2).joinToString(", ") { it.name } + " and ${members.count() - 2}"
        }
    }
}