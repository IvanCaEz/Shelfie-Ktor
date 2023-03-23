package com.example.routes

import com.example.models.Book
import com.example.models.bookList
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.userRouting(){
    route("/users") {
        // GET
        // Todos los libros
        get {
            if (bookList.isNotEmpty()) {
                call.respond(bookList)
            } else {
                call.respondText("No books found.", status = HttpStatusCode.OK)
            }
        }
        // Buscar por ID de libro
        get ("{id}"){
            if (call.parameters["id"].isNullOrBlank()) return@get call.respondText(
                "Missing book id", status = HttpStatusCode.BadRequest
            )
            val id = call.parameters["id"]
            for (book in bookList) {
                if (book.id == id?.toInt()) return@get call.respond(book)
            }
            call.respondText(
                "Book with id $id not found",
                status = HttpStatusCode.NotFound
            )
        }

        // Post

        post {
            val book = call.receive<Book>()
            bookList.add(book)
            call.respondText("Book stored correctly", status = HttpStatusCode.Created)

        }

        // Put solo pueden hacerlo los administradores
        put("{id?}") {
            if (call.parameters["id"].isNullOrBlank()) return@put call.respondText(
                "Missing id",
                status = HttpStatusCode.BadRequest
            )
            val id = call.parameters["id"]
            val bookToUpdate = call.receive<Book>()
            for (book in bookList) {
                if (book.id == id?.toInt()) {
                    book.id = bookToUpdate.id
                    book.title = bookToUpdate.title
                    book.author = bookToUpdate.author
                    book.publicationYear = bookToUpdate.publicationYear
                    book.synopsis = bookToUpdate.synopsis
                    book.bookCover = bookToUpdate.bookCover
                    book.state = bookToUpdate.state
                    book.stockTotal = bookToUpdate.stockTotal
                    book.stockRemaining = bookToUpdate.stockRemaining
                    book.genre = bookToUpdate.genre

                    return@put call.respondText(
                        "Book with id $id has been updated",
                        status = HttpStatusCode.Accepted
                    )
                }
            }
            call.respondText(
                "Book with id $id not found.",
                status = HttpStatusCode.NotFound
            )
        }

        delete("{id}"){
            if (call.parameters["id"].isNullOrBlank()) return@delete call.respondText(
                "Missing book id",
                status = HttpStatusCode.BadRequest
            )
            val id = call.parameters["id"]
            for (book in bookList) {
                if (book.id == id?.toInt()) {
                    bookList.remove(book)
                    return@delete call.respondText("Book removed correctly.", status = HttpStatusCode.Accepted)
                }
            }
            call.respondText(
                "Book with id $id not found.",
                status = HttpStatusCode.NotFound
            )


        }
    }

}