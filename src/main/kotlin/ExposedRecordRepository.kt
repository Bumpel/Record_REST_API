package com.RecordAPI

import com.RecordAPI.domain.*
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

object Records : Table() {
    val id = integer("id").autoIncrement()
    val owner = varchar("owner", 50)
    val title = varchar("title", 100)
    val artist = varchar("artist", 100)
    val year = integer("year")
    override val primaryKey = PrimaryKey(id)
}

class ExposedRecordRepository(database: Database) : RecordRepository {

    init {
        transaction(database) {
            SchemaUtils.create(Records)
        }
    }

    // Das block-Lambda MUSS non-suspend sein!
    private suspend fun <T> dbQuery(block: () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    override suspend fun create(record: DBRecordUpload): DBRecord = dbQuery {
        val insertResult = Records.insert {
            it[owner] = record.owner
            it[title] = record.title
            it[artist] = record.artist
            it[year] = record.year
        }
        val id = insertResult[Records.id] ?: error("Insert hat keine ID geliefert!")
        DBRecord(id, record.owner, record.title, record.artist, record.year)
    }

    override suspend fun read(id: Int): DBRecord? = dbQuery {
        Records.selectAll()
            .where { Records.id eq id }
            .map {
                DBRecord(
                    id = it[Records.id],
                    owner = it[Records.owner],
                    title = it[Records.title],
                    artist = it[Records.artist],
                    year = it[Records.year]
                )
            }
            .singleOrNull()
    }

    override suspend fun readAll(): List<DBRecord> = dbQuery {
        Records.selectAll()
            .map {
                DBRecord(
                    id = it[Records.id],
                    owner = it[Records.owner],
                    title = it[Records.title],
                    artist = it[Records.artist],
                    year = it[Records.year]
                )
            }
    }

    override suspend fun update(id: Int, update: DBRecordUpload): DBRecord? = dbQuery {
        val existing = Records.selectAll().where { Records.id eq id }.singleOrNull()
        if (existing == null || existing[Records.owner] != update.owner) {
            null
        } else {
            Records.update({ Records.id eq id }) {
                it[title] = update.title
                it[artist] = update.artist
                it[year] = update.year
            }
            DBRecord(id, update.owner, update.title, update.artist, update.year)
        }
    }

    override suspend fun delete(id: Int, ownerParam: String): Boolean = dbQuery {
        val existing = Records.selectAll().where { Records.id eq id }.singleOrNull()
        if (existing == null || existing[Records.owner] != ownerParam) {
            false
        } else {
            Records.deleteWhere { Records.id eq id }
            true
        }
    }
}