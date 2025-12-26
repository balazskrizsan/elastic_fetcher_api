package com.kbalazsworks.elastic_fetcher_api.domain.schedulers

import com.kbalazsworks.elastic_fetcher_api.domain.repositories.SemanticLogClassifierRepository.ILogApi
import com.kbalazsworks.elastic_fetcher_api.domain.services.ElkBatchLogReader
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.Scheduled
import java.time.Instant

@Configuration
class ElasticFetcherScheduler(
    private val logApi: ILogApi
) {
    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }

    @Scheduled(fixedRate = 30000)
    fun periodicFetch() {
        ElkBatchLogReader().fetchAllWarnAndAbove(
            index = "logs-dev",
            from = Instant.parse("2025-12-22T00:00:00Z")
        ) { elasticDoc ->
            val level = elasticDoc.level ?: return@fetchAllWarnAndAbove
            val message = elasticDoc.message ?: return@fetchAllWarnAndAbove
            val rawMessage = elasticDoc.rawMessage ?: return@fetchAllWarnAndAbove

            log.info(logApi.postClassification(ILogApi.Request(level, message, rawMessage)).body.toString())
        }
    }
}
