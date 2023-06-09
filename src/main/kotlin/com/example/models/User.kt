package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class User (
    var idUser: String,
    var name: String,
    var email: String,
    var password: String,
    var userName: String,
    var description: String,
    var userType: UserType,
    var borrowedBooksCounter: Int,
    var bookHistory: Set<Int>,
    var banned: Boolean,
    var userImage:  String
)
