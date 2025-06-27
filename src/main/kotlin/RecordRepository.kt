package com.RecordAPI

interface RecordRepository {
    suspend fun create(record: DBRecordUpload): DBRecord
    suspend fun read(id: Int): DBRecord?
    suspend fun readAll(): List<DBRecord>
    suspend fun update(id: Int, update: DBRecordUpload): DBRecord?
    suspend fun delete(id: Int, ownerParam: String): Boolean
}