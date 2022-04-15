package com.example.messengersdk.utils
import platform.Foundation.NSUUID

actual fun randomUUID(): String = NSUUID().UUIDString()