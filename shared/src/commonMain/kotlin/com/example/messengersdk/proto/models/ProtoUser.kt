package com.example.messengersdk.proto.models

import kotlinx.serialization.Serializable

@Serializable
data class ProtoUser(
    val uid: String,
    val name: String
): ProtoMessage