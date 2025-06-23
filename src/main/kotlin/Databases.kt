package com.RecordAPI

import io.ktor.http.*
import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.*
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule

fun Application.configureDatabases() {
    val database = Database.connect(
        url = "jdbc:sqlite:${System.getProperty("user.home")}/Desktop/RecordDB.sqlite",
        driver = "org.sqlite.JDBC"
    )

    val recordService = RecordService(database)

    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
            registerModule(JavaTimeModule())
            disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        }
    }

    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respond(HttpStatusCode.InternalServerError, "Serverfehler: ${cause.localizedMessage}")
        }
    }

    routing {
        // Get All Records
        get("/records") {
            val records = recordService.readAll()
            call.respond(HttpStatusCode.OK, records)
        }

        // Create Record
        post("/records") {
            val recordUpload = call.receive<DBRecordUpload>()
            val record = recordService.create(recordUpload)
            call.respond(HttpStatusCode.Created, record)
        }

        // Read Record
        get("/records/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Ungültige ID")
                return@get
            }

            val record = recordService.read(id)
            if (record != null) {
                call.respond(HttpStatusCode.OK, record)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        // Update Record
        put("/records/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Ungültige ID")
                return@put
            }

            val updated = call.receive<DBRecordUpload>()
            val record = recordService.update(id, updated)

            if (record != null) {
                call.respond(HttpStatusCode.OK, record)
            } else {
                call.respond(HttpStatusCode.NotFound, "Record nicht gefunden oder Owner stimmt nicht überein")
            }
        }

        // Delete Record (requires owner query parameter)
        delete("/records/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            val owner = call.request.queryParameters["owner"]

            if (id == null || owner == null) {
                call.respond(HttpStatusCode.BadRequest, "ID oder Owner fehlen")
                return@delete
            }

            val success = recordService.delete(id, owner)
            if (success) {
                call.respond(HttpStatusCode.NoContent)
            } else {
                call.respond(HttpStatusCode.NotFound, "Record nicht gefunden oder Owner stimmt nicht überein")
            }
        }
    }
}
