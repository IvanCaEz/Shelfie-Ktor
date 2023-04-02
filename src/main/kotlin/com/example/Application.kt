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
 *  - Pasar de JSON a SQL ??
 *
 *  Android
 *  - Desde la app se pasan ya todos los datos tratados (arreglar los posts y eso):
 *      - Arreglar IDs (Generar automáticamente la siguiente ID según el tamaño de la lista(mapa))
 *          - val nextUserID = (userList.size+1).toString() <- Ejemplo
 *          - Cuando el usuario se registra en la app, calcular el tamaño de la lista y sumarle 1,
 *            entonces pasamos ese número como ID
 *                  - Lo mismo al calcular las IDs de las reviews y libros
 *          - Tener una imagen placeholder en el android y usarla como foto de perfil del usuario (crear usuario
 *          con ese campo vacío) y solo cambiarlo cuando el usuario quiera en su perfil o obligarle a elegirla
 *          al principio
 *          - Al principio la lista de libros leídos es 0 así que pasamos lista vacía
 *
 *
 * Cosas que pensar
 *  - Cómo vincular una review al libro y al usuario a la misma vez?
 *  ---------------------------No prioritario------------------------------------
 * - Libro: Añadir puntuación?
 *             - Hacer que en la review sea obligatorio añadir una puntuación (del 1 al 5 "estrellitas")
 *             - Entonces calculamos la puntuación media de todas las reviews de todos los usuarios
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
