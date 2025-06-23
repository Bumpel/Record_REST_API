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


fun Application.configureRouting() {
    /*install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respondText(text = "500: $cause", status = HttpStatusCode.InternalServerError)
        }
    }*/

    routing {
        get("/") {
            call.respondText("Hello World!")
        }

        staticResources("/static", "static")
    }
}
