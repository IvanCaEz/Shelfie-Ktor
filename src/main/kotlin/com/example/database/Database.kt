package com.example.database

import com.example.models.*
import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException

class Database {
    private var connection: Connection? = null
    fun connectToDB() {
        try {
            val user = "postgres"
            val password = "Program101010Ivan"
            val jdbcUrl = "jdbc:postgresql://localhost:5432/shelfie"
            if (connection == null) connection = DriverManager.getConnection(jdbcUrl, user, password)

        } catch (e: SQLException) {
            println("Error: " + e.errorCode + e.message)
        }


    }
    init {
        connectToDB()
    }


    // BOOKS
    fun getAllBooks(): List<Book> {
        val bookList = mutableListOf<Book>()

        try {
            val statement = connection!!.createStatement()
            val booksSelect = "SELECT * FROM books"
            val result = statement.executeQuery(booksSelect)

            while (result.next()) {
                val book = getBookFromResult(result)
                bookList.add(book)
            }
            statement.close()

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
            val statement = connection!!.createStatement()
            val bookSelect = "SELECT * FROM books WHERE id_book = $bookID"
            val result = statement.executeQuery(bookSelect)

            while (result.next()) {
                book = getBookFromResult(result)
            }
            statement.close()

        } catch (e: SQLException) {
            println("Error " + e.errorCode + ": " + e.message)
        }
        return book
    }

    fun updateBook(bookID: String, bookToUpdate: Book) {
        try {

            val bookSentence = "UPDATE books SET title=?, author=?, publication_year=?," +
                    "synopsis=?, book_cover=?, state=?, stock_total=?, stock_remaining=?, genre=?  WHERE id_book = $bookID"
            val preparedBook: PreparedStatement = connection!!.prepareStatement(bookSentence)
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


        } catch (e: SQLException) {
            println("Error " + e.errorCode + ": " + e.message)
        }
    }

    fun insertNewBook(newBook: Book) {
        try {

            val bookSentence = "INSERT INTO books VALUES (DEFAULT, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
            val preparedBook: PreparedStatement = connection!!.prepareStatement(bookSentence)
            //preparedBook.setInt(1, newBook.idBook.toInt())
            preparedBook.setString(1, newBook.title)
            preparedBook.setString(2, newBook.author)
            preparedBook.setString(3, newBook.publicationYear)
            preparedBook.setString(4, newBook.synopsis)
            preparedBook.setString(5, newBook.bookCover)
            preparedBook.setBoolean(6, newBook.state)
            preparedBook.setInt(7, newBook.stockTotal)
            preparedBook.setInt(8, newBook.stockRemaining)
            preparedBook.setString(9, newBook.genre)
            preparedBook.executeUpdate()
            preparedBook.close()


        } catch (e: SQLException) {
            println("Error " + e.errorCode + ": " + e.message)
        }
    }

    fun deleteBook(bookID: String) {
        try {
            val statement = connection!!.createStatement()
            val removeBook = "DELETE FROM books WHERE id_book = $bookID"
            statement.executeUpdate(removeBook)
            statement.close()

        } catch (e: SQLException) {
            println("Error " + e.errorCode + ": " + e.message)
        }
    }

    // USERS TODO - Put / Arreglar post de libros leidos


    fun getAllUsers(): List<User> {
        val userList = mutableListOf<User>()

        try {
            val statement = connection!!.createStatement()
            val userSelect = "SELECT * FROM users"
            val result = statement.executeQuery(userSelect)

            while (result.next()) {
                val history = result.getArray("book_history").array as Array<Int>
                val user = getUserFromResult(result, history)

                userList.add(user)
            }
            statement.close()

        } catch (e: SQLException) {
            println("Error " + e.errorCode + ": " + e.message)
        }
        return userList.toList()
    }

    fun getUserByID(userID: String): User {
        var user = User(
            "", "", "", "", "", "", UserType.NORMAL,
            0, setOf<Int>(), false, ""
        )
        try {

            val statement = connection!!.createStatement()
            val userSelect = "SELECT * FROM users WHERE id_user = $userID"
            val result = statement.executeQuery(userSelect)

            while (result.next()) {
                val history = result.getArray("book_history").array as Array<Int>
                 user = getUserFromResult(result, history)
            }
            statement.close()

        } catch (e: SQLException) {
            println("Error " + e.errorCode + ": " + e.message)
        }
        return user
    }

    fun getUserByUserName(userName: String): User {
        var user = User(
            "", "", "", "", "", "", UserType.NORMAL,
            0, setOf<Int>(), false, ""
        )
        try {

            val statement = connection!!.createStatement()
            val userSelect = "SELECT * FROM users WHERE user_name = '$userName'"
            val result = statement.executeQuery(userSelect)

            while (result.next()) {
                val history = result.getArray("book_history").array as Array<Int>
                user = getUserFromResult(result, history)
            }
            println(user.userName+user.name)
            statement.close()

        } catch (e: SQLException) {
            println("Error " + e.errorCode + ": " + e.message)
        }
        return user
    }

    fun updateUser(userID: String, userToUpdate: User) {
        //No updatea los libros leídos porque ya tenemos rutas y funciones específicas
        try {
            val userSentence = "UPDATE users SET name=?, email=?, password=?," +
                    "user_name=?, description=?, user_type=?, borrowed_books_counter=?," +
                    " banned=?, user_image=? WHERE id_user = $userID"
            val preparedUser: PreparedStatement = connection!!.prepareStatement(userSentence)
            preparedUser.setString(1, userToUpdate.name)
            preparedUser.setString(2, userToUpdate.email)
            preparedUser.setString(3, userToUpdate.password)
            preparedUser.setString(4, userToUpdate.userName)
            preparedUser.setString(5, userToUpdate.description)
            preparedUser.setString(6, userToUpdate.userType.toString())
            preparedUser.setInt(7, userToUpdate.borrowedBooksCounter)
            preparedUser.setBoolean(8, userToUpdate.banned)
            preparedUser.setString(9, userToUpdate.userImage)

            preparedUser.executeUpdate()
            preparedUser.close()


        } catch (e: SQLException) {
            println("Error " + e.errorCode + ": " + e.message)
        }
    }

    fun deleteUser(userID: String) {
        try {
            val statement = connection!!.createStatement()
            val removeUser = "DELETE FROM users WHERE id_user = $userID"
            statement.executeUpdate(removeUser)
            statement.close()

        } catch (e: SQLException) {
            println("Error " + e.errorCode + ": " + e.message)
        }
    }

    fun insertNewUser(newUser: User) {
        try {
            val userSentence = "INSERT INTO users VALUES (DEFAULT, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
            val preparedUser: PreparedStatement = connection!!.prepareStatement(userSentence)
            preparedUser.setString(1, newUser.name)
            preparedUser.setString(2, newUser.email)
            preparedUser.setString(3, newUser.password)
            preparedUser.setString(4, newUser.userName)
            preparedUser.setString(5, newUser.description)
            preparedUser.setString(
                6, when (newUser.userType) {
                    UserType.ADMIN -> "ADMIN"
                    else -> "NORMAL"
                }
            )
            preparedUser.setInt(7, newUser.borrowedBooksCounter)
            //Al crear usuario la lista de leídos está vacía
            val booksRead = connection!!.createArrayOf("INT", newUser.bookHistory.toTypedArray())
            preparedUser.setArray(8, booksRead)
            preparedUser.setBoolean(9, newUser.banned)
            preparedUser.setString(10, newUser.userImage)



            preparedUser.executeUpdate()
            preparedUser.close()


        } catch (e: SQLException) {
            println("Error " + e.errorCode + ": " + e.message)
        }
    }

    fun getUserLoans(userID: String): List<BookLoan> {
        val bookLoans = mutableListOf<BookLoan>()
        try {
            val statement = connection!!.createStatement()
            val loanSelect = "SELECT * FROM book_loans WHERE id_user = $userID"
            val result = statement.executeQuery(loanSelect)

            while (result.next()) {

                val bookLoan = getLoanFromResult(result)

                bookLoans.add(bookLoan)
            }
            statement.close()

        } catch (e: SQLException) {
            println("Error " + e.errorCode + ": " + e.message)
        }
        return bookLoans.toList()
    }

    fun getLoanByBookID(userID: String, bookID: String): BookLoan {
        var bookLoan = BookLoan("", "", "", "")
        try {

            val statement = connection!!.createStatement()
            val loanSelect = "SELECT * FROM book_loans WHERE id_user = $userID AND id_book = $bookID"
            val result = statement.executeQuery(loanSelect)

            while (result.next()) {
                bookLoan = getLoanFromResult(result)
            }
            statement.close()

        } catch (e: SQLException) {
            println("Error " + e.errorCode + ": " + e.message)
        }
        return bookLoan
    }

    fun addBookLoan(newBookLoan: BookLoan) {
        try {
            val loanSentence = "INSERT INTO book_loans VALUES (?, ?, ?, ?)"
            val preparedLoan: PreparedStatement = connection!!.prepareStatement(loanSentence)
            preparedLoan.setInt(1, newBookLoan.idUser.toInt())
            preparedLoan.setInt(2, newBookLoan.idBook.toInt())
            preparedLoan.setString(3, newBookLoan.startDate)
            preparedLoan.setString(4, newBookLoan.endDate)
            preparedLoan.executeUpdate()
            preparedLoan.close()


        } catch (e: SQLException) {
            println("Error " + e.errorCode + ": " + e.message)
        }
    }

    fun updateBookLoan(userID: String, bookLoanToUpdate: BookLoan) {
        try {
            val loanSentence = "UPDATE book_loans SET id_user=?, id_book=?, start_date=?, end_date=? WHERE" +
                    " id_user = $userID AND id_book = ${bookLoanToUpdate.idBook}"
            val preparedLoan: PreparedStatement = connection!!.prepareStatement(loanSentence)
            preparedLoan.setInt(1, bookLoanToUpdate.idUser.toInt())
            preparedLoan.setInt(2, bookLoanToUpdate.idBook.toInt())
            preparedLoan.setString(3, bookLoanToUpdate.startDate)
            preparedLoan.setString(4, bookLoanToUpdate.endDate)
            preparedLoan.executeUpdate()
            preparedLoan.close()

        } catch (e: SQLException) {
            println("Error " + e.errorCode + ": " + e.message)
        }

    }

    fun deleteBookLoan(userID: String, bookID: String) {
        try {
            val statement = connection!!.createStatement()
            val removeLoan = "DELETE FROM book_loans WHERE id_user = $userID AND id_book = $bookID"
            statement.executeUpdate(removeLoan)
            statement.close()

        } catch (e: SQLException) {
            println("Error " + e.errorCode + ": " + e.message)
        }
    }


    fun addBookRead(userID: String, bookID: String) {
        try {

            val statement = connection!!.createStatement()
            val updateArray =
                "UPDATE users SET book_history = array_append(book_history, $bookID) WHERE id_user = $userID"
            statement.executeUpdate(updateArray)
            statement.close()

        } catch (e: SQLException) {
            println("Error " + e.errorCode + ": " + e.message)
        }
    }

    fun deleteBookRead(userID: String, bookID: String) {
        try {
            val statement = connection!!.createStatement()
            val removeElement =
                "UPDATE users SET book_history = array_remove(book_history, $bookID) WHERE id_user = $userID"
            statement.executeUpdate(removeElement)
            statement.close()

        } catch (e: SQLException) {
            println("Error " + e.errorCode + ": " + e.message)
        }
    }


    fun getBookHistoryFromUser(userID: String): Set<Int> {
        var history = setOf<Int>()
        try {

            val statement = connection!!.createStatement()
            val userSelect = "SELECT book_history FROM users WHERE id_user = $userID"
            val result = statement.executeQuery(userSelect)

            while (result.next()) {
                history = (result.getArray("book_history").array as Array<Int>)
                    .map { it }.toSet()
            }
            statement.close()

        } catch (e: SQLException) {
            println("Error " + e.errorCode + ": " + e.message)
        }
        return history
    }

    fun getFavGenresFromUser(userID: String): Set<String> {
        var favGenres = setOf<String>()
        try {

            val statement = connection!!.createStatement()
            val userSelect = "SELECT favorite_genres FROM users WHERE id_user = $userID"
            val result = statement.executeQuery(userSelect)

            while (result.next()) {
                favGenres = (result.getArray("favorite_genres").array as Array<String>)
                    .map { it }.toSet()
            }
            statement.close()

        } catch (e: SQLException) {
            println("Error " + e.errorCode + ": " + e.message)
        }
        return favGenres
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
            result.getString("user_name"),
            result.getString("description"),
            when (result.getString("user_type")) {
                "ADMIN" -> UserType.ADMIN
                else -> UserType.NORMAL
            },
            result.getInt("borrowed_books_counter"),
            history.map { it }.toSet(),
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

    /**
     * Esta función transforma el resultado de la query de la base de datos en un préstamo.
     * @param result El resultado de la query con toda la info del préstamo
     */
    private fun getLoanFromResult(result: ResultSet): BookLoan {
        return BookLoan(
            result.getString("id_user"),
            result.getString("id_book"),
            result.getString("start_date"),
            result.getString("end_date")
        )
    }

    // REVIEWS
    fun getAllReviewsOfUser(userID: String): List<Review> {
        val reviewList = mutableListOf<Review>()

        try {
            val statement = connection!!.createStatement()
            val reviewSelect = "SELECT * FROM reviews WHERE id_user = $userID"
            val result = statement.executeQuery(reviewSelect)

            while (result.next()) {
                val review = getReviewFromResult(result)
                reviewList.add(review)
            }
            statement.close()

        } catch (e: SQLException) {
            println("Error " + e.errorCode + ": " + e.message)
        }
        return reviewList.toList()
    }
    fun getAllReviewsOfBook(bookID: String): List<Review> {
        val reviewList = mutableListOf<Review>()

        try {
            val statement = connection!!.createStatement()
            val reviewSelect = "SELECT * FROM reviews WHERE id_book = $bookID"
            val result = statement.executeQuery(reviewSelect)

            while (result.next()) {
                val review = getReviewFromResult(result)
                reviewList.add(review)
            }
            statement.close()

        } catch (e: SQLException) {
            println("Error " + e.errorCode + ": " + e.message)
        }
        return reviewList.toList()
    }

    fun getBookReviewByID(bookID: String, reviewID: String): Review {
        var review = Review("", "", "", "", "", 0)
        try {
            val statement = connection!!.createStatement()
            val reviewSelect = "SELECT * FROM reviews WHERE id_book = $bookID AND id_review = $reviewID"
            val result = statement.executeQuery(reviewSelect)

            while (result.next()) {
                review = getReviewFromResult(result)
            }
            statement.close()

        } catch (e: SQLException) {
            println("Error " + e.errorCode + ": " + e.message)
        }
        return review
    }

    fun deleteReview(bookID: String, reviewID: String) {
        try {
            val statement = connection!!.createStatement()
            val removeReview = "DELETE FROM reviews WHERE id_book = $bookID AND id_review = $reviewID"
            statement.executeUpdate(removeReview)
            statement.close()

        } catch (e: SQLException) {
            println("Error " + e.errorCode + ": " + e.message)
        }
    }

    fun deleteReviewsFromUser(userID: String){
        try {
            val statement = connection!!.createStatement()
            val removeReview = "DELETE FROM reviews WHERE id_user = $userID"
            statement.executeUpdate(removeReview)
            statement.close()

        } catch (e: SQLException) {
            println("Error " + e.errorCode + ": " + e.message)
        }
    }

    fun insertNewReview(newReview: Review) {
        try {
            val reviewSentence = "INSERT INTO reviews VALUES (DEFAULT, ?, ?, ?, ?, ?)"
            val preparedReview: PreparedStatement = connection!!.prepareStatement(reviewSentence)
            //preparedReview.setInt(1, newReview.idReview.toInt())
            preparedReview.setInt(1, newReview.idBook.toInt())
            preparedReview.setInt(2, newReview.idUser.toInt())
            preparedReview.setString(3, newReview.date)
            preparedReview.setString(4, newReview.comment)
            preparedReview.setInt(5, newReview.rating)
            preparedReview.executeUpdate()
            preparedReview.close()

        } catch (e: SQLException) {
            println("Error " + e.errorCode + ": " + e.message)
        }
    }

    fun updateReview(reviewID: String, reviewToUpdate: Review) {
        //Sólo updatea la fecha, el comentario y la puntuación
        try {

            val reviewSentence =
                "UPDATE reviews SET date=?, review=?, rating=? WHERE id_review = $reviewID " +
                        "AND id_book = ${reviewToUpdate.idBook} AND id_user = ${reviewToUpdate.idUser}"
            val preparedReview: PreparedStatement = connection!!.prepareStatement(reviewSentence)
            preparedReview.setString(1, reviewToUpdate.date)
            preparedReview.setString(2, reviewToUpdate.comment)
            preparedReview.setInt(3, reviewToUpdate.rating)

            preparedReview.executeUpdate()
            preparedReview.close()


        } catch (e: SQLException) {
            println("Error " + e.errorCode + ": " + e.message)
        }
    }

}