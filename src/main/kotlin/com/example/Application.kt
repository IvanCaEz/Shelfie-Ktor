package com.example

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.example.plugins.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

/**
 * Cosas que hacer
 *  - Subir imágenes
 *  - Pasar de JSON a SQL ??
 *
 * Cosas que pensar
 *  - User: almacenar libro entero o solo ID del libro?
 *  - Cómo vincular una review al libro y al usuario a la misma vez?
 *
 */

fun Application.module() {
    configureSerialization()
    configureRouting()
}
