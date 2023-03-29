package com.example.routes

import com.example.models.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File
import java.io.FileNotFoundException

fun Route.userRouting(){
    route("/users") {
        // GET
        // Todos los users
        get {
            if (userList.isNotEmpty()) {
                call.respond(userList.values)
            } else {
                call.respondText("No users found.", status = HttpStatusCode.OK)
            }
        }
        // Buscar por ID
        get ("{id}"){
            if (call.parameters["id"].isNullOrBlank()) return@get call.respondText(
                "Missing user id", status = HttpStatusCode.BadRequest
            )
            val id = call.parameters["id"]
            if (userList.isNotEmpty()) {
                //Miramos si hay un usuario con ese ID en el mapa de usuarios
                if (userList.containsKey(id) && userList[id] != null) {
                    return@get call.respond(userList[id]!!)
                } else call.respondText("User with id $id not found", status = HttpStatusCode.NotFound)
            } else call.respondText("No users found.", status = HttpStatusCode.OK)
        }

        // Get historial de libros leídos

        get ("{id?}/book_history") {
            if (call.parameters["id"].isNullOrBlank()) return@get call.respondText(
                "Missing user id.", status = HttpStatusCode.BadRequest
            )
            val id = call.parameters["id"]
            if (userList.isNotEmpty()) {
                //Miramos si hay un usuario con ese ID en el mapa de usuarios
                if (userList.containsKey(id)) {
                    // Miramos que el historial no esté vacío
                    if (userList[id]?.bookHistory!!.isNotEmpty() ) {
                        return@get call.respond(userList[id]?.bookHistory!!)
                    } else {
                        return@get call.respondText("User with id $id hasn't read any books yet.")
                    }
                } else call.respondText("User with id $id not found.", status = HttpStatusCode.NotFound)
            } else call.respondText("No users found.", status = HttpStatusCode.OK)
        }

        // Get imagen del usuario

        get("{id?}/user_image") {
            var file: File = File("")
            if (call.parameters["id"].isNullOrBlank()) return@get call.respondText(
                "Missing user id.",
                status = HttpStatusCode.BadRequest
            )
            val id = call.parameters["id"]

            if (userList.containsKey(id)) file = File("src/main/kotlin/com/example/user-images/" + userList[id]!!.userImage)
            if (file.exists()) {
                call.respondFile(file)
            } else {
                call.respondText("No image found.", status = HttpStatusCode.NotFound)
            }
        }


        // POST
        // En vez de verificar la id (que debería calcularse sola), verificamos que el correo no esté repetido

        post {
            val userData = call.receiveMultipart()
            val newUser = User("", "", "" ,"",UserType.NORMAL,
                0, mutableSetOf<String>(), false, "")

            // Separem el tractament de les dades entre: dades primitives i fitxers
            userData.forEachPart { part ->
                when (part) {
                    // Aquí recollim les dades primitives
                    // No recogemos la lista de libros leídos porque empieza con 0
                    is PartData.FormItem -> {
                        when (part.name) {
                            "idUser" -> newUser.idUser = part.value
                            "name" -> newUser.name  = part.value
                            "email" -> newUser.email = part.value
                            "password" -> newUser.password = part.value
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
                        } catch (e: FileNotFoundException){
                            println("Error " + e.message)
                        }
                    }
                    else -> {}
                }
                println("Subido ${part.name}")
            }
            println("Ahora posteamos")
            // Si no hay ningún usuario ya con ese id o ha sido borrado (el valor es nulo), lo añadimos a la booklist con esa ID
            if (!userList.containsKey(newUser.idUser) || userList[newUser.idUser] == null){
                for (user in userList.values){
                    if (user?.email == newUser.email){
                        return@post call.respondText("${newUser.email} already exists in our database.", status = HttpStatusCode.OK)
                    }
                }
                userList[newUser.idUser] = newUser
                call.respondText("User stored correctly.", status = HttpStatusCode.Created)
                return@post call.respond(newUser)
            } else return@post call.respondText("User with id ${newUser.idUser} already exists.", status = HttpStatusCode.OK)
        }

        }


        // POST Libro a historial del user

        post ("{id?}/book_history") {
            val book = call.receive<Book>()
            if (call.parameters["id"].isNullOrBlank()) return@post call.respondText(
                "Missing user id", status = HttpStatusCode.BadRequest)
            val userID = call.parameters["id"]
            if (userList.isNotEmpty()) {
                //Miramos si hay un usuario con ese ID (y el valor no es nulo) en el mapa de usuarios
                if (userList.containsKey(userID) || userList[userID] != null) {
                    // Si el historial de libros leídos no tiene el id del libro, lo añadimos
                    if (!userList[userID]?.bookHistory!!.contains(book.idBook)) {
                        userList[userID]!!.bookHistory.add(book.idBook)
                        // Si ya existe
                    } else return@post call.respondText("Book with id ${book.idBook} already exists.", status = HttpStatusCode.OK)
                    // Si no existe ese usuario en nuestro mapa
                } else call.respondText("User with id $userID not found.", status = HttpStatusCode.NotFound)
                // Lo mapeamos correctamente
                call.respondText("Book with id ${book.idBook} added to book history of user with id $userID correctly.",
                    status = HttpStatusCode.Created)
                return@post call.respond(book)
            } else return@post  call.respondText("No users found.", status = HttpStatusCode.OK)

        }

        // PUT

        put("{id?}") {
            if (call.parameters["id"].isNullOrBlank()) return@put call.respondText(
                "Missing id", status = HttpStatusCode.BadRequest)
            val id = call.parameters["id"]
            val userToUpdate = call.receive<User>()
            if (userList.isNotEmpty()){
                if (userList.containsKey(id)){
                    userList[id!!] = userToUpdate
                    return@put call.respondText(
                        "User with id $id has been updated.", status = HttpStatusCode.Accepted
                    )
                } else call.respondText("User with id $id not found.", status = HttpStatusCode.NotFound)

            } else  call.respondText("No users found.", status = HttpStatusCode.NotFound)
        }

        //DELETE (solo los admins)

        delete("{id}"){
            if (call.parameters["id"].isNullOrBlank()) return@delete call.respondText(
                "Missing user id",
                status = HttpStatusCode.BadRequest
            )
            val id = call.parameters["id"]
            if (userList.isNotEmpty()){
                userList[id!!] = null
                return@delete call.respondText(
                    "User removed successfully.", status = HttpStatusCode.Accepted)
            } else {
                call.respondText(
                    "User with id $id not found.", status = HttpStatusCode.NotFound
                )
            }

        }

        // DELETE libro leído (los usuarios normales también pueden)
        delete("{userid?}/book_history/{bookid?}"){
            if (call.parameters["userid"].isNullOrBlank()) return@delete call.respondText(
                "Missing user id", status = HttpStatusCode.BadRequest
            )
            val userID = call.parameters["userid"]
            val bookID = call.parameters["bookid"]
            if (userList.isNotEmpty()){
                if (userList[userID]?.bookHistory!!.isNotEmpty()){
                    userList[userID]!!.bookHistory.remove(bookID)
                    return@delete call.respondText(
                        "Book with id $bookID removed successfully from user history.",
                        status = HttpStatusCode.Accepted)
                }
                }else {
                    call.respondText("No users found.", status = HttpStatusCode.OK)
                }

        }

}

