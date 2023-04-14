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
 *  AVISO: Las imágenes tardan en aparecer en el intelliJ pero sí que se suben
 * Cosas que hacer
 *  - Arreglar PUT de usuarios y libros (la imagen da problemas)
 *
 *  Android
 *          - Tener una imagen placeholder en el android y usarla como foto de perfil del usuario (crear usuario
 *          con ese campo vacío) y solo cambiarlo cuando el usuario quiera en su perfil o obligarle a elegirla
 *          al principio
 *          - Al principio la lista de libros leídos es 0 así que pasamos lista vacía
 *
 *
 * Cosas que pensar
 *  ---------------------------No prioritario------------------------------------
 *  Android
 * - Libro: Añadir puntuación?
 *             - Hacer que en la review sea obligatorio añadir una puntuación (del 1 al 5 "estrellitas")
 *             - Entonces calculamos la puntuación media de todas las reviews de todos los usuarios (HECHO)
 *             - Debería actualizarse siempre que se añada una review a ese libro
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
