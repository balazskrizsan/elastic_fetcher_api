package com.kbalazsworks.elastic_fetcher_api.domain.services

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jooq.DSLContext
import org.jooq.SQLDialect
import org.jooq.TransactionalRunnable
import org.jooq.impl.DSL
import org.springframework.stereotype.Service

@Service
class JooqService(private val ap: ApplicationPropertiesService) {

    private var dslContext: DSLContext? = null

    fun getDslContext(): DSLContext {
        return dslContext ?: run {
            val config = HikariConfig().apply {
                transactionIsolation = "TRANSACTION_READ_UNCOMMITTED"
                jdbcUrl = ap.springDatasourceUrl
                username = ap.springDatasourceUsername
                password = ap.springDatasourcePassword
                maximumPoolSize = 10
                addDataSourceProperty("cachePrepStmts", "true")
                addDataSourceProperty("prepStmtCacheSize", "250")
                addDataSourceProperty("prepStmtCacheSqlLimit", "2048")
            }

            dslContext = DSL.using(HikariDataSource(config), SQLDialect.POSTGRES)
            dslContext!!
        }
    }

    fun transaction(transactional: TransactionalRunnable?) {
        getDslContext().transaction(transactional)
    }

    companion object {
        private var dslContext: DSLContext? = null
    }
}