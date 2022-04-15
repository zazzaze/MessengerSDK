package com.example.messengersdk.proto.models

import kotlinx.serialization.Serializable

@Serializable
data class ProtoChatMessage(
    val uid: String,
    val holder: ProtoUser,
    val chatId: String,
    val text: String,
    val sendTime: Long
): ProtoMessage