package com.example.models

import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
data class Book (
    var id: Int,
    var title: String,
    var author: String,
    var publicationYear: Int,
    var synopsis: String,
    var bookCover: String,
    var state: Boolean,
    var stockTotal: Int,
    var stockRemaining: Int,
    var genre: String,
    val comments: MutableList<Review>
)
val bookList = mutableListOf<Book>()