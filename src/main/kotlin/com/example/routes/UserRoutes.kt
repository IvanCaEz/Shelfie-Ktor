package com.example.routes

import com.example.models.Book
import com.example.models.User
import com.example.models.bookList
import com.example.models.userList
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

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

        get ("{id?}/book_history") {
            if (call.parameters["id"].isNullOrBlank()) return@get call.respondText(
                "Missing user id", status = HttpStatusCode.BadRequest
            )
            val id = call.parameters["id"]
            if (userList.isNotEmpty()) {
                //Miramos si hay un usuario con ese ID en el mapa de usuarios
                if (userList.containsKey(id)) {
                    // Miramos que el historial no esté vacío
                    if (userList[id]?.bookHistory!!.isNotEmpty() ) {
                        return@get call.respond(userList[id]?.bookHistory!!)
                    } else {
                        return@get call.respondText("User with id $id hasn't read any books yet")
                    }
                } else call.respondText("User with id $id not found", status = HttpStatusCode.NotFound)
            } else call.respondText("No users found.", status = HttpStatusCode.OK)
        }

        // Get historial de libros leídos

        // POST

        post {
            val user = call.receive<User>()
            // Si no hay un usuario con ese ID o lo hay pero el valor es nulo (ha sido eliminado), se añade
           if (!userList.containsKey(user.idUser) || userList[user.idUser] == null){
                userList[user.idUser] = user
                call.respondText("User stored correctly", status = HttpStatusCode.Created)
                return@post call.respond(user)
            } else return@post call.respondText("User with id ${user.idUser} already exists", status = HttpStatusCode.OK)
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
                    // Si el historial de libros leídos no tiene mapeado el id del libro o lo tiene pero
                    // el valor es nulo, lo añádimos al mapa
                    if (!userList[userID]?.bookHistory!!.containsKey(book.idBook)
                        || userList[userID]?.bookHistory!![book.idBook] == null ) {
                        userList[userID]!!.bookHistory[book.idBook] = book
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
                    userList[userID]!!.bookHistory[bookID!!] = null
                    return@delete call.respondText(
                        "Book with id $bookID removed successfully from user history.",
                        status = HttpStatusCode.Accepted)
                }
                }else {
                    call.respondText("No users found.", status = HttpStatusCode.OK)
                }

        }

    }
}

