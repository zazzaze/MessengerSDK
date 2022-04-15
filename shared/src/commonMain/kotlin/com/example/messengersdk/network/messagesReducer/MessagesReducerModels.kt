package com.example.messengersdk.network.messagesReducer

import com.example.messengersdk.dto.MessageDTO

sealed class MessagesReducerAction {
    object Init: MessagesReducerAction()
    data class SendMessage(val messageDTO: MessageDTO): MessagesReducerAction()
}

sealed class MessagesReducerEvent {
//
}