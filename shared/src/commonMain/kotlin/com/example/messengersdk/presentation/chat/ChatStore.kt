package com.example.messengersdk.presentation.chat

import com.example.messengersdk.core.toResource
import com.example.messengersdk.database.AppDatabaseRepository
import com.example.messengersdk.dto.ChatDTO
import com.example.messengersdk.dto.MessageDTO
import com.example.messengersdk.dto.UserDTO
import com.example.messengersdk.models.Message
import com.example.messengersdk.network.messagesReducer.MessagesReducer
import com.example.messengersdk.network.messagesReducer.MessagesReducerAction
import com.example.messengersdk.presentation.BaseStore
import com.example.messengersdk.utils.randomUUID
import com.example.messengersdk.utils.safeLet
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.datetime.Clock
import org.koin.core.component.inject

class ChatStore(private val chatId: String): BaseStore<ChatState, ChatAction, ChatEffect>() {
    companion object {
        const val batchSize = 40L
    }
    override val stateFlow: MutableStateFlow<ChatState> = MutableStateFlow(ChatState())
    override val sideEffectsFlow: MutableSharedFlow<ChatEffect> = MutableSharedFlow()

    private val appDatabaseRepository: AppDatabaseRepository by inject()

    private var batches: List<MessagesBatch> = listOf(MessagesBatch(messages = emptyList()))

    private val messagesReducer: MessagesReducer by inject()

    override suspend fun dispatch(action: ChatAction, initialState: ChatState) {
        when(action)  {
            is ChatAction.Init -> {
                messagesReducer.dispatch(MessagesReducerAction.Init)
                updateChatInfo()
                observeLastMessages()
                loadNextBatch(batch = 0)
            }
            is ChatAction.SendMessage -> {
                safeLet(appDatabaseRepository.getChatById(chatId), appDatabaseRepository.getCurrentUser()){ chat, user ->
                    val message = MessageDTO.TextMessageDTO(
                        id = randomUUID(),
                        from = user,
                        sendDate = Clock.System.now().epochSeconds,
                        chat = chat,
                        status = MessageDTO.MessageDTOStatus.SENDING,
                        content = action.content
                    )
                    messagesReducer.dispatch(MessagesReducerAction.SendMessage(message))
                }
            }
            is ChatAction.NeedLoadNextBatch -> {
                if (stateFlow.value.isFull) { return }
                loadNextBatch(stateFlow.value.batch)
            }
        }
    }

    private suspend fun observeLastMessages() {
        appDatabaseRepository.observeLastMessagesFromChat(id = chatId, limit = batchSize)
            .collect { messages ->
                this.batches = batches.plus(MessagesBatch(messages = messages)).sorted()
                mergeBatches()

                updateState { state ->
                    val messages = mapMessages(batches.last().messages)
                    state.copy(
                        chatResource = messages.toResource(),
                        batch = messages.count() / batchSize
                    )
                }
            }
    }

    private suspend fun loadNextBatch(batch: Long) {
        val nextBatch = appDatabaseRepository.getMessagesBatchFromChat(id = chatId, batchSize = batchSize, offset = batch)
        updateState { state ->
            this.batches.last().mergeWithMessages(nextBatch)
            val messages = mapMessages(batches.last().messages)
            state.copy(
                chatResource = messages.toResource(),
                batch = batch + 1,
                isFull = nextBatch.count() < batchSize
            )
        }
    }

    private fun mapMessages(messages: List<MessageDTO>): List<Message> {
        if (messages.count() == 0) {
            return emptyList()
        }

        val newListOfMessages = ArrayList<Message>()

        for (i in 0 until messages.count() - 1) {
            newListOfMessages.add(messages[i].toMessage(drawTail = messages[i].from.id != messages[i + 1].from.id))
        }

        newListOfMessages.add(messages.last().toMessage(drawTail = true))
        return newListOfMessages
    }

    private fun mergeBatches() {
        val batches = this.batches.toMutableList()
        while (batches.count() > 1 && batches.last().compareTo(batches[batches.count() - 2]) == 0) {
            batches.last().mergeWithMessages(batches[batches.count() - 2].messages)
            batches.removeAt(batches.count() - 2)
        }

        this.batches = batches
    }

    private data class MessagesBatch(
        var messages: List<MessageDTO>
    ): Comparable<MessagesBatch> {
        override fun compareTo(other: MessagesBatch): Int {
            if (messages.count() == 0 || other.messages.count() == 0) return 0
            if (messages.intersect(other.messages).count() != 0) return 0
            if (messages.last().sendDate > other.messages.first().sendDate) return 1
            return -1
        }

        fun mergeWithMessages(messages: List<MessageDTO>) {
            this.messages = this.messages.plus(messages).distinctBy { it.id }.sortedBy { it.sendDate }.mapNotNull { it }
        }
    }

    private suspend fun updateChatInfo() {
        val chat = appDatabaseRepository.getChatById(chatId)
        when (chat) {
            is ChatDTO.SimpleChatDTO -> {
                updateState { state ->
                    state.copy(title = chat.withUser.name, isMultiple = false)
                }
            }
            is ChatDTO.MultipleChatDTO -> {
                updateState { state ->
                    state.copy(title = buildTitleForMultipleChatByMembers(chat.members), isMultiple = true)
                }
            }
        }
    }

    private fun buildTitleForMultipleChatByMembers(members: List<UserDTO>): String {
        return if (members.count() < 3) {
            members.joinToString(", ") { it.name }
        } else {
            members.subList(0, 2).joinToString(", ") { it.name } + " and ${members.count() - 2}"
        }
    }
}