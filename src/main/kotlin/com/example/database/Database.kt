package com.example.database

import com.example.models.*
import io.ktor.util.reflect.*
import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import kotlin.reflect.jvm.internal.impl.load.java.structure.JavaArrayType

class Database {
    fun connectToDB(): Connection {
        lateinit var connection: Connection
        try {
            val user = "postgres"
            val password = "Program101010Ivan"
            val jdbcUrl = "jdbc:postgresql://localhost:5432/shelfie"
            connection = DriverManager.getConnection(jdbcUrl, user, password)

            println("Connection to the database: ${connection.isValid(0)}")

        } catch (e: SQLException) {
            println("Error: " + e.errorCode + e.message)
        }
        return connection

    }


    // BOOKS
    fun getAllBooks(): List<Book> {
        val bookList = mutableListOf<Book>()

        try {
            val connection = connectToDB()
            val statement = connection.createStatement()
            val booksSelect = "SELECT * FROM books"
            val result = statement.executeQuery(booksSelect)

            while (result.next()) {
                val book = getBookFromResult(result)
                bookList.add(book)
            }
            statement.close()
            connection.close()
            println("Disconnected from database")

        } catch (e: SQLException) {
            println("Error " + e.errorCode + ": " + e.message)
        }
        return bookList.toList()
    }


    fun getBookByID(bookID: String): Book {
        var book = Book(
            "", "", "", "", "", "", true,
            0, 0, ""
        )
        try {
            val connection = connectToDB()
            val statement = connection.createStatement()
            val bookSelect = "SELECT * FROM books WHERE id_book = $bookID"
            val result = statement.executeQuery(bookSelect)

            while (result.next()) {
                book = getBookFromResult(result)
            }
            statement.close()
            connection.close()
            println("Disconnected from database")

        } catch (e: SQLException) {
            println("Error " + e.errorCode + ": " + e.message)
        }
        return book
    }

    fun updateBook(bookID: String, bookToUpdate: Book) {
        try {
            val connection = connectToDB()

            val bookSentence = "UPDATE books SET title=?, author=?, publication_year=?," +
                    "synopsis=?, book_cover=?, state=?, stock_total=?, stock_remaining=?, genre=?  WHERE id_book = $bookID"
            val preparedBook: PreparedStatement = connection.prepareStatement(bookSentence)
            preparedBook.setString(1, bookToUpdate.title)
            preparedBook.setString(2, bookToUpdate.author)
            preparedBook.setString(3, bookToUpdate.publicationYear)
            preparedBook.setString(4, bookToUpdate.synopsis)
            preparedBook.setString(5, bookToUpdate.bookCover)
            preparedBook.setBoolean(6, bookToUpdate.state)
            preparedBook.setInt(7, bookToUpdate.stockTotal)
            preparedBook.setInt(8, bookToUpdate.stockRemaining)
            preparedBook.setString(9, bookToUpdate.genre)
            preparedBook.executeUpdate()
            preparedBook.close()

            connection.close()
            println("Disconnected from database")

        } catch (e: SQLException) {
            println("Error " + e.errorCode + ": " + e.message)
        }
    }

    fun insertNewBook(newBook: Book) {
        try {
            val connection = connectToDB()

            val bookSentence = "INSERT INTO books VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
            val preparedBook: PreparedStatement = connection.prepareStatement(bookSentence)
            preparedBook.setInt(1, newBook.idBook.toInt())
            preparedBook.setString(2, newBook.title)
            preparedBook.setString(3, newBook.author)
            preparedBook.setString(4, newBook.publicationYear)
            preparedBook.setString(5, newBook.synopsis)
            preparedBook.setString(6, newBook.bookCover)
            preparedBook.setBoolean(7, newBook.state)
            preparedBook.setInt(8, newBook.stockTotal)
            preparedBook.setInt(9, newBook.stockRemaining)
            preparedBook.setString(10, newBook.genre)
            preparedBook.executeUpdate()
            preparedBook.close()

            connection.close()
            println("Disconnected from database")

        } catch (e: SQLException) {
            println("Error " + e.errorCode + ": " + e.message)
        }
    }

    fun deleteBook(bookID: String) {
        try {
            val connection = connectToDB()
            val statement = connection.createStatement()
            val removeBook = "DELETE FROM books WHERE id_book = $bookID"
            statement.executeUpdate(removeBook)
            statement.close()
            connection.close()
            println("Disconnected from database")

        } catch (e: SQLException) {
            println("Error " + e.errorCode + ": " + e.message)
        }
    }

    // USERS TODO - Put / Arreglar post de libros leidos


    fun getAllUsers(): List<User> {
        val userList = mutableListOf<User>()

        try {
            val connection = connectToDB()
            val statement = connection.createStatement()
            val userSelect = "SELECT * FROM users"
            val result = statement.executeQuery(userSelect)

            while (result.next()) {
                val history = result.getArray("book_history").array as Array<Int>
                println(history.size)

                val user = getUserFromResult(result, history)

                userList.add(user)
            }
            statement.close()
            connection.close()
            println("Disconnected from database")

        } catch (e: SQLException) {
            println("Error " + e.errorCode + ": " + e.message)
        }
        return userList.toList()
    }

    fun getUserByID(userID: String): User {
        var user = User(
            "", "", "", "", UserType.NORMAL,
            0, mutableSetOf<Int>(), false, ""
        )
        try {
            val connection = connectToDB()

            val statement = connection.createStatement()
            val userSelect = "SELECT * FROM users WHERE id_user = $userID"
            val result = statement.executeQuery(userSelect)

            while (result.next()) {
                val history = result.getArray("book_history").array as Array<Int>
                user = getUserFromResult(result, history)
            }
            statement.close()
            connection.close()
            println("Disconnected from database")

        } catch (e: SQLException) {
            println("Error " + e.errorCode + ": " + e.message)
        }
        return user
    }

    fun updateUser(userID: String, userToUpdate: User) {
        //No updatea los libros leídos porque ya tenemos rutas y funciones específicas
        try {
            val connection = connectToDB()

            val userSentence = "UPDATE users SET name=?, email=?, password=?," +
                    "user_type=?, borrowed_books_counter=?, banned=?, user_image=? WHERE id_user = $userID"
            val preparedUser: PreparedStatement = connection.prepareStatement(userSentence)
            preparedUser.setString(1, userToUpdate.name)
            preparedUser.setString(2, userToUpdate.email)
            preparedUser.setString(3, userToUpdate.password)
            preparedUser.setString(4, userToUpdate.userType.toString())
            preparedUser.setInt(5, userToUpdate.borrowedBooksCounter)
            preparedUser.setBoolean(6, userToUpdate.banned)
            preparedUser.setString(7, userToUpdate.userImage)

            preparedUser.executeUpdate()
            preparedUser.close()

            connection.close()
            println("Disconnected from database")

        } catch (e: SQLException) {
            println("Error " + e.errorCode + ": " + e.message)
        }
    }

    fun deleteUser(userID: String) {
        try {
            val connection = connectToDB()
            val statement = connection.createStatement()
            val removeUser = "DELETE FROM users WHERE id_user = $userID"
            statement.executeUpdate(removeUser)
            statement.close()
            connection.close()
            println("Disconnected from database")

        } catch (e: SQLException) {
            println("Error " + e.errorCode + ": " + e.message)
        }
    }

    fun insertNewUser(newUser: User) {
        try {
            val connection = connectToDB()
            val userSentence = "INSERT INTO users VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)"
            val preparedUser: PreparedStatement = connection.prepareStatement(userSentence)
            preparedUser.setInt(1, newUser.idUser.toInt())
            preparedUser.setString(2, newUser.name)
            preparedUser.setString(3, newUser.email)
            preparedUser.setString(4, newUser.password)
            preparedUser.setString(
                5, when (newUser.userType) {
                    UserType.ADMIN -> "ADMIN"
                    else -> "NORMAL"
                }
            )
            preparedUser.setInt(6, newUser.borrowedBooksCounter)
            //Al crear usuario la lista de leídos está vacía
            val booksRead = connection.createArrayOf("INT", newUser.bookHistory.toTypedArray())
            preparedUser.setArray(7, booksRead)
            preparedUser.setBoolean(8, newUser.banned)
            preparedUser.setString(9, newUser.userImage)
            preparedUser.executeUpdate()
            preparedUser.close()

            connection.close()
            println("Disconnected from database")

        } catch (e: SQLException) {
            println("Error " + e.errorCode + ": " + e.message)
        }
    }

    fun addBookRead(userID: String, bookID: String) {
        try {
            val connection = connectToDB()

            val statement = connection.createStatement()
            val updateArray =
                "UPDATE users SET book_history = array_append(book_history, $bookID) WHERE id_user = $userID"
            statement.executeUpdate(updateArray)
            statement.close()
            connection.close()
            println("Disconnected from database")

        } catch (e: SQLException) {
            println("Error " + e.errorCode + ": " + e.message)
        }
    }

    fun deleteBookRead(userID: String, bookID: String) {
        try {
            val connection = connectToDB()
            val statement = connection.createStatement()
            val removeElement =
                "UPDATE users SET book_history = array_remove(book_history, $bookID) WHERE id_user = $userID"
            statement.executeUpdate(removeElement)
            statement.close()
            connection.close()
            println("Disconnected from database")

        } catch (e: SQLException) {
            println("Error " + e.errorCode + ": " + e.message)
        }
    }

    fun getBookHistoryFromUser(userID: String): MutableSet<Int> {
        var history = mutableSetOf<Int>()
        try {
            val connection = connectToDB()

            val statement = connection.createStatement()
            val userSelect = "SELECT book_history FROM users WHERE id_user = $userID"
            val result = statement.executeQuery(userSelect)

            while (result.next()) {
                history = (result.getArray("book_history").array as Array<Int>)
                    .map { it }.toMutableSet()
            }
            statement.close()
            connection.close()
            println("Disconnected from database")

        } catch (e: SQLException) {
            println("Error " + e.errorCode + ": " + e.message)
        }
        return history
    }

    /**
     * Esta función transforma el resultado de la query de la base de datos en un libro.
     * @param result El resultado de la query con toda la info del libro.
     */
    private fun getBookFromResult(result: ResultSet): Book {
        return Book(
            result.getString("id_book"),
            result.getString("title"),
            result.getString("author"),
            result.getString("publication_year"),
            result.getString("synopsis"),
            result.getString("book_cover"),
            result.getBoolean("state"),
            result.getInt("stock_total"),
            result.getInt("stock_remaining"),
            result.getString("genre")
        )
    }

    /**
     * Esta función transforma el resultado de la query de la base de datos en un usuario.
     * @param result El resultado de la query con toda la info del usuario
     * @param history los libros leídos del usuario en formato Array<Int> que transformaremos en un set de Int
     */
    private fun getUserFromResult(result: ResultSet, history: Array<Int>): User {
        return User(
            result.getString("id_user"),
            result.getString("name"),
            result.getString("email"),
            result.getString("password"),
            when (result.getString("user_type")) {
                "ADMIN" -> UserType.ADMIN
                else -> UserType.NORMAL
            },
            result.getInt("borrowed_books_counter"),
            history.map { it }.toMutableSet(),
            result.getBoolean("banned"),
            result.getString("user_image")
        )
    }

    /**
     * Esta función transforma el resultado de la query de la base de datos en una review.
     * @param result El resultado de la query con toda la info de la review
     */
    private fun getReviewFromResult(result: ResultSet): Review {
        return Review(
            result.getString("id_review"),
            result.getString("id_book"),
            result.getString("id_user"),
            result.getString("date"),
            result.getString("review"),
            result.getInt("rating")
        )
    }

    // REVIEWS
    fun getAllReviewsOfBook(bookID: String): List<Review> {
        val reviewList = mutableListOf<Review>()

        try {
            val connection = connectToDB()
            val statement = connection.createStatement()
            val reviewSelect = "SELECT * FROM reviews WHERE id_book = $bookID"
            val result = statement.executeQuery(reviewSelect)

            while (result.next()) {
                val review = getReviewFromResult(result)
                reviewList.add(review)
            }
            statement.close()
            connection.close()
            println("Disconnected from database")

        } catch (e: SQLException) {
            println("Error " + e.errorCode + ": " + e.message)
        }
        return reviewList.toList()
    }

    fun getBookReviewByID(bookID: String, reviewID: String): Review {
        var review = Review("", "", "", "", "", 0)
        try {
            val connection = connectToDB()
            val statement = connection.createStatement()
            val reviewSelect = "SELECT * FROM reviews WHERE id_book = $bookID AND id_review = $reviewID"
            val result = statement.executeQuery(reviewSelect)

            while (result.next()) {
                review = getReviewFromResult(result)
            }
            statement.close()
            connection.close()
            println("Disconnected from database")

        } catch (e: SQLException) {
            println("Error " + e.errorCode + ": " + e.message)
        }
        return review
    }

    fun deleteReview(bookID: String, reviewID: String) {
        try {
            val connection = connectToDB()
            val statement = connection.createStatement()
            val removeReview = "DELETE FROM reviews WHERE id_book = $bookID AND id_review = $reviewID"
            statement.executeUpdate(removeReview)
            statement.close()
            connection.close()
            println("Disconnected from database")

        } catch (e: SQLException) {
            println("Error " + e.errorCode + ": " + e.message)
        }
    }

    fun deleteReviewsFromUser(userID: String){
        try {
            val connection = connectToDB()
            val statement = connection.createStatement()
            val removeReview = "DELETE FROM reviews WHERE id_user = $userID"
            statement.executeUpdate(removeReview)
            statement.close()
            connection.close()
            println("Disconnected from database")

        } catch (e: SQLException) {
            println("Error " + e.errorCode + ": " + e.message)
        }
    }

    fun insertNewReview(newReview: Review) {
        try {
            val connection = connectToDB()
            val reviewSentence = "INSERT INTO reviews VALUES (?, ?, ?, ?, ?, ?)"
            val preparedReview: PreparedStatement = connection.prepareStatement(reviewSentence)
            preparedReview.setInt(1, newReview.idReview.toInt())
            preparedReview.setInt(2, newReview.idBook.toInt())
            preparedReview.setInt(3, newReview.idUser.toInt())
            preparedReview.setString(4, newReview.date)
            preparedReview.setString(5, newReview.comment)
            preparedReview.setInt(6, newReview.rating)
            preparedReview.executeUpdate()
            preparedReview.close()
            connection.close()
            println("Disconnected from database")

        } catch (e: SQLException) {
            println("Error " + e.errorCode + ": " + e.message)
        }
    }

    fun updateReview(reviewID: String, reviewToUpdate: Review) {
        //Sólo updatea la fecha, el comentario y la puntuación
        try {
            val connection = connectToDB()

            val reviewSentence =
                "UPDATE reviews SET date=?, review=?, rating=? WHERE id_review = $reviewID " +
                        "AND id_book = ${reviewToUpdate.idBook} AND id_user = ${reviewToUpdate.idUser}"
            val preparedReview: PreparedStatement = connection.prepareStatement(reviewSentence)
            preparedReview.setString(1, reviewToUpdate.date)
            preparedReview.setString(2, reviewToUpdate.comment)
            preparedReview.setInt(3, reviewToUpdate.rating)

            preparedReview.executeUpdate()
            preparedReview.close()

            connection.close()
            println("Disconnected from database")

        } catch (e: SQLException) {
            println("Error " + e.errorCode + ": " + e.message)
        }
    }

}