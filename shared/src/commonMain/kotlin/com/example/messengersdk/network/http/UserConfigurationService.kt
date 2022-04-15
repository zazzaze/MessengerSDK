package com.example.messengersdk.network.http

import com.example.messengersdk.dispatchers.ktorDispatcher
import com.example.messengersdk.network.http.models.UserConfiguration
import io.ktor.client.*
import io.ktor.client.request.*
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class UserConfigurationService: KoinComponent {
    private val httpClient: HttpClient by inject()
    private val hostUrl: String by inject(HostQualifier)

    suspend fun loadConfiguration(apiKey: String, userId: String, token: String): Result<UserConfiguration> {
        return withContext(ktorDispatcher) {
            runCatching {
                httpClient.get("$hostUrl/config") {
//                    parameter("api_key", apiKey)
//                    parameter("uid", userId)
                    parameter("token", token)
//                    header("Authorization", "OAuth $token")
                }
            }
        }
    }
}