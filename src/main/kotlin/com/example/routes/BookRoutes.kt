package com.example.routes

import com.example.database.Database
import com.example.models.Book
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.*
import java.io.File
import java.io.FileNotFoundException

fun Route.bookRouting() {
    val db = Database()

    route("/books") {
        // GET
        // Todos los libros
        get {
            val listOfBooksFromDB = db.getAllBooks()
            if (listOfBooksFromDB.isNotEmpty()) call.respond(listOfBooksFromDB)
            else call.respondText("No books found.", status = HttpStatusCode.OK)
        }
        // Buscar por ID de libro
        get("{id}") {
            val id = call.parameters["id"]
            if (id.isNullOrBlank()) return@get call.respondText(
                "Missing book id.", status = HttpStatusCode.BadRequest
            )

            val listOfBooksFromDB = db.getAllBooks()
            if (listOfBooksFromDB.isNotEmpty()) {
                val bookWeWant = listOfBooksFromDB.filter { it.idBook == id }
                if (bookWeWant.size == 1) {
                    return@get call.respond(db.getBookByID(id))
                } else call.respondText("Book with id $id not found.", status = HttpStatusCode.NotFound)
            } else call.respondText("No books found.", status = HttpStatusCode.OK)
        }
        // Buscar por título (devuelve una lista de libros que contengan ese título)
        get("/q={title}") {
            val bookTitle = call.parameters["title"]
            if (bookTitle.isNullOrBlank()) return@get call.respondText(
                "Missing book title.", status = HttpStatusCode.BadRequest
            )

            val listOfBooksFromDB = db.getAllBooks()

            if (listOfBooksFromDB.isNotEmpty()) {
                val bookListByTittleDB = listOfBooksFromDB.filter { book ->
                    book.title.toLowerCasePreservingASCIIRules().contains(bookTitle)
                }
                if (bookListByTittleDB.isNotEmpty()) {
                    return@get call.respond(bookListByTittleDB)
                } else call.respondText("No books found.", status = HttpStatusCode.NotFound)
            } else call.respondText("No books found in the database.", status = HttpStatusCode.OK)
        }

        get("{id?}/book_cover") {
            var file: File = File("")
            val id = call.parameters["id"]
            if (id.isNullOrBlank()) return@get call.respondText(
                "Missing book id.",
                status = HttpStatusCode.BadRequest
            )

            val listOfBooksFromDB = db.getAllBooks()
            if (listOfBooksFromDB.isNotEmpty()) {
                val bookWeWant = listOfBooksFromDB.filter { it.idBook == id }
                if (bookWeWant.size == 1) {
                    file = File("src/main/kotlin/com/example/book-covers/" + bookWeWant[0].bookCover)
                } else {
                    call.respondText("No book found with id $id.", status = HttpStatusCode.NotFound)
                }
                if (file.exists()) {
                    call.respondFile(file)
                } else {
                    call.respondText("No image found.", status = HttpStatusCode.NotFound)
                }
            }
        }


        // Post solo pueden hacerlo los admins

        post {
            val bookData = call.receiveMultipart()
            val book = Book("", "", "", "", "", "", true,
                0, 0, "")

            // Separem el tractament de les dades entre: dades primitives i fitxers
            bookData.forEachPart { part ->
                when (part) {
                    is PartData.FormItem -> {
                        when (part.name) {
                            // "idBook" -> book.idBook = part.value
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
                        } catch (e: FileNotFoundException) {
                            println("Error " + e.message)
                        }
                    }

                    else -> {}
                }
            }


            db.insertNewBook(book)

            call.respondText("Book stored correctly.", status = HttpStatusCode.Created)
            return@post call.respond(book)
        }

        // Put solo pueden hacerlo los administradores
        put("{id?}") {
            val id = call.parameters["id"]
            if (id.isNullOrBlank()) return@put call.respondText(
                "Missing id.", status = HttpStatusCode.BadRequest)


            val bookData = call.receiveMultipart()
            val book = Book("", "", "", "", "", "", true,
                0, 0, "")

            // Separem el tractament de les dades entre: dades primitives i fitxers
            bookData.forEachPart { part ->
                when (part) {
                    is PartData.FormItem -> {
                        when (part.name) {
                            // "idBook" -> book.idBook = part.value
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
                            // Si cambia la imagen, borramos la ruta anterior y guardamos la nueva
                            if (book.bookCover != db.getBookByID(id).bookCover){
                                File("src/main/kotlin/com/example/book-covers/" +db.getBookByID(id).bookCover).delete()
                                val fileBytes = part.streamProvider().readBytes()
                                File("src/main/kotlin/com/example/book-covers/" + book.bookCover).writeBytes(fileBytes)
                                println("Imagen subida")
                            }

                        } catch (e: FileNotFoundException) {
                            println("Error " + e.message)
                        }
                    }

                    else -> {}
                }
                println("Subido ${part.name}")
            }

            val listOfBooksFromDB = db.getAllBooks()
            if (listOfBooksFromDB.isNotEmpty()) {
                if (listOfBooksFromDB.filter { it.idBook == id }.size == 1) {
                    db.updateBook(id, book)
                    return@put call.respondText(
                        "Book with id $id has been updated.", status = HttpStatusCode.Accepted
                    )
                } else return@put call.respondText(
                    "Book with id $id not found.", status = HttpStatusCode.NotFound
                )
            } else call.respondText("No books found.", status = HttpStatusCode.OK)
        }
        //Delete solo pueden hacerlo los admins
        delete("{id?}") {
            val id = call.parameters["id"]
            if (id.isNullOrBlank()) return@delete call.respondText(
                "Missing book id.",
                status = HttpStatusCode.BadRequest
            )
            val listOfBooksFromDB = db.getAllBooks()
            if (listOfBooksFromDB.isNotEmpty()) {
                if (listOfBooksFromDB.filter { it.idBook == id }.size == 1) {
                    db.deleteBook(id)
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
}


