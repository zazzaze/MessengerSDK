package com.example.messengersdk.dispatchers

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlin.coroutines.CoroutineContext

actual val ioDispatcher: CoroutineContext
    get() = Dispatchers.Main

actual val uiDispatcher: CoroutineContext
    get() = Dispatchers.Default

actual val ktorDispatcher: CoroutineContext
    get() = GlobalScope.coroutineContext + Dispatchers.Main
