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
    var bookHistory: MutableSet<Int>, // <- Cambiar a set de Ints
    var banned: Boolean,
    var userImage:  String
)
val userList = mutableMapOf<String, User?>()