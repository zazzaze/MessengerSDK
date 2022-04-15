package com.example.messengersdk.dispatchers

import kotlin.coroutines.CoroutineContext

expect val ioDispatcher: CoroutineContext

expect val uiDispatcher: CoroutineContext

expect val ktorDispatcher: CoroutineContext