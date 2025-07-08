package com.RecordAPI.adapter

import com.RecordAPI.application.RecordService
import com.RecordAPI.domain.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

fun Route.recordRoutes(recordService: RecordService) {
    route("/") {
        get {
            call.respondText("Hello World!")
        }
    }
    route("/records") {
        get("{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("Ungültige ID"))
                return@get
            }
            val record = recordService.read(id)
            if (record != null) {
                call.respond(HttpStatusCode.OK, record)
            } else {
                call.respond(HttpStatusCode.NotFound, ErrorResponse("Record nicht gefunden"))
            }
            call.respondText("Hello World!")
        }
        put("{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("Ungültige ID"))
                return@put
            }
            val updated = try {
                call.receive<DBRecordUpload>()
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("Ungültiger Body: ${e.localizedMessage}"))
                return@put
            }
            val existingRecord = recordService.read(id)
            if (existingRecord == null) {
                call.respond(HttpStatusCode.NotFound, ErrorResponse("Record nicht gefunden"))
                return@put
            }
            if (existingRecord.owner != updated.owner) {
                call.respond(HttpStatusCode.Forbidden, ErrorResponse("Nur der Owner darf den Record aktualisieren"))
                return@put
            }
            val record = recordService.update(id, updated)
            if (record != null) {
                call.respond(HttpStatusCode.OK, record)
            } else {
                call.respond(HttpStatusCode.InternalServerError, ErrorResponse("Fehler beim Aktualisieren"))
            }
        }
        delete("{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            val owner = call.request.queryParameters["owner"]
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("Ungültige ID"))
                return@delete
            }
            if (owner == null) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("Owner-Parameter fehlt"))
                return@delete
            }
            val existingRecord = recordService.read(id)
            if (existingRecord == null) {
                call.respond(HttpStatusCode.NotFound, ErrorResponse("Record nicht gefunden"))
                return@delete
            }
            if (existingRecord.owner != owner) {
                call.respond(HttpStatusCode.Forbidden, ErrorResponse("Nur der Owner darf den Record löschen"))
                return@delete
            }
            val success = recordService.delete(id, owner)
            if (success) {
                call.respond(HttpStatusCode.NoContent)
            } else {
                call.respond(HttpStatusCode.InternalServerError, ErrorResponse("Fehler beim Löschen"))
            }
        }
        get {
            val records = recordService.readAll()
            call.respond(HttpStatusCode.OK, records)
        }
        post {
            val recordUpload = try {
                call.receive<DBRecordUpload>()
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("Ungültiger Body: ${e.localizedMessage}"))
                return@post
            }
            val record = recordService.create(recordUpload)
            call.respond(HttpStatusCode.Created, record)
        }
    }
}