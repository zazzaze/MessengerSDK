package com.example.messengersdk.network.sockets

import co.touchlab.kermit.Logger

internal expect class PlatformSocket(
    url: String,
    logger: Logger
) {
    fun openSocket(listener: PlatformSocketListener)
    fun closeSocket(code: Int, reason: String)
    fun sendBytes(bytes: ByteArray)
    fun sendPingAndReceivePong(callback: (Throwable?) -> Unit)
}
interface PlatformSocketListener {
    fun onOpen()
    fun onFailure(t: Throwable)
    fun onMessage(msgBytes: ByteArray)
    fun onClosing(code: Int, reason: String)
    fun onClosed(code: Int, reason: String)
}