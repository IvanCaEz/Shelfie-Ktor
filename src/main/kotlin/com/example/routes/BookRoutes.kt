package com.example.routes

import com.example.models.Book
import com.example.models.bookList
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.bookRouting() {
    route("/books") {
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
                if (book.idBook == id?.toInt()) return@get call.respond(book)
            }
            call.respondText(
                "Book with id $id not found",
                status = HttpStatusCode.NotFound
            )
        }

        // Post

        post {
            val book = call.receive<Book>()
            // Si no hay ningún libro ya con ese id, lo añadimos
            if (bookList.none { book.idBook == it.idBook }){
                bookList.add(book)
            } else {
                return@post call.respondText(
                    "Book with id ${book.idBook} already exists", status = HttpStatusCode.OK)
            }
            call.respondText("Book stored correctly", status = HttpStatusCode.Created)


        }

        /*
        Esto de las imágenes no logro hacerlo funcionar... preguntar a Jordi el próximo día
        post {
           // val book = call.receive<Book>()
            var bookID = 0
            var bookTitle = ""
            var author = ""
            var publicationYear = 0
            var synopsis = ""
            var bookCover = ""
            var state = true
            var stockTotal = 0
            var stockRemaining = 0
            var genre = ""
            val reviews = mutableListOf<Review>()
            val bookData = call.receiveMultipart()

            // Separem el tractament de les dades entre: dades primitives i fitxers
            bookData.forEachPart { part ->
                when(part) {
                    // Aquí recollim les dades primitives
                    is PartData.FormItem -> {
                        when(part.name) {
                            "idBook" -> bookID = part.value.toInt()
                            "title" -> bookTitle = part.value
                            "author" -> author = part.value
                            "publicationYear" -> publicationYear = part.value.toInt()
                            "synopsis" -> synopsis = part.value
                            "state" -> state = part.value.toBoolean()
                            "stockTotal" -> stockTotal = part.value.toInt()
                            "stockRemaining" -> stockRemaining = part.value.toInt()
                            "genre" -> genre = part.value
                        }
                    }
                    // Aquí recollim els fitxers
                    is PartData.FileItem -> {
                        bookCover = "uploads/" + part.originalFileName as String
                        val fileBytes = part.streamProvider().readBytes()
                        File(bookCover).writeBytes(fileBytes)
                    }
                    else -> {}
                }

            }

            // Si no hay ningún libro ya con ese id, lo añadimos
            if (bookList.none { bookID == it.idBook }){
                bookList.add(Book(bookID, bookTitle, author,
                    publicationYear, synopsis, bookCover, state, stockTotal, stockRemaining, genre, reviews))
                //bookList.add(book)
            } else {
                return@post call.respondText(
                    "Book with id $bookID already exists", status = HttpStatusCode.OK)
            }
            call.respondText("Book stored correctly", status = HttpStatusCode.Created)


        }

        // GET book cover

        get("/bookCover/{id?}") {
            var file: File = File("")
            if (call.parameters["id"].isNullOrBlank()) return@get call.respondText(
                "Missing id",
                status = HttpStatusCode.BadRequest
            )
            val id = call.parameters["id"]
            for (book in bookList) {
                if (book.idBook == id?.toInt()) file = File(book.bookCover)
            }
            if (file.exists()) {
                call.respondFile(file)
            } else {
                call.respondText("No image found", status = HttpStatusCode.NotFound)
            }
        }
         */

        // Put solo pueden hacerlo los administradores
        put("{id?}") {
            if (call.parameters["id"].isNullOrBlank()) return@put call.respondText(
                "Missing id",
                status = HttpStatusCode.BadRequest
            )
            val id = call.parameters["id"]
            val bookToUpdate = call.receive<Book>()
            for (book in bookList) {
                if (book.idBook == id?.toInt()) {
                    book.idBook = bookToUpdate.idBook
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
                if (book.idBook == id?.toInt()) {
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
