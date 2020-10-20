package net.saamankhali.portfoliosite.models.db

import net.saamankhali.portfoliosite.PortfolioSiteProperties
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service
import org.sqlite.SQLiteDataSource
import javax.sql.DataSource

// const val DOWNLOADS_DB_PATH = "downloads.db"

@Configuration
class DBSources {
    @Bean(name = ["downloadsDataSource"])
    fun getStatsDataSource(properties: PortfolioSiteProperties): DataSource = (DataSourceBuilder.create() as DataSourceBuilder<SQLiteDataSource>)
        .url("jdbc:sqlite:${properties.downloadDBLocation}")
        .build()

    @Bean(name = ["downloadsDataSourceTemplate"])
    fun firstJdbcTemplate(@Qualifier("downloadsDataSource") downloadsDS: DataSource): JdbcTemplate = JdbcTemplate(downloadsDS)
}

@Service
class DownloadDBSetupHelper @Autowired constructor(@Qualifier(value = "downloadsDataSourceTemplate") initDownloadsDBTemplate: JdbcTemplate, initProperties: PortfolioSiteProperties)
    : DBSetupHelper(initDownloadsDBTemplate, initProperties) {
    override fun getCurrentDBVersion(): Int = 1

    override fun updateDB(oldVersion: Int) {
        if(oldVersion < 1)
        {
            dbTemplate.execute(
            """
            CREATE TABLE DownloadCounts(
                path VARCHAR(3072) PRIMARY KEY NOT NULL,
                downloads_this_week INTEGER DEFAULT 1 NOT NULL,
                downloads_all_time INTEGER DEFAULT 1 NOT NULL
            )
            """.trimIndent())

            dbTemplate.execute(
            """
            CREATE TABLE DownloadMetadata(
                path VARCHAR(3072) PRIMARY KEY NOT NULL,
                datetime_modified TEXT NOT NULL,
                sha_1 VARCHAR(3072) NOT NULL,
                sha_2_256 VARCHAR(3072) NOT NULL
            )
            """.trimIndent())
        }
    }
}