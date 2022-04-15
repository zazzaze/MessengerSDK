package com.example.messengersdk.dto

sealed class ChatDTO {
    abstract val id: String

    data class SimpleChatDTO(
        override val id: String,
        val withUser: UserDTO
    ): ChatDTO()

    data class MultipleChatDTO(
        override val id: String,
        val members: List<UserDTO>
    ): ChatDTO()
}