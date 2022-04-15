package com.example.messengersdk.core

sealed class Resource<out T : Any> {
    open val value: T? = null
    open val throwable: Throwable? = null

    val isData
        get() = value != null

    object Loading : Resource<Nothing>()
    data class Data<T : Any>(override val value: T) : Resource<T>()
    data class Error(override val throwable: Throwable) : Resource<Nothing>()
}

inline fun <reified T : Any> Any?.toResource(): Resource<T> = Resource.Data(this as T)

inline fun <reified T : Any> Throwable.toResource(): Resource<T> = Resource.Error(this)