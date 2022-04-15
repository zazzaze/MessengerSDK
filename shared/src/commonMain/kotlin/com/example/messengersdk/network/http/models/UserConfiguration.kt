package com.example.messengersdk.network.http.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserConfiguration(
    @SerialName("ChatType") val chatType: String,
    @SerialName("ChatId") val chatId: String
)