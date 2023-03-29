package com.example.routes

import com.example.models.Book
import com.example.models.Review
import com.example.models.bookList
import com.example.models.userList
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File
import java.io.FileNotFoundException

fun Route.bookRouting() {
    route("/books") {
        // GET
        // Todos los libros
        get {
            if (bookList.isNotEmpty()) {
                call.respond(bookList.values)
            } else {
                call.respondText("No books found.", status = HttpStatusCode.OK)
            }
        }
        // Buscar por ID de libro
        get("{id}") {
            if (call.parameters["id"].isNullOrBlank()) return@get call.respondText(
                "Missing book id", status = HttpStatusCode.BadRequest
            )
            val id = call.parameters["id"]
            if (bookList.isNotEmpty()) {
                if (bookList.containsKey(id) && bookList[id] != null) {
                    return@get call.respond(bookList[id]!!)
                } else call.respondText("Book with id $id not found.", status = HttpStatusCode.NotFound)
            } else call.respondText("No books found.", status = HttpStatusCode.OK)
        }

        get("{id?}/bookCover") {
            var file: File = File("")
            if (call.parameters["id"].isNullOrBlank()) return@get call.respondText(
                "Missing book id.",
                status = HttpStatusCode.BadRequest
            )
            val id = call.parameters["id"]

            if (bookList.containsKey(id)) file = File("src/main/kotlin/com/example/book-covers/" + bookList[id]!!.bookCover)
            if (file.exists()) {
                call.respondFile(file)
            } else {
                call.respondText("No image found.", status = HttpStatusCode.NotFound)
            }
        }

        // Post solo pueden hacerlo los admins

        post {
            // val book = call.receive<Book>()
            val bookData = call.receiveMultipart()
            val book = Book(
                "", "", "", "", "", "", true,
                0, 0, ""
            )

            // Separem el tractament de les dades entre: dades primitives i fitxers
            bookData.forEachPart { part ->
                when (part) {
                    // Aquí recollim les dades primitives
                    is PartData.FormItem -> {
                        when (part.name) {
                            "idBook" -> book.idBook = part.value
                            "title" -> book.title = part.value
                            "author" -> book.author = part.value
                            "publicationYear" -> book.publicationYear = part.value
                            "synopsis" -> book.synopsis = part.value
                            "state" -> book.state = part.value.toBoolean()
                            "stockTotal" -> book.stockTotal = part.value.toInt()
                            "stockRemaining" -> book.stockRemaining = part.value.toInt()
                            "genre" -> book.genre = part.value

                        }
                    }
                    // Aquí recollim els fitxers
                    is PartData.FileItem -> {
                        try {
                            book.bookCover = part.originalFileName as String

                            val fileBytes = part.streamProvider().readBytes()
                            File("src/main/kotlin/com/example/book-covers/" + book.bookCover).writeBytes(fileBytes)
                            println("Imagen subida")
                        } catch (e: FileNotFoundException){
                            println("Error " + e.message)
                        }
                    }
                    else -> {}
                }
                println("Subido ${part.name}")
            }
            println("Ahora posteamos")
            // Si no hay ningún libro ya con ese id o ha sido borrado (el valor es nulo), lo añadimos a la booklist con esa ID
            if (!bookList.containsKey(book.idBook) || bookList[book.idBook] == null) {
                bookList[book.idBook] = book
                call.respondText("Book stored correctly.", status = HttpStatusCode.Created)
                return@post call.respond(book)
            } else {
                return@post call.respondText(
                    "Book with id ${book.idBook} already exists.",
                    status = HttpStatusCode.OK
                )
            }
        }

        // Put solo pueden hacerlo los administradores
        put("{id?}") {
            if (call.parameters["id"].isNullOrBlank()) return@put call.respondText(
                "Missing id.",
                status = HttpStatusCode.BadRequest
            )
            val id = call.parameters["id"]
            val bookToUpdate = call.receive<Book>()

            if (bookList.isNotEmpty()) {
                if (bookList.containsKey(id)) {
                    bookList[id!!] = bookToUpdate
                    return@put call.respondText(
                        "Book with id $id has been updated.", status = HttpStatusCode.Accepted
                    )
                } else return@put call.respondText(
                    "Book with id $id not found.",
                    status = HttpStatusCode.NotFound
                )
            } else call.respondText("No books found.", status = HttpStatusCode.OK)
        }
    }
    //Delete solo pueden hacerlo los admins
    delete("{id}") {
        if (call.parameters["id"].isNullOrBlank()) return@delete call.respondText(
            "Missing book id.",
            status = HttpStatusCode.BadRequest
        )
        val id = call.parameters["id"]
        if (bookList.isNotEmpty()) {
            if (bookList.containsKey(id)) {
                bookList[id!!] = null
                return@delete call.respondText(
                    "Book removed successfully.", status = HttpStatusCode.Accepted
                )
            } else return@delete call.respondText(
                "Book with id $id not found.",
                status = HttpStatusCode.NotFound
            )
        } else {
            call.respondText("No books found.", status = HttpStatusCode.NotFound)
        }
    }
}


