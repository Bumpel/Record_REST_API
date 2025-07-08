package com.RecordAPI.application

import com.RecordAPI.RecordRepository
import com.RecordAPI.domain.*

class RecordService(private val repository: RecordRepository) {
    suspend fun create(record: DBRecordUpload): DBRecord = repository.create(record)
    suspend fun read(id: Int): DBRecord? = repository.read(id)
    suspend fun readAll(): List<DBRecord> = repository.readAll()
    suspend fun update(id: Int, update: DBRecordUpload): DBRecord? = repository.update(id, update)
    suspend fun delete(id: Int, owner: String): Boolean = repository.delete(id, owner)
}