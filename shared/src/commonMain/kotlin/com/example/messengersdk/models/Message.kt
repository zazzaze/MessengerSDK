package com.example.messengersdk.models

import com.example.messengersdk.dto.MessageDTO

sealed class Message {
    abstract val id: String
    abstract val from: User
    abstract val drawTail: Boolean
    abstract val sendDate: Long
    abstract val status: Status

    abstract fun asText(): String

    data class TextMessage(
        override val id: String,
        override val from: User,
        override val drawTail: Boolean,
        override val sendDate: Long,
        override val status: Status,
        val content: String
    ): Message() {
        override fun asText(): String {
            return content
        }
    }

    fun copy(
        id: String = this.id,
        from: User = this.from,
        drawTail: Boolean = this.drawTail,
        sendDate: Long = this.sendDate
    ): Message {
        when (this) {
            is TextMessage -> {
                return TextMessage(
                    id = id,
                    from = from,
                    drawTail = drawTail,
                    sendDate = sendDate,
                    status = status,
                    content = content
                )
            }
        }
    }

    enum class Status {
        UNKNOWN,
        SENDING,
        RECEIVED
    }
}