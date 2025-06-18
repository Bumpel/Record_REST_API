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
import io.ktor.server.request.receiveText

val mapper: ObjectMapper = jacksonObjectMapper()
data class Album(
    val id: Int,
    val owner: String,
    val title: String,
    val artist: String,
    val year: Int
)

data class AlbumUpload(
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
        get("/album/{id}") {
            val idParam = call.parameters["id"]
            val id = idParam?.toIntOrNull()

            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Ungültige ID")
                return@get
            }

            val album = Album(
                id = id,
                owner = "example_owner",
                title = "Example Title",
                artist = "Example Artist",
                year = 2020
            )
            val json = mapper.writeValueAsString(album)
            call.respond(json)
        }
        post("/album") {
            val body = call.receiveText()
            val albumUpload: AlbumUpload = mapper.readValue(body)

            // Ausgabe im Server-Log
            println("Received album upload: $albumUpload")

            call.respond(HttpStatusCode.OK, "Album received")
        }

        staticResources("/static", "static")
    }
}
