package com.example.routes

import com.example.models.Review
import com.example.models.bookList
import com.example.models.reviewList
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.reviewRouting(){
    route("/books/{bookid?}/reviews"){

        // GET

        get {
            val bookID = call.parameters["bookid"]
            for (book in bookList){
                if (book.idBook == bookID?.toInt()){
                    if (book.reviews.isNotEmpty()){
                        call.respond(book.reviews)
                    }
                } else {
                    call.respondText("No reviews found in book $bookID", status = HttpStatusCode.OK)
                }
            }
        }

        //

        get("{id}"){
            if (call.parameters["id"].isNullOrBlank()) return@get call.respondText(
                "Missing book id", status = HttpStatusCode.BadRequest
            )
            val bookID = call.parameters["bookid"]
            val id = call.parameters["id"]
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
        }

        // POST

        post {
            val bookID = call.parameters["bookid"]
            val review = call.receive<Review>()
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

        }
        // Delete

        delete("{id}"){
            val bookID = call.parameters["bookid"]
            if (bookID.isNullOrBlank()) return@delete call.respondText(
                "Missing book id",
                status = HttpStatusCode.BadRequest
            )
            val commentID = call.parameters["id"]
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
    }


}

