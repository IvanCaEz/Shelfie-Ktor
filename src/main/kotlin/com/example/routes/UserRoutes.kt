package com.example.routes

import com.example.database.Database
import com.example.models.*
import com.google.gson.Gson
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File
import java.io.FileNotFoundException

fun Route.userRouting() {
    val db = Database()
    route("/users") {
        /**
         * GET todos los usuarios
         */
        //authenticate("auth-digest") {

            get {
                val userList = db.getAllUsers()
                if (userList.isNotEmpty()) {
                    call.respond(userList)
                } else call.respondText("No users found.", status = HttpStatusCode.OK)
            }
            /**
             * GET usuario por ID
             * Miramos si tenemos un usuario con ese ID en nuestra base de datos
             */
            get("{id}") {
                val id = call.parameters["id"]

                if (id.isNullOrBlank()) return@get call.respondText(
                    "Missing user id.", status = HttpStatusCode.BadRequest
                )
                val user = db.getUserByID(id)
                val userList = mutableListOf<User>()
                userList.add(user)
                if (userList.isNotEmpty()) {
                    if (userList.filter { it.idUser == id }.size == 1) {
                        return@get call.respond(db.getUserByID(id))
                    } else call.respondText("User with id $id not found", status = HttpStatusCode.NotFound)
                } else call.respondText("No users found.", status = HttpStatusCode.OK)
            }

            /**
             * GET usuario por username
             * Miramos si tenemos un usuario con ese username en nuestra base de datos
             */
            get("/username/{userName}") {
                val userName = call.parameters["userName"]

                if (userName.isNullOrBlank()) return@get call.respondText(
                    "Missing user id.", status = HttpStatusCode.BadRequest
                )
                val user = db.getUserByUserName(userName)
                val userList = mutableListOf<User>()
                userList.add(user)
                if (userList.isNotEmpty()) {
                    if (userList.filter { it.userName == userName }.size == 1) {
                        return@get call.respond(db.getUserByUserName(userName))
                    } else call.respondText("User with username $userName not found.", status = HttpStatusCode.NotFound)
                } else call.respondText("No users found.", status = HttpStatusCode.OK)
            }
        //}

        /**
         * GET historial de libros leídos
         * Miramos si tenemos un usuario con ese ID en nuestro mapa / base de datos
         * Si el historial de libros leídos no está vacío, retornamos los IDs de los libros que ha leído
         */
        get("{id?}/book_history") {
            val id = call.parameters["id"]
            if (id.isNullOrBlank()) return@get call.respondText(
                "Missing user id.", status = HttpStatusCode.BadRequest
            )
            val userList = db.getAllUsers()
            if (userList.isNotEmpty()) {
                if (userList.filter { it.idUser == id }.size == 1) {
                    println("User encontrado")
                    // No sé por qué no se añade el historial en la otra función así que aquí se lo añadimos
                    userList[0].bookHistory = db.getBookHistoryFromUser(id)
                    if (userList[0].bookHistory.isNotEmpty()) {
                        // Por cada ID de libro que tiene el bookHistory, obtenemos los datos del libro y retornamos
                        // la lista
                        val historyList = mutableListOf<Book>()
                        userList[0].bookHistory.forEach { bookID ->
                            historyList.add(db.getBookByID(bookID.toString()))
                        }
                        return@get call.respond(historyList)
                    } else {
                        return@get call.respondText("User with id $id hasn't read any books yet.")
                    }
                } else call.respondText("User with id $id not found.", status = HttpStatusCode.NotFound)
            } else call.respondText("No users found.", status = HttpStatusCode.OK)
        }

        /**
         * GET préstamos de libros del usuario
         * Miramos si tenemos un usuario con ese ID en la base de datos
         * Si el número de libros prestados no es 0, devolvemos la lista de libros prestados (3 máximo)
         */
        get("{id?}/book_loans") {
            val id = call.parameters["id"]
            if (id.isNullOrBlank()) return@get call.respondText(
                "Missing user id.", status = HttpStatusCode.BadRequest
            )
            val userList = db.getAllUsers()
            if (userList.isNotEmpty()) {
                if (userList.filter { it.idUser == id }.size == 1) {
                    println("User encontrado")
                    // No sé por qué no se añade el historial en la otra función así que aquí se lo añadimos
                    val userLoans = db.getUserLoans(id)
                    if (userLoans.isNotEmpty()) {
                        return@get call.respond(userLoans)
                    } else {
                        return@get call.respond(listOf<BookLoan>())
                    }
                } else call.respondText("User with id $id not found.", status = HttpStatusCode.NotFound)
            } else call.respondText("No users found.", status = HttpStatusCode.OK)
        }
        /**
         * GET préstamos de libros específico
         * Miramos si tenemos un usuario con ese ID en la base de datos
         * Si el número de libros prestados no es 0, devolvemos la lista de libros prestados (3 máximo)
         */
        get("{id?}/book_loans/{bookID}") {
            val id = call.parameters["id"]
            val bookID = call.parameters["bookID"]
            if (id.isNullOrBlank()) return@get call.respondText(
                "Missing user id.", status = HttpStatusCode.BadRequest
            )
            val userList = db.getAllUsers()
            if (userList.isNotEmpty()) {
                if (userList.filter { it.idUser == id }.size == 1) {
                    println("User encontrado")
                    // Obtenemos los préstamos del usuario
                    val userLoans = db.getUserLoans(id)
                    // Miramos que tenga ese libro prestado y lo devolvemos
                    if (userLoans.filter { it.idBook == bookID }.size == 1) {
                        val bookLoan = db.getLoanByBookID(id, bookID!!)
                        return@get call.respond(bookLoan)
                    } else {
                        return@get call.respondText("Book with id $bookID was not been loaned.")
                    }
                } else call.respondText("User with id $id not found.", status = HttpStatusCode.NotFound)
            } else call.respondText("No users found.", status = HttpStatusCode.OK)
        }

        /**
         * GET imagen del usuario
         * Miramos si tenemos un usuario con ese ID en nuestro mapa / base de datos
         * Si lo tenemos, añadimos a la ruta de la carpeta de las imagenes el nombre de la imagen del usuario
         */

        get("{id?}/user_image") {
            var file: File = File("")
            if (call.parameters["id"].isNullOrBlank()) return@get call.respondText(
                "Missing user id.",
                status = HttpStatusCode.BadRequest
            )
            val id = call.parameters["id"]

            val user = db.getUserByID(id!!)
            file = File("src/main/kotlin/com/example/user-images/" + user.userImage)

            if (file.exists()) {
                call.respondFile(file)
            } else {
                call.respondText("No image found.", status = HttpStatusCode.NotFound)
            }

        }

        /**
         * POST Usuario
         * Recibimos los datos por partes y añadimos la foto a nuestra carpeta de imágenes
         * (Esto igual cambiarlo y hacer que se registren con una foto placeholder y luego puedan cambiarla
         * una vez registrados)
         * Para añadirlo, verificamos que el correo no esté ya en la base de datos,
         * la ID se generará de forma automática según el tamaño de la lista de usuarios
         *
         */
        post {
            val userData = call.receiveMultipart()
            var newUser = User(
                "", "", "", "", "", "", UserType.NORMAL,
                0, setOf<Int>(), false, ""
            )
            val gson = Gson()
            // Separem el tractament de les dades entre: dades primitives i fitxers
            userData.forEachPart { part ->
                when (part) {
                    is PartData.FormItem -> {
                        if (part.name == "body") {
                            newUser = gson.fromJson(part.value, User::class.java)
                        }
                    }
                    // Aquí recollim els fitxers
                    // Habrá que hacer en el android que este campo sea una imagen placeholder
                    // o no guardarla hasta que la cambie
                    is PartData.FileItem -> {
                        try {
                            newUser.userImage = part.originalFileName as String
                            val fileBytes = part.streamProvider().readBytes()
                            File("src/main/kotlin/com/example/user-images/" + newUser.userImage).writeBytes(fileBytes)
                            println("Imagen subida")
                        } catch (e: FileNotFoundException) {
                            println("Error " + e.message)
                        }
                    }

                    else -> {}
                }

                println("Tipo user: ${newUser.userType}")

                println("Subido ${part.name}")
            }
            println("Ahora posteamos")
            // Si no hay ningún usuario ya con ese mail, lo añadimos a la base de datos con esa ID
            val userList = db.getAllUsers()
            if (userList.any { it.email == newUser.email }) {
                call.respondText(
                    "Email ${newUser.email} already exists in our database.",
                    status = HttpStatusCode.OK
                )
            } else {

                db.insertNewUser(newUser)

                return@post call.respondText("User stored correctly.", status = HttpStatusCode.Created)
            }
        }


        // POST Préstamo al user

        post("{userid?}/book_loans") {
            val bookLoan = call.receive<BookLoan>()
            val userID = call.parameters["userid"]
            if (userID.isNullOrBlank()) return@post call.respondText(
                "Missing user id.", status = HttpStatusCode.BadRequest
            )
            val userLoans = db.getUserLoans(userID)
            // Si tiene menos de 3 préstamos puede pedir prestado otro
            if (userLoans.size < 3) {
                // Si el libro que quiere pedir prestado no lo ha pedido ya prestado
                if (userLoans.none { it.idBook == bookLoan.idBook }) {
                    // Lo añadimos
                    db.addBookLoan(bookLoan)

                    return@post call.respondText(
                        "User with id $userID has borrowed book with id ${bookLoan.idBook} until ${bookLoan.endDate}.",
                        status = HttpStatusCode.Created
                    )
                    // Si ya existe
                } else return@post call.respondText(
                    "This user has already this book on loan.",
                    status = HttpStatusCode.OK
                )
                // Si no existe ese usuario en nuestro mapa
            } else return@post call.respondText("User with id $userID has 3 active loans.", status = HttpStatusCode.OK)
        }

        // POST Libro a historial del user

        post("{userid?}/book_history") {
            val bookID = call.receive<Int>()
            val userID = call.parameters["userid"]
            if (userID.isNullOrBlank()) return@post call.respondText(
                "Missing user id.", status = HttpStatusCode.BadRequest
            )
            val userList = db.getAllUsers()
            if (userList.isNotEmpty()) {
                if (userList.filter { it.idUser == userID }.size == 1) {
                    // Si el historial de libros leídos no tiene el id del libro, lo añadimos
                    // No sé por qué no se añade el historial en la otra función así que aquí se lo añadimos
                    userList[0].bookHistory = db.getBookHistoryFromUser(userID)
                    if (!userList[0].bookHistory.contains(bookID.toInt())) {
                        // Lo añadimos
                        db.addBookRead(userID, bookID)

                        return@post call.respondText(
                            "Book with id $bookID added to book history of user with id $userID correctly.",
                            status = HttpStatusCode.Created
                        )
                        // Si ya existe
                    } else return@post call.respondText(
                        "Book with id $bookID already exists.",
                        status = HttpStatusCode.OK
                    )
                    // Si no existe ese usuario en nuestro mapa
                } else call.respondText("User with id $userID not found.", status = HttpStatusCode.NotFound)
            } else return@post call.respondText("No users found.", status = HttpStatusCode.OK)
        }

        // PUT

        put("{id?}") {
            val id = call.parameters["id"]
            if (id.isNullOrBlank()) return@put call.respondText(
                "Missing id", status = HttpStatusCode.BadRequest
            )
            val userData = call.receiveMultipart()
            var userToUpdate = User(
                "", "", "", "", "", "", UserType.NORMAL,
                0, setOf<Int>(), false, ""
            )
            val gson = Gson()
            // Separem el tractament de les dades entre: dades primitives i fitxers
            userData.forEachPart { part ->
                when (part) {
                    is PartData.FormItem -> {
                        userToUpdate = gson.fromJson(part.value, User::class.java)
                    }
                    // Aquí recollim els fitxers
                    // Habrá que hacer en el android que este campo sea una imagen placeholder
                    // o no guardarla hasta que la cambie
                    is PartData.FileItem -> {
                        try {
                            userToUpdate.userImage = part.originalFileName as String
                            if (userToUpdate.userImage != db.getUserByID(id).userImage) {
                                // File("src/main/kotlin/com/example/user-images/" + db.getUserByID(id).userImage).delete()
                                val fileBytes = part.streamProvider().readBytes()
                                File("src/main/kotlin/com/example/user-images/" + userToUpdate.userImage).writeBytes(
                                    fileBytes
                                )
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
            userToUpdate.bookHistory = db.getBookHistoryFromUser(id)
            userToUpdate.borrowedBooksCounter = db.getUserLoans(id).size
            val userList = db.getAllUsers()
            if (userList.isNotEmpty()) {
                if (userList.filter { it.idUser == id }.size == 1) {
                    db.updateUser(id, userToUpdate)
                    return@put call.respondText(
                        "User with id $id has been updated.", status = HttpStatusCode.Accepted
                    )
                } else call.respondText("User with id $id not found.", status = HttpStatusCode.NotFound)

            } else call.respondText("No users found.", status = HttpStatusCode.NotFound)
        }

        //DELETE (solo los admins)

        delete("{id}") {
            val id = call.parameters["id"]
            if (call.parameters["id"].isNullOrBlank()) return@delete call.respondText(
                "Missing user id",
                status = HttpStatusCode.BadRequest
            )
            // Primero quitamos las reviews y luego al usuario
            db.deleteReviewsFromUser(id!!)
            db.deleteUser(id)
            return@delete call.respondText(
                "User removed successfully.", status = HttpStatusCode.Accepted
            )
        }

        // PUT libro prestado
        put("{userid?}/book_loans/{bookid?}") {
            val userID = call.parameters["userid"]
            if (userID.isNullOrBlank()) return@put call.respondText(
                "Missing user id", status = HttpStatusCode.BadRequest
            )
            val bookID = call.parameters["bookid"]
            val bookLoanToUpdate = call.receive<BookLoan>()
            val bookLoans = db.getUserLoans(userID)
            if (bookLoans.filter { it.idBook == bookID }.size == 1) {
                db.updateBookLoan(userID, bookLoanToUpdate)
                return@put call.respondText(
                    "Bookloan of id ${bookLoanToUpdate.idBook} has been updated.",
                    status = HttpStatusCode.Accepted
                )
            } else call.respondText(
                "Book with id $bookID not found in user's loaned books.",
                status = HttpStatusCode.OK
            )

        }

        // DELETE libro prestado
        delete("{userid?}/book_loans/{bookid?}") {
            val userID = call.parameters["userid"]
            if (userID.isNullOrBlank()) return@delete call.respondText(
                "Missing user id", status = HttpStatusCode.BadRequest
            )
            val bookID = call.parameters["bookid"]
            val bookLoans = db.getUserLoans(userID)
            if (bookLoans.filter { it.idBook == bookID }.size == 1) {
                // Lo quitamos
                db.deleteBookLoan(userID, bookID!!)
                return@delete call.respondText(
                    "Book with id $bookID has been returned.",
                    status = HttpStatusCode.Accepted
                )
            } else call.respondText(
                "Book with id $bookID not found in user's loaned books.",
                status = HttpStatusCode.OK
            )

        }


        // DELETE libro leído (los usuarios normales también pueden)
        delete("{userid?}/book_history/{bookid?}") {
            val userID = call.parameters["userid"]
            if (userID.isNullOrBlank()) return@delete call.respondText(
                "Missing user id", status = HttpStatusCode.BadRequest
            )

            val bookID = call.parameters["bookid"]
            val userList = db.getAllUsers()
            if (userList.isNotEmpty()) {
                if (userList.filter { it.idUser == userID }.size == 1) {
                    // Si el historial de libros leídos tiene el id del libro
                    // No sé por qué no se añade el historial en la otra función así que aquí se lo añadimos
                    userList[0].bookHistory = db.getBookHistoryFromUser(userID)
                    if (userList[0].bookHistory.contains(bookID!!.toInt())) {
                        // Lo quitamos
                        println("HOli")
                        db.deleteBookRead(userID, bookID)
                        return@delete call.respondText(
                            "Book with id $bookID removed successfully from user history.",
                            status = HttpStatusCode.Accepted
                        )
                    } else call.respondText(
                        "Book with id $bookID not found in user's history.",
                        status = HttpStatusCode.OK
                    )
                } else call.respondText("No users found.", status = HttpStatusCode.OK)
            }
        }
    }

    //}

}


