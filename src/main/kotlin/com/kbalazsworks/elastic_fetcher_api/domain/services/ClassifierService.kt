package com.kbalazsworks.elastic_fetcher_api.domain.services

import com.kbalazsworks.elastic_fetcher_api.domain.repositories.semantic_log_classifier.ILogApi
import com.kbalazsworks.elastic_fetcher_api.domain.value_objects.LogEntry
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class ClassifierService(
    private val runStateService: RunStateService,
    private val elasticService: ElasticService,
    private val logApi: ILogApi,
) {
    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }

    fun run(index: String, errorIndexName: String, batchSize: Int = 20): Boolean {
        val state = runStateService.getByIndex(index)
        log.info("Cycle start: {} / {}", index, state.timestamp)

        val hits = elasticService.fetchOverInfo(index, batchSize, state.timestamp, state.doc)
        if (hits.isEmpty()) {
            log.debug("No new documents for index={}", index)

            return false
        }

        runStateService.saveLastHit(index, hits.last())

        val classifiedResponses = hits
            .mapNotNull { it.source() }
            .mapNotNull(::processLog)

        elasticService.sendBulk(errorIndexName, classifiedResponses)

        return true
    }

    private fun processLog(doc: LogEntry): ILogApi.Response? {
        val level = doc.level ?: return null
        val message = doc.message ?: return null
        val structuredMessage = doc.structuredMessage ?: return null
        val timestamp = doc.timestamp ?: return null

        val response = logApi.postClassification(ILogApi.Request(level, structuredMessage, message, timestamp))

        return response.body?.data
    }
}
