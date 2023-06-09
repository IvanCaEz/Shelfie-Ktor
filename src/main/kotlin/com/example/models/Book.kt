package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class Book (
    var idBook: String,
    var title: String,
    var author: String,
    var publicationYear: String,
    var synopsis: String,
    var bookCover: String,
    var state: Boolean,
    var stockTotal: Int,
    var stockRemaining: Int,
    var genre: String

)
