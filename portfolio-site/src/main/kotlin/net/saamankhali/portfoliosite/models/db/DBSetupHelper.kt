package net.saamankhali.portfoliosite.models.db

import net.saamankhali.portfoliosite.PortfolioSiteProperties
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import java.io.File
import java.lang.IllegalStateException

abstract class DBSetupHelper(initDBTemplate: JdbcTemplate, initProperties: PortfolioSiteProperties) {
    val dbTemplate: JdbcTemplate = initDBTemplate
    protected val logger: Logger = LoggerFactory.getLogger(javaClass)
    protected val properties: PortfolioSiteProperties = initProperties
    // protected abstract val CURRENT_VERSION: Int

    init
    {
        val DB_INFO_VERSION: Int = 1

        val currentVer: Int = getCurrentDBVersion()
        val existingVer: Int? = getDBVersion()

        if(existingVer == null)
        {
            if(properties.createDatabases)
            {
                dbTemplate.execute("""
                    CREATE TABLE DBInfo(
                        db_version INTEGER NOT NULL,
                        info_version INTEGER NOT NULL
                    )
                """.trimIndent())

                updateDB(0)

                dbTemplate.update("""
                    INSERT INTO DBInfo(db_version, info_version)
                    VALUES (?, ?)
                """.trimIndent(), currentVer, DB_INFO_VERSION)

                logger.info("Database created.")
            }
            else
            {
                logger.error("Database not found. Set the \"createDatabases\" property to " +
                        "true to create missing tables.")
                throw DatabaseNotFoundException()
            }
        }
        else
        {
            updateDB(existingVer)

            dbTemplate.update("""
                UPDATE DBInfo
                SET db_version = ?
            """.trimIndent(), currentVer)

            logger.info("Existing database initialized.")
        }
    }

    protected abstract fun getCurrentDBVersion(): Int

    protected abstract fun updateDB(oldVersion: Int)

    protected fun tableExists(tableName: String): Boolean
    {
        var result: Boolean = false
        dbTemplate.query("SELECT name FROM sqlite_master WHERE name = ?",
                RowMapper { _, _ -> result = true }, tableName)

        return result
    }

    fun getDBVersion(): Int?
    {
        if(tableExists("DBInfo")) {
            val results: List<Int> = dbTemplate.query("""
            SELECT db_version
            FROM DBInfo
        """.trimIndent(), RowMapper { resultSet, _ -> resultSet.getInt("db_version") })

            if (results.isEmpty()) {
                throw IllegalStateException("DBInfo table has no data")
            } else {
                return results[0]
            }
        }
        else
        {
            return null
        }
    }

//    fun setupTable(tableName: String, createStatement: String): Boolean
//    {
//        if(!tableExists(tableName))
//        {
//            logger.info("Table \"$tableName\" was not found; it will be created.")
//            dbTemplate.execute(createStatement)
//
//            return true
//        }
//        else
//        {
//            logger.info("Existing table \"$tableName\" found.")
//
//            return false
//        }
//    }
}

class DatabaseNotFoundException()
    : Throwable("The database was not found.")
