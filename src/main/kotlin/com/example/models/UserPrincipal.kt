package com.example.models

import com.example.database.Database
import io.ktor.server.auth.*
import java.nio.charset.StandardCharsets.UTF_8
import java.security.MessageDigest

data class UserPrincipal(val userName: String, val realm: String) : Principal

fun getMd5Digest(str: String): ByteArray = MessageDigest.getInstance("MD5").digest(str.toByteArray(UTF_8))

val myRealm = "El colinabo es un cruce entre un nabo y una col"
val db = Database()
val userTable = db.getAllUsers().associate { user ->
    user.userName to getMd5Digest("${user.userName}:$myRealm:${user.password}")
}
/*
val userTable: Map<String, ByteArray> = mapOf(
    "jetbrains" to getMd5Digest("jetbrains:$myRealm:foobar"),
    "admin" to getMd5Digest("admin:$myRealm:password")
)
 */

