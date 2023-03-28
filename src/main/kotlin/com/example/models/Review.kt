package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class Review (
    var idReview: String,
    var idBook : String,
    var idUser: String,
    var date: Int,
    var comment: String
)
val reviewList = mutableListOf<Review>()
