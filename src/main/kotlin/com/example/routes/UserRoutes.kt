package com.example.routes

import com.example.database.Database
import com.example.models.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File
import java.io.FileNotFoundException

fun Route.userRouting() {

    route("/users") {
        /**
         * GET todos los usuarios
         */
        get {
            val userList = Database().getAllUsers()
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
            val userList = Database().getAllUsers()
            if (userList.isNotEmpty()) {
                if (userList.filter { it.idUser == id }.size == 1) {
                    return@get call.respond(Database().getUserByID(id))
                } else call.respondText("User with id $id not found", status = HttpStatusCode.NotFound)
            } else call.respondText("No users found.", status = HttpStatusCode.OK)
        }

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
            val userList = Database().getAllUsers()
            if (userList.isNotEmpty()) {
                if (userList.filter { it.idUser == id }.size == 1) {
                    println("User encontrado")
                    // No sé por qué no se añade el historial en la otra función así que aquí se lo añadimos
                   userList[0].bookHistory = Database().getBookHistoryFromUser(id)
                    if (userList[0].bookHistory.isNotEmpty()) {
                        return@get call.respond(userList[0].bookHistory)
                    } else {
                        return@get call.respondText("User with id $id hasn't read any books yet.")
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

            val userList = Database().getAllUsers()
            if (userList.isNotEmpty()) {
                if (userList.filter { it.idUser == id }.size == 1) {
                    file = File("src/main/kotlin/com/example/user-images/" + userList[0].userImage)
                } else {
                    call.respondText("No user found with id $id.", status = HttpStatusCode.NotFound)
                }
                if (file.exists()) {
                    call.respondFile(file)
                } else {
                    call.respondText("No image found.", status = HttpStatusCode.NotFound)
                }
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
            val newUser = User(
                "", "", "", "", UserType.NORMAL,
                0, mutableSetOf<Int>(), false, ""
            )
            // Separem el tractament de les dades entre: dades primitives i fitxers
            userData.forEachPart { part ->
                when (part) {
                    // No recogemos la lista de libros leídos porque empieza con 0
                    is PartData.FormItem -> {
                        when (part.name) {
                            //"idUser" -> newUser.idUser = part.value
                            "name" -> newUser.name = part.value
                            "email" -> newUser.email = part.value
                            "password" -> newUser.password = part.value
                            "userType" -> when (part.value){
                                "ADMIN" -> newUser.userType = UserType.ADMIN

                                else -> newUser.userType = UserType.NORMAL
                            }
                            "borrowedBooksCounter" -> newUser.borrowedBooksCounter = part.value.toInt()
                            "banned" -> newUser.banned = part.value.toBoolean()
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
            val userList = Database().getAllUsers()
            if (userList.any { it.email == newUser.email }){
                call.respondText("Email ${newUser.email} already exists in our database.",
                    status = HttpStatusCode.OK)
            } else{
                var nextID = (userList.size + 1)
                var foundID = false
                // Recorre los ids hasta encontrar un número que no esté repetido, si lo encuentra sale del bucle,
                // si no lo encuentra, el id será al tamaño de la lista de usuarios+1
                // El for es para mantener un orden numérico y que no se complique cuando se eliminan usuarios
                for (n in 1..nextID) {
                    if (userList.none { it.idUser == n.toString() }) {
                        nextID = n
                        foundID = true
                        break
                    }
                }
                if (!foundID) {
                    while (userList.filter { it.idUser == nextID.toString() }.size == 1) {
                        nextID++
                    }
                }
                newUser.idUser = nextID.toString()
                Database().insertNewUser(newUser)

                call.respondText("User stored correctly.", status = HttpStatusCode.Created)
                return@post call.respond(newUser)
            }
        }

        // POST Libro a historial del user

        post("{userid?}/book_history") {
            val book = call.receive<Book>()
            val userID = call.parameters["userid"]
            if (userID.isNullOrBlank()) return@post call.respondText(
                "Missing user id.", status = HttpStatusCode.BadRequest
            )
            val userList = Database().getAllUsers()
            if (userList.isNotEmpty()) {
                if (userList.filter { it.idUser == userID }.size == 1) {
                    // Si el historial de libros leídos no tiene el id del libro, lo añadimos
                    // No sé por qué no se añade el historial en la otra función así que aquí se lo añadimos
                    userList[0].bookHistory = Database().getBookHistoryFromUser(userID)
                    if (!userList[0].bookHistory.contains(book.idBook.toInt())) {
                        // Lo añadimos
                        Database().addBookRead(userID, book.idBook)

                        call.respondText(
                            "Book with id ${book.idBook} added to book history of user with id $userID correctly.",
                            status = HttpStatusCode.Created
                        )
                        return@post call.respond(book)
                        // Si ya existe
                    } else return@post call.respondText(
                        "Book with id ${book.idBook} already exists.",
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

            val userToUpdate = call.receive<User>()
            val userList = Database().getAllUsers()
            if (userList.isNotEmpty()) {
                if (userList.filter { it.idUser == id }.size == 1) {
                    Database().updateUser(id, userToUpdate)
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
            val userList = Database().getAllUsers()
            if (userList.isNotEmpty()) {
                if (userList.filter { it.idUser == id }.size == 1) {
                    Database().deleteUser(id!!)
                    return@delete call.respondText(
                        "User removed successfully.", status = HttpStatusCode.Accepted
                    )
                } else {
                    call.respondText(
                        "User with id $id not found.", status = HttpStatusCode.NotFound
                    )
                }
            }
        }


        // DELETE libro leído (los usuarios normales también pueden)
        delete("{userid?}/book_history/{bookid?}") {
            val userID = call.parameters["userid"]
            if (userID.isNullOrBlank()) return@delete call.respondText(
                "Missing user id", status = HttpStatusCode.BadRequest
            )

            val bookID = call.parameters["bookid"]
            val userList = Database().getAllUsers()
            if (userList.isNotEmpty()) {
                if (userList.filter { it.idUser == userID }.size == 1) {
                    // Si el historial de libros leídos  tiene el id del libro
                    // No sé por qué no se añade el historial en la otra función así que aquí se lo añadimos
                    userList[0].bookHistory = Database().getBookHistoryFromUser(userID)
                    if (userList[0].bookHistory.contains(bookID!!.toInt())) {
                        // Lo quitamos
                        println("HOli")
                        Database().deleteBookRead(userID, bookID)
                        return@delete call.respondText(
                            "Book with id $bookID removed successfully from user history.",
                            status = HttpStatusCode.Accepted
                        )
                    } else call.respondText("Book with id $bookID not found in user's history.", status = HttpStatusCode.OK)
                } else call.respondText("No users found.", status = HttpStatusCode.OK)
            }
        }
    }
}


