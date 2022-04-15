package com.example.messengersdk.proto

import com.example.messengersdk.proto.models.ProtoChatMessage
import com.example.messengersdk.proto.models.ProtoChatsList
import com.example.messengersdk.proto.models.ProtoMessage
import com.example.messengersdk.proto.models.ProtoUser
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.protobuf.ProtoBuf

class ProtoMessageConverter {
    companion object {
        public fun decodeFromByteArray(byteArray: ByteArray): ProtoMessage? {
            return tryParse<ProtoChatsList>(byteArray)?: tryParse<ProtoChatMessage>(byteArray)?: tryParse<ProtoUser>(byteArray)
        }

        private inline fun <reified T> tryParse(byteArray: ByteArray): T? {
            return try {
                ProtoBuf.decodeFromByteArray<T>(byteArray)
            } catch (_: Throwable) {
                null
            }
        }
    }
}