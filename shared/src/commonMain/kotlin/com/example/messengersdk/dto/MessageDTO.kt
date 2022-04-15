package com.example.messengersdk.dto

import com.example.messengersdk.models.Chat
import com.example.messengersdk.models.Message
import com.example.messengersdk.models.User

sealed class MessageDTO {
    abstract val id: String
    abstract val from: UserDTO
    abstract val sendDate: Long
    abstract val chat: ChatDTO
    abstract val status: MessageDTOStatus

    data class TextMessageDTO(
        override val id: String,
        override val from: UserDTO,
        override val sendDate: Long,
        override val chat: ChatDTO,
        override val status: MessageDTOStatus,
        val content: String
    ): MessageDTO() {
        override fun toMessage(drawTail: Boolean): Message {
            return Message.TextMessage(
                id = this.id,
                from = this.from.toUser(),
                drawTail = drawTail,
                sendDate = this.sendDate,
                status = this.status.toMessageStatus(),
                content = content
            )
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

            other as TextMessageDTO

            if (id != other.id) return false

            return true
        }

        override fun hashCode(): Int {
            return id.hashCode()
        }
    }

    abstract fun toMessage(drawTail: Boolean): Message

    enum class MessageDTOStatus(val intValue: Int) {
        UNKNOWN(-1),
        SENDING(0),
        RECEIVED(1)
    }

    fun MessageDTO.MessageDTOStatus.toMessageStatus(): Message.Status {
        return when (this) {
            MessageDTO.MessageDTOStatus.UNKNOWN -> {
                Message.Status.UNKNOWN
            }
            MessageDTO.MessageDTOStatus.RECEIVED -> {
                Message.Status.RECEIVED
            }
            MessageDTO.MessageDTOStatus.SENDING -> {
                Message.Status.SENDING
            }
        }
    }
}