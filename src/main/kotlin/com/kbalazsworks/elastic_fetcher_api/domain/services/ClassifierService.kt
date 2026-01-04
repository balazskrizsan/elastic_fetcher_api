package com.kbalazsworks.elastic_fetcher_api.domain.services

import com.kbalazsworks.elastic_fetcher_api.domain.repositories.semantic_log_classifier.ILogApi
import com.kbalazsworks.elastic_fetcher_api.domain.repositories.semantic_log_classifier.ILogApi.VectorStoreXSimilarity
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

    data class ClassifiedError(val similarity: Float, val message: String, val contextInfoMessage: String)

    fun start(index: String, errorIndexName: String, batchSize: Int = 10) {
        while (true) {
            val hits = elasticService.fetch(index, batchSize)
            val lastHit = hits.last()

            runStateService.saveLastHit(index, lastHit)

            val classifiedResponses = mutableListOf<VectorStoreXSimilarity>()
            hits.forEach { hit ->
                val source = hit.source()
                if (source != null) {
                    val response = processLog(source)
                    if (null != response) {
                        classifiedResponses.add(response)
                    }
                }
            }

            elasticService.sendBulk(errorIndexName, classifiedResponses)
        }
    }

    private fun processLog(doc: LogEntry): VectorStoreXSimilarity? {
        val level = doc.level ?: return null
        val message = doc.message ?: return null
        val structuredMessage = doc.structuredMessage ?: return null

        val response = logApi.postClassification(ILogApi.Request(level, structuredMessage, message))

        return response.body?.data?.first()
    }
}
