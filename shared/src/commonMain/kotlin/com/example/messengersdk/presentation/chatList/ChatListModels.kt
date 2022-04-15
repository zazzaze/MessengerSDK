package com.example.messengersdk.presentation.chatList

import com.example.messengersdk.core.Resource
import com.example.messengersdk.models.Chat
import com.example.messengersdk.models.Message

data class ChatListState(
    val chatListResource: Resource<List<Chat>> = Resource.Loading,
)

sealed class ChatListAction {
    object Init: ChatListAction()
}

sealed class ChatListEffect {
    //
}
