package com.example.messengersdk.proto.models

import kotlinx.serialization.Serializable

@Serializable
data class ProtoChat(
    val uid: String,
    val chatType: String,
    val members: List<ProtoUser>,
    val messages: List<ProtoChatMessage>
): ProtoMessage