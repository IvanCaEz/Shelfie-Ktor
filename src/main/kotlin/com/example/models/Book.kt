package com.example.models

import kotlinx.serialization.Serializable
import java.util.Date

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
    //val reviews: MutableMap<String, Review?>
    // añadir puntuacion
)
val bookList = mutableMapOf<String, Book?>()