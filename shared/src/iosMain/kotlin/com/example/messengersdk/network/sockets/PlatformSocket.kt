package com.example.messengersdk.network.sockets

import co.touchlab.kermit.Logger
import io.ktor.utils.io.core.*
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.allocArrayOf
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.usePinned
import platform.Foundation.*
import platform.darwin.NSObject
import platform.posix.memcpy

internal actual class PlatformSocket actual constructor(url: String, private val logger: Logger) {
    private val socketEndpoint = NSURL.URLWithString(url)!!
    private var webSocket: NSURLSessionWebSocketTask? = null


    actual fun openSocket(listener: PlatformSocketListener) {
        val urlSession = NSURLSession.sessionWithConfiguration(
            configuration = NSURLSessionConfiguration.defaultSessionConfiguration(),
            delegate = object : NSObject(), NSURLSessionWebSocketDelegateProtocol {
                override fun URLSession(
                    session: NSURLSession,
                    webSocketTask: NSURLSessionWebSocketTask,
                    didOpenWithProtocol: String?
                ) {
                    logger.v("Socket did opened")
                    listener.onOpen()
                }
                override fun URLSession(
                    session: NSURLSession,
                    webSocketTask: NSURLSessionWebSocketTask,
                    didCloseWithCode: NSURLSessionWebSocketCloseCode,
                    reason: NSData?
                ) {
                    logger.v("Socket did closed")
                    listener.onClosed(didCloseWithCode.toInt(), reason.toString())
                }
            },
            delegateQueue = NSOperationQueue.currentQueue()
        )

        webSocket = urlSession.webSocketTaskWithURL(socketEndpoint)
        listenMessages(listener)
        webSocket?.resume()
    }
    private fun listenMessages(listener: PlatformSocketListener) {
        webSocket?.receiveMessageWithCompletionHandler { message, nsError ->
            when {
                nsError != null -> {
                    logger.v(message = "Did recieve error", throwable = Throwable(nsError.description))
                    listener.onFailure(Throwable(nsError.description))
                }
                message != null -> {
                    logger.v(message = "Did recieve message: ${message.string}")
                    message.string?.toByteArray()?.let { data ->
                        listener.onMessage(data)
                    }?: message.data?.toByteArray()?.let { data ->
                        listener.onMessage(data)
                    }
                }
            }
            listenMessages(listener)
        }
    }
    actual fun closeSocket(code: Int, reason: String) {
        webSocket?.cancelWithCloseCode(code.toLong(), null)
        webSocket = null
    }

    actual fun sendBytes(bytes: ByteArray) {
        val data = bytes.toNSData()
        val msg = NSURLSessionWebSocketMessage(data)
        webSocket?.sendMessage(NSURLSessionWebSocketMessage(data)) { err ->
            logger.e("Send message error", Throwable(err?.description))
        }
    }

    actual fun sendPingAndReceivePong(callback: (Throwable?) -> Unit) {
        webSocket?.sendPingWithPongReceiveHandler { error ->
            if (error == null) logger.v(message = "did receive pong message successfully")
            else logger.v(message = "did not receive pong message with error", throwable = Throwable(error.description))
            callback(Throwable(error?.description))
        }
    }

    private fun NSData.toByteArray(): ByteArray = ByteArray(this@toByteArray.length.toInt()).apply {
        usePinned {
            memcpy(it.addressOf(0), this@toByteArray.bytes, this@toByteArray.length)
        }
    }

    private fun ByteArray.toNSData(): NSData = memScoped {
        NSData.create(bytes = allocArrayOf(this@toNSData), length = this@toNSData.size.toULong())
    }
}