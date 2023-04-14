package com.example.routes

import com.example.database.Database
import com.example.models.Review
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun Route.reviewRouting() {
    val db = Database()

    route("/users/{userid?}/reviews"){
        get {
            val userid = call.parameters["userid"]

            val listOfUserReviews = db.getAllReviewsOfUser(userid!!)
            if (listOfUserReviews.isNotEmpty()) {
                        return@get call.respond(listOfUserReviews)
            } else call.respondText("No books found.", status = HttpStatusCode.NotFound)
        }
    }

    route("/books/{bookid?}/reviews") {
        // GET
        get {
            val bookID = call.parameters["bookid"]

            val listOfBooksFromDB = db.getAllBooks()
            if (listOfBooksFromDB.isNotEmpty()) {
                if (listOfBooksFromDB.filter { it.idBook == bookID }.size == 1) {
                    //Miramos que no esté vacía la lista de reviews del libro
                    val bookReviews = db.getAllReviewsOfBook(bookID!!)
                    if (bookReviews.isNotEmpty()) {
                        return@get call.respond(bookReviews)
                    } else call.respondText("No reviews found in book $bookID.", status = HttpStatusCode.NotFound)
                } else call.respondText("Book with id $bookID not found.", status = HttpStatusCode.NotFound)
            } else call.respondText("No books found.", status = HttpStatusCode.NotFound)
        }

        // GET por ID de la review

        get("{id}") {
            if (call.parameters["id"].isNullOrBlank()) return@get call.respondText(
                "Missing book id", status = HttpStatusCode.BadRequest
            )
            val bookID = call.parameters["bookid"]
            val id = call.parameters["id"]

            val listOfBooksFromDB = db.getAllBooks()
            if (listOfBooksFromDB.isNotEmpty()) {
                if (listOfBooksFromDB.filter { it.idBook == bookID }.size == 1) {
                    //Miramos que no esté vacía la lista de reviews del libro
                    val bookReviews = db.getAllReviewsOfBook(bookID!!)
                    if (bookReviews.isNotEmpty()) {
                        val review = db.getBookReviewByID(bookID, id!!)
                        // Si el ID de la review es un string vacío significa que no hay review
                        if (review.idReview != "") {
                            return@get call.respond(review)
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
            // Filtramos lista de libros por ID de libro (Cambiar a llamar al libro?)
            val listOfBooksFromDB = db.getAllBooks()
            val listOfReviews = db.getAllReviewsOfBook(bookID!!)
            if (listOfBooksFromDB.filter { it.idBook == bookID }.size == 1) {
                if (listOfReviews.filter { it.idBook == reviewCall.idBook && it.idUser == reviewCall.idUser }.isEmpty()){
                    db.insertNewReview(reviewCall)
                    call.respondText("Review stored correctly.", status = HttpStatusCode.Created)
                    return@post call.respond(reviewCall)
                } else return@post call.respondText(
                    "User with id ${reviewCall.idUser} already reviewed this book, please update your review",
                    status = HttpStatusCode.OK
                )

            } else return@post call.respondText(
                "Book with id ${reviewCall.idBook} doesn't exists.",
                status = HttpStatusCode.OK
            )
        }

        // PUT
        put("{id}") {
            val bookID = call.parameters["bookid"]
            val reviewID = call.parameters["id"]

            if (reviewID.isNullOrBlank()) return@put call.respondText(
                "Missing review id.",
                status = HttpStatusCode.BadRequest
            )

            val reviewToUpdate = call.receive<Review>()
            if (db.getBookByID(bookID!!).idBook != "") {
                val review = db.getBookReviewByID(bookID, reviewID)
                if (review.idReview != "") {
                    db.updateReview(reviewID, reviewToUpdate)

                    return@put call.respondText(
                        "Review with id $reviewID has been updated.", status = HttpStatusCode.Accepted
                    )
                } else call.respondText(
                    "Review with id $reviewID not found in book $bookID.",
                    status = HttpStatusCode.NotFound
                )
            } else call.respondText("Book with id $bookID not found.", status = HttpStatusCode.NotFound)
        }


        // Delete

        delete("{id}") {
            val bookID = call.parameters["bookid"]
            val reviewID = call.parameters["id"]
            if (reviewID.isNullOrBlank()) return@delete call.respondText(
                "Missing review id.",
                status = HttpStatusCode.BadRequest
            )

            val review = db.getBookReviewByID(bookID!!, reviewID)
            if (review.idReview != "") {
                db.deleteReview(bookID, reviewID)
                call.respondText("Review with id $reviewID in book $bookID has been deleted.")
            } else call.respondText(
                "Review with id $reviewID from book with id $bookID not found.",
                status = HttpStatusCode.NotFound
            )

        }

    }
}

