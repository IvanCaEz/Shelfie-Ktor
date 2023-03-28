package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class User(
    var idUser: String,
    var name: String,
    var email: String,
    var password: String,
    var userType: UserType,
    var borrowedBooksCounter: Int,
    var bookHistory: MutableMap<String, Book?>,
    var banned: Boolean
)
val userList = mutableMapOf<String, User?>()