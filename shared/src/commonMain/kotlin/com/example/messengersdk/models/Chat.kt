package com.example.messengersdk.models

sealed class Chat {
    abstract val id: String
    abstract val title: String
    abstract val lastMessage: Message?

    data class SimpleChat(
        override val id: String,
        override val title: String,
        override val lastMessage: Message?
    ): Chat()

    data class MultipleChat(
        override val id: String,
        override val title: String,
        override val lastMessage: Message?
    ): Chat()
}