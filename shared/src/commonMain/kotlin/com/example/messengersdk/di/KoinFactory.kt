package com.example.messengersdk.di

import co.touchlab.kermit.Logger
import co.touchlab.kermit.StaticConfig
import com.example.messengersdk.database.AppDatabaseRepository
import com.example.messengersdk.database.AppDatabaseRepositoryImpl
import com.example.messengersdk.network.NetworkService
import com.example.messengersdk.network.chatsService.ChatsReducer
import com.example.messengersdk.network.http.HostQualifier
import com.example.messengersdk.network.http.HttpNetworkService
import com.example.messengersdk.network.http.UserConfigurationService
import com.example.messengersdk.network.messagesReducer.MessagesReducer
import io.ktor.client.*
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.bind
import org.koin.dsl.module

fun initKoin(appDeclaration: KoinAppDeclaration = {}) =
    startKoin {
        appDeclaration()
        modules(platformModule, commonModule)
    }

fun initKoin() = initKoin {
    val log = Logger(config = StaticConfig(), tag = "KOIN_")
    log.d("initKoin ios")
}

val commonModule = module {
    single { AppDatabaseRepositoryImpl() } bind AppDatabaseRepository::class
    single { NetworkService() }

    single { HttpNetworkService().httpClient } bind HttpClient::class
    single(HostQualifier, definition = { HostQualifier.value })

    single { UserConfigurationService() } bind UserConfigurationService::class
    single { MessagesReducer() } bind MessagesReducer::class
    single { ChatsReducer() } bind ChatsReducer::class
}
expect val platformModule: Module