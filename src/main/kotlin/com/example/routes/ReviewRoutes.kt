package com.example.routes

import com.example.models.Review
import com.example.models.bookList
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
                    if (bookList[bookID]!!.reviews.isNotEmpty()) {
                        return@get call.respond(bookList[bookID]!!.reviews)
                    } else call.respondText("No reviews found in book $bookID", status = HttpStatusCode.OK)
                } else call.respondText("Book with id $bookID not found", status = HttpStatusCode.NotFound)
            } else call.respondText("No books found.", status = HttpStatusCode.OK)
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
                    if (bookList[bookID]!!.reviews.isNotEmpty()) {
                        if (bookList[bookID]!!.reviews.containsKey(id) && bookList[bookID]!!.reviews[id] != null) {
                            return@get call.respond(bookList[bookID]!!.reviews[id]!!)
                        } else call.respondText(
                            "Review with id $id not found on book with id $bookID",
                            status = HttpStatusCode.NotFound
                        )
                    } else call.respondText("No reviews found in book $bookID", status = HttpStatusCode.OK)
                } else call.respondText("Book with id $bookID not found", status = HttpStatusCode.NotFound)
            } else call.respondText("No books found.", status = HttpStatusCode.OK)


        }

        // POST

        post {
            val bookID = call.parameters["bookid"]
            val review = call.receive<Review>()
            // Si no hay una review con el ID de la review o lo hay pero el valor es nulo, se añade
            if (!bookList[bookID]!!.reviews.containsKey(review.idReview) ||
                bookList[bookID]!!.reviews[review.idReview] == null ) {
                bookList[bookID]!!.reviews[review.idReview] = review
                call.respondText("Review stored correctly.", status = HttpStatusCode.Created)
                return@post call.respond(review)
            } else return@post call.respondText("Review with id ${review.idReview} already exists.", status = HttpStatusCode.OK)
        }

        // PUT
        put("{id}") {
            val bookID = call.parameters["bookid"]

            if (bookID.isNullOrBlank()) return@put call.respondText(
                "Missing book id",
                status = HttpStatusCode.BadRequest
            )
            val reviewID = call.parameters["id"]
            val commentToUpdate = call.receive<Review>()
            if (bookList.isNotEmpty()) {
                if (bookList.containsKey(bookID) && bookList[bookID] != null) {
                    if (bookList[bookID]!!.reviews.isNotEmpty()) {
                        if (bookList[bookID]!!.reviews.containsKey(reviewID) &&
                            bookList[bookID]!!.reviews[reviewID] != null) {
                            bookList[bookID]!!.reviews[reviewID!!] = commentToUpdate
                            return@put call.respondText(
                                "Review with id $reviewID has been updated", status = HttpStatusCode.Accepted)
                        }
                    } else call.respondText("No reviews found in book $bookID", status = HttpStatusCode.OK)
                } else call.respondText("Book with id $bookID not found", status = HttpStatusCode.NotFound)
            } else call.respondText("No books found.", status = HttpStatusCode.OK)
        }

        // Delete

        delete("{id}") {
            val bookID = call.parameters["bookid"]
            if (bookID.isNullOrBlank()) return@delete call.respondText(
                "Missing book id",
                status = HttpStatusCode.BadRequest
            )
            val reviewID = call.parameters["id"]
            if (bookList.isNotEmpty()) {
                if (bookList[bookID]!!.reviews.isNotEmpty()) {
                    if (bookList[bookID]!!.reviews.containsKey(reviewID)){
                        bookList[bookID]!!.reviews[reviewID!!] = null
                        return@delete call.respondText(
                            "Review with id $reviewID removed from book with id $bookID.",
                            status = HttpStatusCode.Accepted)
                    } else  call.respondText(
                        "Review with id $reviewID from book with id $bookID not found.",
                        status = HttpStatusCode.NotFound)
                }else call.respondText("No reviews found in book with id $bookID.", status = HttpStatusCode.OK)
            } else call.respondText("No books found.", status = HttpStatusCode.OK)

        }

    }
}

