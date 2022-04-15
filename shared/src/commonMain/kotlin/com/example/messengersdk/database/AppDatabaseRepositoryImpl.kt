package com.example.messengersdk.database

import com.example.messengersdk.dto.ChatDTO
import com.example.messengersdk.dto.MessageDTO
import com.example.messengersdk.dto.UserDTO
import com.example.messengersdk.utils.safeLet
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.flow.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import tables.AppDatabase
import tables.Chat
import tables.Message
import tables.User

internal class AppDatabaseRepositoryImpl: KoinComponent, AppDatabaseRepository {
    private val databaseDriverFactory: DatabaseDriverFactory by inject()

    private val database: AppDatabase by lazy {
        AppDatabase(databaseDriverFactory.createDriver())
    }

    override fun observeLastMessagesFromChat(id: String, limit: Long) = database.messageQueries
        .getLastMessagesFromChat(chatId = id, limit = limit)
        .asFlow()
        .mapToList()
        .mapNotNull { dbMessage -> dbMessage.mapNotNull { it.toMessageDTO() } }

    override fun getMessagesFromChat(id: String) = database.messageQueries
        .getMessagesFromChat(chatId = id)
        .executeAsList()
        .mapNotNull { it.toMessageDTO() }

    override fun getMessagesBatchFromChat(id: String, batchSize: Long, offset: Long) = database.messageQueries
        .getMessagesBatchFromChat(chatId = id, limit = batchSize, offset = offset)
        .executeAsList()
        .mapNotNull { it.toMessageDTO() }

    override fun getLastMessageFromChat(chatId: String, fromUserId: String): MessageDTO? = database.messageQueries
        .getLastMessageFromChatFromUser(chatId = chatId, userId = fromUserId)
        .executeAsOneOrNull()
        ?.toMessageDTO()

    override fun addMessage(message: MessageDTO) {
        database.userQueries.addUser(
            id = message.from.id,
            name = message.from.name,
            isCurrent = if (message.from.isCurrent) 1 else 0
        )
        addChat(message.chat)
        when (message) {
            is MessageDTO.TextMessageDTO -> {
                database.messageQueries.addMessage(
                    id = message.id,
                    fromId = message.from.id,
                    sendTime = message.sendDate,
                    textContent = message.content,
                    chatId = message.chat.id,
                    type = "TEXT",
                    status = message.status.intValue.toLong()
                )
            }
        }
    }

    override fun getLastMessageFromChat(id: String) = database.messageQueries
        .getLastMessageFromChat(chatId = id)
        .executeAsOneOrNull()
        ?.toMessageDTO()

    override fun getUnsendMessages(): MessagesList = database.messageQueries
        .getMessagesWithStatus(MessageDTO.MessageDTOStatus.SENDING.ordinal.toLong())
        .executeAsList()
        .mapNotNull { it.toMessageDTO() }

    override fun observeChatsList() = database.chatQueries
        .getAllChats()
        .asFlow()
        .mapToList()
        .mapNotNull { dbChats -> dbChats.mapNotNull { it.toChatDTO() } }

    override fun addChat(chatDTO: ChatDTO) {
        when (chatDTO) {
            is ChatDTO.SimpleChatDTO -> {
                database.chatQueries.addChat(
                    id = chatDTO.id,
                    withUserId = chatDTO.withUser.id,
                    type = "SINGLE"
                )
            }
            is ChatDTO.MultipleChatDTO -> {
                for (member in chatDTO.members) {
                    database.multipleChatQueries.addMemberToChat(id = "${chatDTO.id}_${member.id}", chatId = chatDTO.id, member = member.id)
                }
                database.chatQueries.addChat(
                    id = chatDTO.id,
                    withUserId = null,
                    type = "MULTIPLE"
                )
            }
        }
    }

    override fun getChatById(chatId: String) = database.chatQueries
        .getChatById(chatId)
        .executeAsOneOrNull()
        ?.toChatDTO()

    override fun getCurrentUser() = database
        .userQueries
        .getCurrentUser()
        .executeAsOneOrNull()
        ?.toUserDTO()

    override fun addUser(user: UserDTO) = database.userQueries
        .addUser(
            id = user.id,
            name = user.name,
            isCurrent = if(user.isCurrent) 1 else 0
        )

    override fun clearAll() {
        database.messageQueries.clear()
        database.multipleChatQueries.clear()
        database.chatQueries.clear()
        database.userQueries.clear()
    }

    private fun Message.toMessageDTO(): MessageDTO? {
        val userDB = database.userQueries.getUser(id = this.fromId).executeAsOneOrNull()
        val chatDB = database.chatQueries.getChatById(chatId = this.chatId).executeAsOneOrNull()
        safeLet(userDB, chatDB) { user, chat ->
            chat.toChatDTO()?.let { chatDTO ->
                if (this.type == "TEXT") {
                    return this.textContent?.let {
                        MessageDTO.TextMessageDTO(
                            id = this.id,
                            from = user.toUserDTO(),
                            sendDate = this.sendTime,
                            chat = chatDTO,
                            status = getMessageDTOStatusFromLong(this.status),
                            content = it,
                        )
                    }
                }
            }
        }

        return null
    }

    private fun getMessageDTOStatusFromLong(value: Long): MessageDTO.MessageDTOStatus {
        return MessageDTO.MessageDTOStatus.values()
            .firstOrNull { it.intValue.toLong() == value }
            ?: MessageDTO.MessageDTOStatus.UNKNOWN
    }

    private fun User.toUserDTO() = UserDTO(
        id = this.id,
        name = this.name,
        isCurrent = this.isCurrent == 1L
    )

    private fun Chat.toChatDTO(): ChatDTO? {
        when (this.type) {
            "SINGLE" -> {
                this.withUserId?.let { userId ->
                    database.userQueries.getUser(id = userId).executeAsOneOrNull()?.let {
                        return ChatDTO.SimpleChatDTO(
                            id = this.id,
                            withUser = it.toUserDTO()
                        )
                    }
                }
            }
            "MULTIPLE" -> {
                val chatMembers = database.multipleChatQueries
                    .getChatWithMembersByChatId(chatId = this.id)
                    .executeAsList()
                    .mapNotNull {
                        database.userQueries.getUser(it.member).executeAsOneOrNull()?.let { user ->
                            user.toUserDTO()
                        }
                    }
                return ChatDTO.MultipleChatDTO(
                    id = this.id,
                    members = chatMembers
                )
            }
        }

        return null
    }
}