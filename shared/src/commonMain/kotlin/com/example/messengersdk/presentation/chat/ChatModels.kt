package com.example.messengersdk.presentation.chat

import com.example.messengersdk.core.Resource
import com.example.messengersdk.models.Message

data class ChatState(
    val chatResource: Resource<List<Message>> = Resource.Loading,
    val batch: Long = 0,
    val isFull: Boolean = false,
    val title: String = "",
    val isMultiple: Boolean = false
)

sealed class ChatAction {
    object Init: ChatAction()

    data class SendMessage(val content: String): ChatAction()

    object NeedLoadNextBatch: ChatAction()
}

sealed class ChatEffect {
    //
}