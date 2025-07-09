package com.RecordAPI

import com.RecordAPI.domain.DBRecord
import com.RecordAPI.domain.DBRecordUpload


class MockRecordRepository : RecordRepository {
    private val records = mutableMapOf<Int, DBRecord>()


    private var currentId = 1

    @Synchronized
    fun getNextId(): Int {
        return currentId++
    }


    override suspend fun create(record: DBRecordUpload): DBRecord {
        val id = getNextId()
        val dbRecord = DBRecord(id, record.owner, record.title, record.artist, record.year)
        records[id] = dbRecord
        return dbRecord
    }

    override suspend fun read(id: Int): DBRecord? = records[id]

    override suspend fun readAll(): List<DBRecord> = records.values.toList()

    override suspend fun update(id: Int, update: DBRecordUpload): DBRecord? {
        val existing = records[id] ?: return null
        if (existing.owner != update.owner) return null
        val updated = existing.copy(
            title = update.title,
            artist = update.artist,
            year = update.year
        )
        records[id] = updated
        return updated
    }

    override suspend fun delete(id: Int, ownerParam: String): Boolean {
        val existing = records[id] ?: return false
        if (existing.owner != ownerParam) return false
        records.remove(id)
        return true
    }
}