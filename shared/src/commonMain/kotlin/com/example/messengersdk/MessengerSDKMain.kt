package com.example.messengersdk

import com.example.messengersdk.database.AppDatabaseRepository
import com.example.messengersdk.dispatchers.ioDispatcher
import com.example.messengersdk.dispatchers.uiDispatcher
import com.example.messengersdk.dto.UserDTO
import com.example.messengersdk.network.NetworkService
import com.example.messengersdk.network.chatsService.ChatsReducer
import com.example.messengersdk.network.chatsService.ChatsReducerAction
import com.example.messengersdk.network.http.UserConfigurationService
import com.example.messengersdk.network.messagesReducer.MessagesReducer
import com.example.messengersdk.network.messagesReducer.MessagesReducerAction
import com.example.messengersdk.network.sockets.AppSocket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MessengerSDKMain(private val configuration: Configuration): KoinComponent {
    private val scope: CoroutineScope = CoroutineScope(uiDispatcher + SupervisorJob())
    var userScope: UserScope = UserScope.Unknown
    private val configurationService: UserConfigurationService by inject()
    private val webSocketService: NetworkService by inject()
    private val appDatabaseRepository: AppDatabaseRepository by inject()
    private val messagesReducer: MessagesReducer by inject()
    private val chatsReducer: ChatsReducer by inject()

    init {
        chatsReducer.dispatch(ChatsReducerAction.Init)
        appDatabaseRepository.getCurrentUser()?.let {
            if (it.id != configuration.uid) {
                appDatabaseRepository.clearAll()
                appDatabaseRepository.addUser(UserDTO(id = configuration.uid, name="", isCurrent = true))
            }
        }?: appDatabaseRepository.addUser(UserDTO(id = configuration.uid, name="", isCurrent = true))
        scope.launch(ioDispatcher) {
            val config = configurationService.loadConfiguration(apiKey = configuration.apiKey, userId = configuration.uid, token = configuration.token)
            config.onSuccess { config ->
                userScope =  when(val status = config.chatType) {
                    "SINGLE" -> {
                        UserScope.Single(chatId = config.chatId)
                    }
                    "MULTIPLE" -> {
                        UserScope.Multiple
                    }
                    else -> {
                        UserScope.Unknown
                    }
                }
            }.onFailure {
                userScope = UserScope.Unknown
            }
        }

        userScope = UserScope.Unknown
        webSocketService.start(url = "ws://localhost:8070/connection?token=${configuration.token}")
        scope.launch(ioDispatcher) {
            webSocketService.socketStateFlow
                .collect {
                    if (it == AppSocket.State.CONNECTED) {
                        syncUnsendMessages()
                    }
                }
        }
    }

    private fun syncUnsendMessages() {
        val messages = appDatabaseRepository.getUnsendMessages()
        for (message in messages) {
            messagesReducer.dispatch(MessagesReducerAction.SendMessage(message))
        }
    }

    data class Configuration(
        val apiKey: String,
        val uid: String,
        val token: String,
    )

    sealed class UserScope {
        data class Single(val chatId: String): UserScope()
        object Multiple: UserScope()
        object Unknown: UserScope()
    }
}