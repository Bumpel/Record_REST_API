package com.RecordAPI

import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.test.assertFalse
import io.ktor.client.call.*
import kotlinx.coroutines.test.runTest

class ApplicationTest {
    // Erstellt ein neues Mock-Repository.
    val repo: RecordRepository = MockRecordRepository()

    @Test
    fun testRootEndpoint() = testApplication {

        application {
            module()
        }

        val response = client.get("/")
        assertEquals(200, response.status.value)
        assertEquals("Hello World!", response.body<String>())
    }

    @Test
    fun testCreateAndReadRecord() = runTest {

        val upload = DBRecordUpload("max", "Abialbum", "Abikünstler", 2024)

        val created = repo.create(upload)

        val fetched = repo.read(created.id)

        assertNotNull(fetched)

        assertEquals(created, fetched)
    }

    @Test
    fun testReadAllRecords() = runTest {

        repo.create(DBRecordUpload("anna", "Album1", "Künstler1", 2022))
        repo.create(DBRecordUpload("ben", "Album2", "Künstler2", 2023))

        val all = repo.readAll()

        assertEquals(2, all.size)
        assertTrue(all.any { it.owner == "anna" && it.title == "Album1" && it.artist == "Künstler1" && it.year == 2022 && it.id == 1})
        assertTrue(all.any { it.owner == "ben" && it.title == "Album2" && it.artist == "Künstler2" && it.year == 2023})

    }

    @Test
    fun testUpdateRecordOwnerMatches() = runTest {

        val created = repo.create(DBRecordUpload("sara", "OldTitle", "OldArtist", 2020))

        val update = DBRecordUpload("sara", "NewTitle", "NewArtist", 2021)
        val updated = repo.update(created.id, update)

        assertNotNull(updated)
        assertEquals("NewTitle", updated.title)
        assertEquals("NewArtist", updated.artist)
        assertEquals(2021, updated.year)
    }

    @Test
    fun testUpdateRecordOwnerDoesNotMatch() = runTest {

        val created = repo.create(DBRecordUpload("tom", "Title", "Artist", 2019))
        val update = DBRecordUpload("notom", "KeinTitel", "KeinArtist", 2018)
        val updated = repo.update(created.id, update)
        assertNull(updated)
    }

    @Test
    fun testDeleteRecordOwnerMatches() = runTest {

        val created = repo.create(DBRecordUpload("lena", "Test", "TestArtist", 2025))
        val deleted = repo.delete(created.id, "lena")
        assertTrue(deleted)
        assertNull(repo.read(created.id))
    }

    @Test
    fun testDeleteRecordOwnerDoesNotMatch() = runTest {

        val created = repo.create(DBRecordUpload("jan", "Test", "TestArtist", 2025))
        val deleted = repo.delete(created.id, "nichtjan")
        assertFalse(deleted)
        assertNotNull(repo.read(created.id))
    }
}
