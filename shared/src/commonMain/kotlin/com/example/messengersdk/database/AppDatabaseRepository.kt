package com.example.messengersdk.database

import com.example.messengersdk.dto.ChatDTO
import com.example.messengersdk.dto.MessageDTO
import com.example.messengersdk.dto.UserDTO
import com.example.messengersdk.models.Message
import kotlinx.coroutines.flow.Flow
typealias MessagesList = List<MessageDTO>
typealias ChatsList = List<ChatDTO>

interface AppDatabaseRepository {
    // Messages
    fun observeLastMessagesFromChat(id: String, limit: Long): Flow<MessagesList>

    fun getMessagesFromChat(id: String): MessagesList

    fun getMessagesBatchFromChat(id: String, batchSize: Long, offset: Long): MessagesList

    fun addMessage(message: MessageDTO)

    fun getLastMessageFromChat(id: String): MessageDTO?

    fun getLastMessageFromChat(chatId: String, fromUserId: String): MessageDTO?

    fun getUnsendMessages(): MessagesList

    // Chats
    fun observeChatsList(): Flow<ChatsList>

    fun addChat(chatDTO: ChatDTO)

    fun getChatById(chatId: String): ChatDTO?

    // Users
    fun getCurrentUser(): UserDTO?
    fun addUser(user: UserDTO)

    fun clearAll()
}