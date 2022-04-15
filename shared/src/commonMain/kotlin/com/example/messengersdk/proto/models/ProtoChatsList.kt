package com.example.messengersdk.proto.models

import kotlinx.serialization.Serializable

@Serializable
data class ProtoChatsList(
    val chats: List<ProtoChat>
): ProtoMessage