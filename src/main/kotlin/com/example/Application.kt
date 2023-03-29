package com.example

import com.example.plugins.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.cors.routing.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

/**
 * Cosas que hacer
 *  - Subir imágenes
 *  - Pasar de JSON a SQL ??
 *
 *  Android
 *  - Arreglar IDs (Generar automáticamente la siguiente ID según el tamaño de la lista(mapa))
 *          - val nextUserID = (userList.size+1).toString() <- Ejemplo
 *          - Cuando el usuario se registra en la app, calcular el tamaño de la lista y sumarle 1,
 *            entonces pasamos ese número como ID
 *                  - Lo mismo al calcular las IDs de las reviews y libros
 *
 * Cosas que pensar
 *  - User: almacenar libro entero o solo ID del libro?
 *  - Cómo vincular una review al libro y al usuario a la misma vez?
 *  ---------------------------No prioritario------------------------------------
 * - Libro: Añadir puntuación?
 *             - Hacer que en la review sea obligatorio añadir una puntuación
 *             - Entonces calculamos la puntuación media de todas las reviews de todos los usuarios
 *
 */

fun Application.module() {
    configureSerialization()
    configureRouting()
    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Patch)
        allowHeader(HttpHeaders.Authorization)
        allowHeader("Shelfie")
        anyHost() // @TODO: Don't do this in production if possible. Try to limit it.
    }

}
