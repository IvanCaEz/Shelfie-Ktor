package com.example.routes

import com.example.models.User
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
                call.respond(userList)
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
            for (user in userList) {
                if (user.idUser == id?.toInt()) return@get call.respond(user)
            }
            call.respondText(
                "User with id $id not found",
                status = HttpStatusCode.NotFound
            )
        }

        // POST

        post {
            val user = call.receive<User>()
            // Si no hay ningún usuario ya con ese id, lo añadimos
            if (userList.none { user.idUser == it.idUser }){
                userList.add(user)
            } else {
                return@post call.respondText(
                    "Book with id ${user.idUser} already exists", status = HttpStatusCode.OK)
            }
            call.respondText("User stored correctly", status = HttpStatusCode.Created)

        }

        // PUT

        put("{id?}") {
            if (call.parameters["id"].isNullOrBlank()) return@put call.respondText(
                "Missing id",
                status = HttpStatusCode.BadRequest
            )
            val id = call.parameters["id"]
            val userToUpdate = call.receive<User>()
            for (user in userList) {
                if (user.idUser == id?.toInt()) {
                    user.idUser = userToUpdate.idUser
                    user.name = userToUpdate.name
                    user.email = userToUpdate.email
                    user.password = userToUpdate.password
                    user.userType = userToUpdate.userType
                    user.borrowedBooksCounter = userToUpdate.borrowedBooksCounter
                    user.bookHistory = userToUpdate.bookHistory
                    user.banned = userToUpdate.banned


                    return@put call.respondText(
                        "User with id $id has been updated",
                        status = HttpStatusCode.Accepted
                    )
                }
            }
            call.respondText(
                "User with id $id not found.",
                status = HttpStatusCode.NotFound
            )
        }

        //DELETE

        delete("{id}"){
            if (call.parameters["id"].isNullOrBlank()) return@delete call.respondText(
                "Missing user id",
                status = HttpStatusCode.BadRequest
            )
            val id = call.parameters["id"]
            for (user in userList) {
                if (user.idUser == id?.toInt()) {
                    userList.remove(user)
                    return@delete call.respondText("User removed correctly.", status = HttpStatusCode.Accepted)
                }
            }
            call.respondText(
                "User with id $id not found.",
                status = HttpStatusCode.NotFound
            )


        }
    }

}