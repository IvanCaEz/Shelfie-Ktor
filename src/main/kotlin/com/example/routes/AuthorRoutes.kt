package com.example.routes

import com.example.database.Database
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.*


//Esta ruta solo se podrá usar para obtener los libros que ha escrito un determinado Autor
fun Route.authorRouting(){
    val db = Database()
    route("/author"){
        get ( "{name}" ){
            val authorName = call.parameters["name"]
            if (authorName.isNullOrBlank()) return@get call.respondText(
                "Missing author name.", status = HttpStatusCode.BadRequest
            )
            val bookList = db.getAllBooks()

            val bookListByAuthor = bookList.filter { book ->
                book.author.toLowerCasePreservingASCIIRules().contains(authorName)  }
            if (bookListByAuthor.isNotEmpty()){
                return@get call.respond(bookListByAuthor)
            } else call.respondText("No author found.", status = HttpStatusCode.OK)
        }

    }
}