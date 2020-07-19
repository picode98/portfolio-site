package net.saamankhali.portfoliosite.models.db

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.core.env.Environment
import org.springframework.dao.DataAccessException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service
import org.sqlite.SQLiteErrorCode
import org.sqlite.SQLiteException

@Service
class DownloadStatsDBConnector @Autowired constructor(initSetupHelper: DownloadDBSetupHelper)
{
    private val setupHelper: DownloadDBSetupHelper = initSetupHelper
    private val downloadsDBTemplate: JdbcTemplate = setupHelper.dbTemplate

    init
    {
        println("DownloadStatsDBConnector")
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
}