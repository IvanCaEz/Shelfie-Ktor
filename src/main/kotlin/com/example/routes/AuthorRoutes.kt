package com.example.routes

import com.example.models.bookList
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.*


//Esta ruta solo se podrá usar para obtener los libros que ha escrito un determinado Autor
fun Route.authorRouting(){
    route("/author"){
        //
        get ( "{name}" ){
            if (call.parameters["name"].isNullOrBlank()) return@get call.respondText(
                "Missing author name.", status = HttpStatusCode.BadRequest
            )
            val authorName = call.parameters["name"]?.toLowerCasePreservingASCIIRules()
            val listaLibrosPorAutor = bookList.filterValues { book ->
                book?.author?.toLowerCasePreservingASCIIRules() == authorName }
            if (bookList.filterValues { book ->
                    book?.author?.toLowerCasePreservingASCIIRules() == authorName }.isNotEmpty()){
                return@get call.respond(listaLibrosPorAutor)
            } else call.respondText("No author found.", status = HttpStatusCode.OK)
        }

    }
}