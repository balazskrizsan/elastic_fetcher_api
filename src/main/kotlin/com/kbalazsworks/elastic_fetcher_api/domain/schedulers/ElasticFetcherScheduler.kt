package com.kbalazsworks.elastic_fetcher_api.domain.schedulers

import com.kbalazsworks.elastic_fetcher_api.domain.repositories.semantic_log_classifier.ILogApi
import com.kbalazsworks.elastic_fetcher_api.domain.services.ClassifierService
import com.kbalazsworks.elastic_fetcher_api.domain.services.ElasticService
import com.kbalazsworks.elastic_fetcher_api.domain.services.RunStateService
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class ElasticFetcherScheduler(
    private val runStateService: RunStateService,
    private val elasticService: ElasticService,
    private val logApi: ILogApi,
) {
    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
        private val indexName = "logs-dev"
        private val errorIndexName = "alerting-logs-dev"
    }

    @Scheduled(fixedDelay = 3000000)
    fun periodicFetch() {
        log.info("FixedDelay scheduling started")

        ClassifierService(runStateService, elasticService, logApi).start(indexName, errorIndexName)
    }
}
