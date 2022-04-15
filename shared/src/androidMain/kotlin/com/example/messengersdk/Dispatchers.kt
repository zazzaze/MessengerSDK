package com.example.messengersdk.dispatchers

import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

actual val ioDispatcher: CoroutineContext
    get() = Dispatchers.IO

actual val uiDispatcher: CoroutineContext
    get() = Dispatchers.Main

actual val ktorDispatcher: CoroutineContext
    get() = Dispatchers.IO