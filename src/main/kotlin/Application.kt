package com.RecordAPI

import com.RecordAPI.adapter.*
import com.RecordAPI.application.RecordService
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.serialization.jackson.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.Database
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.RecordAPI.domain.ErrorResponse
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.request.*
import org.slf4j.event.Level

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    // DB Connection & Adapter
    val database = Database.connect(
        url = "jdbc:sqlite:${System.getProperty("user.home")}/Desktop/RecordDB.sqlite",
        driver = "org.sqlite.JDBC"
    )
    val repository = ExposedRecordRepository(database)
    val service = RecordService(repository)

    // Ktor Plugins
    install(CallLogging) {
        level = Level.INFO
        format { call ->
            val status = call.response.status()
            val httpMethod = call.request.httpMethod.value
            val userAgent = call.request.headers["User-Agent"]
            val uri = call.request.uri
            "$httpMethod $uri -> $status ( User-Agent: $userAgent)"
        }
    }

    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
            registerModule(JavaTimeModule())
            disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        }
    }

    install(StatusPages) {
        exception<Throwable> { call, cause ->
            cause.printStackTrace() // Hilfreich für Debugging
            call.respond(
                status = HttpStatusCode.InternalServerError,
                message = ErrorResponse("Serverfehler: ${cause.localizedMessage}")
            )
        }
    }

    routing {
        // Swagger UI
        swaggerUI(path = "docs", swaggerFile = "openapi/documentation.yaml")
        // API Routes
        recordRoutes(service)
    }
}