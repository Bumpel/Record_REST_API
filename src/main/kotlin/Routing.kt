package com.RecordAPI

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.http.content.*
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue

val mapper: ObjectMapper = jacksonObjectMapper()
data class Album(
    val id: Int,
    val owner: String,
    val title: String,
    val artist: String,
    val year: Int
)

//private const val BASE_URL = "http://192.168.179.3:8100"

fun Application.configureRouting() {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respondText(text = "500: $cause", status = HttpStatusCode.InternalServerError)
        }
    }

    routing {
        get("/") {
            call.respondText("Hello World!")
        }

        get("/album") {
            val newAlbum = Album(
                id = 1,
                owner = "alice",
                title = "Dark Side of the Moon",
                artist = "Pink Floyd",
                year = 1973
            )
            val json = mapper.writeValueAsString(newAlbum)

            call.respond(json)
        }

        staticResources("/static", "static")
    }
}
