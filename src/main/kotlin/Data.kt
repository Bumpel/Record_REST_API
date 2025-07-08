package com.RecordAPI.domain

data class DBRecord(
    val id: Int,
    val owner: String,
    val title: String,
    val artist: String,
    val year: Int
)

data class DBRecordUpload(
    val owner: String,
    val title: String,
    val artist: String,
    val year: Int
)

data class ErrorResponse(
    val message: String
)