package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class Review (
    var id: Int,
    val idBook : Int,
    var idUser: Int,
    var date: Int,
    var comment: String
)
val reviewList = mutableListOf<Review>()