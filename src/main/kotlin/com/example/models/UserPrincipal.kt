package com.example.models

import com.example.database.Database
import io.ktor.server.auth.*
import java.nio.charset.StandardCharsets.UTF_8
import java.security.MessageDigest

data class UserPrincipal(val userName: String, val realm: String) : Principal
fun getMd5Digest(str: String): ByteArray = MessageDigest.getInstance("MD5").digest(str.toByteArray(UTF_8))

val myRealm = "Elcolinaboesuncruceentreunnaboyunacol"
val db = Database()
var userTable = db.getAllUsers().associate { user ->
    user.userName to getMd5Digest("${user.userName}:$myRealm:${user.password}")
}

fun reGenerateUserTable() {
    userTable = db.getAllUsers().associate { user ->
        user.userName to getMd5Digest("${user.userName}:$myRealm:${user.password}")
    }
}


