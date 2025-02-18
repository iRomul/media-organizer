package io.github.iromul.home.photolib.storage

import io.github.iromul.home.photolib.meta.Origin
import io.github.iromul.home.photolib.scripts.Result
import io.github.oshai.kotlinlogging.KotlinLogging
import java.io.File
import java.sql.Connection
import java.sql.Timestamp
import java.time.LocalDateTime

private val logging = KotlinLogging.logger {}

class DbStorage(
    private val connection: Connection
) : Storage {

    override fun clearStorage() {
        logging.info { "Initializing database..." }

        val schemaFile = requireNotNull(this::class.java.getResourceAsStream("/schema.sql")) { "schema not found" }
            .bufferedReader(Charsets.UTF_8)
            .readText()

        connection.prepareStatement(schemaFile).execute()
        connection.prepareStatement("DELETE FROM media_files").execute()
    }

    override fun storeCache(result: Result) {
        logging.atInfo {
            message = "Storing database"
            payload = mapOf(
                "uniqueFiles" to result.uniqueFiles.size,
                "duplicateFiles" to result.duplicateFiles.size
            )
        }

        connection.prepareStatement("insert into media_files(absolute_path, shoot_at, origin) values(?, ?, ?)")
            .use { statement ->
                result.uniqueFiles.forEach {
                    statement.setString(1, it.absolutePath)
                    statement.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()))
                    statement.setString(3, Origin.MISC.name)

                    statement.executeUpdate()
                }
            }

        connection.prepareStatement("insert into media_files(absolute_path, shoot_at, origin, checksum) values(?, ?, ?, ?)")
            .use { statement ->
                result.duplicateGroups.entries.forEach { (checksum, files) ->
                    files.forEach { file ->
                        statement.setString(1, file.absolutePath)
                        statement.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()))
                        statement.setString(3, Origin.MISC.name)
                        statement.setString(4, checksum.toString())

                        statement.execute()
                    }
                }
            }
    }

    override fun retrieveCache(): Result {
        val result = Result()

        connection.createStatement()
            .use { statement ->
                val rs = statement.executeQuery("select absolute_path from uniques")

                while (rs.next()) {
                    val absolutePath = rs.getString("absolute_path")

                    result.uniqueFiles.add(File(absolutePath))
                }
            }

        val mmap = mutableMapOf<String, MutableList<File>>()

        connection.createStatement()
            .use { statement ->
                val rs = statement.executeQuery("select absolute_path, checksum from duplicates")

                while (rs.next()) {
                    val absolutePath = rs.getString("absolute_path")
                    val checksum = rs.getString("checksum")

                    mmap.putIfAbsent(checksum, mutableListOf())
                    mmap[checksum]!!.add(File(absolutePath))
                }

                result.duplicateGroups.putAll(mmap)
            }

        logging.atInfo {
            message = "Read database"
            payload = mapOf(
                "uniqueFiles" to result.uniqueFiles.size,
                "duplicateFiles" to result.duplicateFiles.size
            )
        }

        return result
    }
}
