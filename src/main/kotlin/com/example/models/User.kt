package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int,
    val name: String,
    val email: String,
    val password: String,
    val userType: String,
    val borrowedBooksCounter: Int,
    val bookHistory: MutableList<Book>,
    val banned: Boolean
)
val userList = mutableListOf<User>()