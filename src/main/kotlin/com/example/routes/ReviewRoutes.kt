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
        /*
            for (book in bookList){
                if (book.idBook == bookID?.toInt()){
                    if (book.reviews.isNotEmpty()){
                        call.respond(book.reviews)
                    }
                } else {
                    call.respondText("No reviews found in book $bookID", status = HttpStatusCode.OK)
                }
            }
             */

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
        /*
            for (book in bookList) {
                if (book.idBook == bookID!!.toInt()){
                    for (comment in book.reviews){
                        if (comment.idReview == id!!.toInt()){
                            return@get call.respond(comment)
                        }
                    }
                }
            }
            call.respondText(
                "Review with id $id not found on book with id $bookID",
                status = HttpStatusCode.NotFound
            )

             */


        // POST

        post {
            val bookID = call.parameters["bookid"]
            val review = call.receive<Review>()

            //if (!bookList[bookID]!!.reviews.containsKey(review.idReview)) {
                bookList[bookID]!!.reviews[review.idReview] = review
                call.respondText("Review stored correctly", status = HttpStatusCode.Created)
                return@post call.respond(review)
            //} else return@post call.respondText("Review with id ${review.idReview} already exists", status = HttpStatusCode.OK)
        }
        /*
            for (book in bookList){
                if ((book.idBook == bookID?.toInt())){
                    // Si ya existe la review con el mismo id, no se almacena
                    if (book.reviews.none { it.idReview == review.idReview }){
                        book.reviews.add(review)
                    } else {
                        return@post call.respondText(
                            "Review with id ${review.idReview} already exists", status = HttpStatusCode.OK)
                    }
                    call.respondText("Review stored correctly", status = HttpStatusCode.Created)
                }
            }
             */
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
        /*
            for (book in bookList){
                if (book.idBook == bookID.toInt()){
                    for (comment in book.reviews) {
                        if (comment.idReview == reviewID?.toInt()) {
                            comment.idBook = commentToUpdate.idBook
                            comment.idUser = commentToUpdate.idUser
                            comment.comment = commentToUpdate.comment
                            comment.date = commentToUpdate.date
                            comment.idBook = commentToUpdate.idBook

                            return@put call.respondText(
                                "Comment with id $reviewID from book with id ${book.idBook} has been updated",
                                status = HttpStatusCode.Accepted
                            )
                        }
                    }
                    call.respondText(
                        "Review with id $$reviewID not found",
                        status = HttpStatusCode.NotFound
                    )
                }
            }

             */


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
                        bookList[bookID]!!.reviews.remove(reviewID)
                        return@delete call.respondText(
                            "Review with id $reviewID removed from book with id $bookID",
                            status = HttpStatusCode.Accepted)
                    } else  call.respondText(
                        "Review with id $reviewID from book with id $bookID not found",
                        status = HttpStatusCode.NotFound)
                }else call.respondText("No reviews found in book with id $bookID.", status = HttpStatusCode.OK)
            } else call.respondText("No books found.", status = HttpStatusCode.OK)

        }
        /*
            for (book in bookList) {
                if (book.idBook == bookID.toInt()) {
                    for (comment in book.reviews){
                        if (comment.idReview == commentID?.toInt()){
                            book.reviews.remove(comment)
                            return@delete call.respondText("Comment with id $commentID removed from book with id $bookID",
                                status = HttpStatusCode.Accepted)
                        }
                    }

                }
            }
            call.respondText(
                "Review with id $commentID from book with id $bookID not found",
                status = HttpStatusCode.NotFound
            )

        }
             */

    }
}

