package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class BookLoan (
    val idUser : String,
    val idBook: String,
    val startDate: String,
    val endDate: String
)