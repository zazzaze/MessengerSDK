package com.example.messengersdk.dto

import com.example.messengersdk.models.User

data class UserDTO(
    val id: String,
    val name: String,
    val isCurrent: Boolean
) {
    fun toUser() = User(
        id = this.id,
        name = this.name,
        isCurrent = this.isCurrent
    )
}