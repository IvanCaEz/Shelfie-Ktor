package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class User(
    var idUser: Int,
    var name: String,
    var email: String,
    var password: String,
    var userType: String,
    var borrowedBooksCounter: Int,
    var bookHistory: MutableList<Book>,
    var banned: Boolean
)
val userList = mutableListOf<User>()