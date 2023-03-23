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
    route("/books/{bookid}/reviews"){

        // GET

        get {
            val bookID = call.parameters["bookid"]
            for (book   in bookList){
                if (book.id == bookID?.toInt()){
                    if (reviewList.isNotEmpty()){
                        call.respond(book.comments)
                    }
                } else {
                    call.respondText("No reviews found in book $bookID", status = HttpStatusCode.OK)
                }
            }
        }

        get("{id}"){
            if (call.parameters["id"].isNullOrBlank()) return@get call.respondText(
                "Missing review id", status = HttpStatusCode.BadRequest
            )
            val  bookID = call.parameters["bookid"]
            val id = call.parameters["id"]
            for (book in bookList) {
                if (book.id == bookID?.toInt()){
                    for (comment in book.comments){
                        if (comment.id == id?.toInt()){
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
                if (book.id == bookID?.toInt()){
                    book.comments.add(review)
                }
            }

            call.respondText("Review stored correctly", status = HttpStatusCode.Created)


        }

        // PUT

        put("{id?}") {
            val bookID = call.parameters["bookid"]

            if (bookID.isNullOrBlank()) return@put call.respondText(
                "Missing book id",
                status = HttpStatusCode.BadRequest
            )
            val commentID = call.parameters["id"]
            val commentToUpdate = call.receive<Review>()
            for (book in bookList){
                if (book.id == bookID.toInt()){
                    for (comment in book.comments) {
                        if (comment.id == commentID?.toInt()) {
                            comment.id = commentToUpdate.id
                           // comment.idUser = commentToUpdate.idUser
                            comment.comment = commentToUpdate.comment
                            comment.date = commentToUpdate.date

                            return@put call.respondText(
                                "Comment with id $commentID from book with id ${book.id} has been updated",
                                status = HttpStatusCode.Accepted
                            )
                        }
                    }
                    call.respondText(
                        "Review with id $$commentID not found",
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
                if (book.id == bookID.toInt()) {
                    for (comment in book.comments){
                        if (comment.id == commentID?.toInt()){
                            book.comments.remove(comment)
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

