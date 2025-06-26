package com.RecordAPI

import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals
import io.ktor.client.call.*

class ApplicationTest {

    @Test
    fun testRootEndpoint() = testApplication {
        application {
            // This is where you set up the app (optional if you use modules)
            module() // call your main module
        }

        val response = client.get("/") // Test your route
        assertEquals(200, response.status.value)
        assertEquals("Hello World!", response.body<String>())
    }

}
