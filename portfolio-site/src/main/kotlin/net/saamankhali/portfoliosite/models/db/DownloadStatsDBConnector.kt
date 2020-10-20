package net.saamankhali.portfoliosite.models.db

import net.saamankhali.portfoliosite.models.DownloadStats
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.core.env.Environment
import org.springframework.dao.DataAccessException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.sqlite.SQLiteErrorCode
import org.sqlite.SQLiteException
import java.nio.file.Path
import java.sql.ResultSet

@Service
class DownloadStatsDBConnector @Autowired constructor(private val setupHelper: DownloadDBSetupHelper,
                                                      private val dbUtils: DBUtils)
{
    private val downloadsDBTemplate: JdbcTemplate = setupHelper.dbTemplate
    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    init
    {
//        try
//        {
//        }
//        catch (ex: DataAccessException)
//        {
//            val rootCause = ex.rootCause
//            if(rootCause != null && rootCause is SQLiteException)
//            {
//                SQLiteErrorCode.SQLITE_ABORT
//                rootCause.resultCode
//                println()
//            }
//        }
//        val allItems1 = template1.query(statement, Test1Mapper())
//        val allItems2 = template2.query(statement, Test1Mapper())
//
//        allItems1.forEach { println(it.toString()) }
//        println()
//        allItems2.forEach { println(it.toString()) }
    }

    // 0 0 0 * * 0
    @Scheduled(cron = "5 * * * * *")
    fun resetWeeklyCounts()
    {
        logger.debug("Resetting weekly counts in database...")
        downloadsDBTemplate.update("""
            UPDATE DownloadCounts SET downloads_this_week = 0
        """.trimIndent())
    }

    fun getStats(path: Path) =
        downloadsDBTemplate.query("""
            SELECT path, downloads_all_time, downloads_this_week
            FROM DownloadCounts
            WHERE path = ?
        """.trimIndent(),
            RowMapper { resultSet: ResultSet, _ ->
                DownloadStats(
                    path = dbUtils.fromDBPathString(resultSet.getString("path")),
                    downloads_all_time = resultSet.getInt("downloads_all_time"),
                    downloads_this_week = resultSet.getInt("downloads_this_week")
                )
            },
            dbUtils.toDBPathString(path)).firstOrNull()

    fun incrementCounts(path: Path)
    {
        downloadsDBTemplate.update(
                """
                    INSERT INTO DownloadCounts (path) VALUES (?)
                    ON CONFLICT (path) DO UPDATE SET downloads_all_time = downloads_all_time + 1, downloads_this_week = downloads_this_week + 1
                """.trimIndent(),
            dbUtils.toDBPathString(path))
    }
}