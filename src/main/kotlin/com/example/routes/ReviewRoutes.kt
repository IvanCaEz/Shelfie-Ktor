package com.example.routes

import com.example.models.Review
import com.example.models.bookList
import com.example.models.reviewList
import com.example.models.userList
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun Route.reviewRouting() {
    route("/books/{bookid?}/reviews") {

        // GET

        get {
            val bookID = call.parameters["bookid"]

            if (bookList.isNotEmpty()) {
                if (bookList.containsKey(bookID)) {
                    //Miramos que no esté vacía la lista de reviews del libro
                    val bookReviews = reviewList.filter{it.idBook == bookID}
                    if (bookReviews.isNotEmpty()){
                        return@get call.respond(bookReviews)
                    } else call.respondText("No reviews found in book $bookID.", status = HttpStatusCode.NotFound)
                } else call.respondText("Book with id $bookID not found.", status = HttpStatusCode.NotFound)
            } else call.respondText("No books found.", status = HttpStatusCode.NotFound)
        }

        // GET por ID

        get("{id}") {
            if (call.parameters["id"].isNullOrBlank()) return@get call.respondText(
                "Missing book id", status = HttpStatusCode.BadRequest
            )
            val bookID = call.parameters["bookid"]
            val id = call.parameters["id"]

            if (bookList.isNotEmpty()) {
                if (bookList.containsKey(bookID)) {
                    //Miramos que no esté vacía la lista de reviews del libro
                    val bookReviews = reviewList.filter{it.idBook == bookID}
                    if (bookReviews.isNotEmpty()) {
                        // Filtramos las reviews del libro y obtenemos una lista con la review que que queremos
                        val review = bookReviews.filter { it.idReview == id }
                        if (review.isNotEmpty()) {
                            // Devolvemos el primer elemento ya que el filtro debería devolver solo una review
                            return@get call.respond(review[0])
                        } else call.respondText(
                            "Review with id $id not found on book with id $bookID.",
                            status = HttpStatusCode.NotFound
                        )
                    } else call.respondText("No reviews found in book $bookID.", status = HttpStatusCode.OK)
                } else call.respondText("Book with id $bookID not found.", status = HttpStatusCode.NotFound)
            } else call.respondText("No books found.", status = HttpStatusCode.NotFound)


        }

        // POST

        post {
            val bookID = call.parameters["bookid"]
            val reviewCall = call.receive<Review>()
            // Filtramos lista de reviews por ID de libro y filtramos de nuevo por ID de review
            val bookReviews = reviewList.filter{it.idBook == bookID}
            val review = bookReviews.filter { it.idReview == reviewCall.idReview }
            // Si está vacía la añadimos
            if (review.isEmpty()){
                reviewList.add(reviewCall)
                call.respondText("Review stored correctly.", status = HttpStatusCode.Created)
                return@post call.respond(review)
            } else return@post call.respondText("Review with id ${reviewCall.idReview} already exists.", status = HttpStatusCode.OK)
        }

        // PUT
        put("{id}") {
            val bookID = call.parameters["bookid"]

            if (bookID.isNullOrBlank()) return@put call.respondText(
                "Missing book id.",
                status = HttpStatusCode.BadRequest
            )
            val bookReviews = reviewList.filter{it.idBook == bookID}
            val reviewID = call.parameters["id"]
            val commentToUpdate = call.receive<Review>()
            val review = bookReviews.filter { it.idReview == commentToUpdate.idReview }
            if (bookList.isNotEmpty()) {
                // Si las el libro tiene reviews, buscamos la review que queremos
                if (bookReviews.isNotEmpty()) {
                    if (review.isNotEmpty()) {
                        // Eliminamos de la lista de reviews la review que queremos updatear (vieja)
                        // y añadimos la updateada (nueva)
                        reviewList.remove(review[0])
                        reviewList.add(commentToUpdate)
                            return@put call.respondText(
                                "Review with id $reviewID has been updated.", status = HttpStatusCode.Accepted)
                        } else call.respondText("Review with id $reviewID not found in book $bookID.", status = HttpStatusCode.NotFound)
                    } else call.respondText("No reviews found in book $bookID.", status = HttpStatusCode.OK)
                } else call.respondText("No books found.", status = HttpStatusCode.NotFound)
            }
        }

        // Delete

        delete("{id}") {
            val bookID = call.parameters["bookid"]
            if (bookID.isNullOrBlank()) return@delete call.respondText(
                "Missing book id",
                status = HttpStatusCode.BadRequest
            )
            val reviewID = call.parameters["id"]
            val bookReviews = reviewList.filter{it.idBook == bookID}
            val review = bookReviews.filter { it.idReview == reviewID }
            if (bookList.isNotEmpty()) {
                if (bookReviews.isNotEmpty()) {
                    if (review.isNotEmpty()){
                        reviewList.remove(review[0])
                        return@delete call.respondText(
                            "Review with id $reviewID removed from book with id $bookID.",
                            status = HttpStatusCode.Accepted)
                    } else  call.respondText(
                        "Review with id $reviewID from book with id $bookID not found.",
                        status = HttpStatusCode.NotFound)
                }else call.respondText("No reviews found in book with id $bookID.", status = HttpStatusCode.OK)
            } else call.respondText("No books found.", status = HttpStatusCode.NotFound)

        }

    }

