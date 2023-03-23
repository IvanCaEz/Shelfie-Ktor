package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class Review (
    var idReview: Int,
    var idBook : Int,
    var idUser: Int,
    var date: Int,
    var comment: String
)
val reviewList = mutableListOf<Review>()