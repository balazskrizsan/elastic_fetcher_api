package com.kbalazsworks.elastic_fetcher_api.domain.services

import co.elastic.clients.elasticsearch.ElasticsearchClient
import co.elastic.clients.elasticsearch._types.FieldValue
import co.elastic.clients.elasticsearch._types.SortOrder
import co.elastic.clients.json.JsonData
import com.fasterxml.jackson.annotation.JsonProperty
import com.kbalazsworks.elastic_fetcher_api.domain.repositories.semantic_log_classifier.ILogApi
import com.kbalazsworks.elastic_fetcher_api.domain.value_objects.LogEntry
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class ElasticService(private val elasticsearchClient: ElasticsearchClient) {
    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }

    data class ClassifiedError(
        val similarity: Float,
        val message: String,
        val contextInfoMessage: String,
        @field:JsonProperty("@timestamp") val timestamp: Instant
    )

    fun fetchOverInfo(index: String, batchSize: Int, lastTimestamp: Long? = null, lastDoc: Long? = null) =
        elasticsearchClient.search(
            { s ->
                s.index(index)
                    .size(batchSize)
                    .sort { it.field { f -> f.field("@timestamp").order(SortOrder.Asc) } }
                    .sort { it.field { f -> f.field("_doc").order(SortOrder.Asc) } }
                    .query { q ->
                        q.bool { b ->
                            b.filter {
                                it.range { r -> r.field("level_value").gte(JsonData.of(30001)) }
                            }
                        }
                    }
                    .apply {
                        if (lastTimestamp != null && lastDoc != null) {
                            searchAfter(listOf(FieldValue.of(lastTimestamp), FieldValue.of(lastDoc)))
                        }
                    }
            },
            LogEntry::class.java
        )
            .hits()
            .hits()
            .also { log.info("Number of elastic hits: {}", it.size) }

    fun sendBulk(index: String, entries: List<ILogApi.Response>) {
        entries.ifEmpty { return }

        val bulkResponse = elasticsearchClient.bulk { b ->
            entries.forEach { entry ->
                val ctx = entry.vectorStoreXSimilarity.vectorStoreX.contextInfo

                val type = ctx["type"]
                val message = ctx["text"]

                if (type != null && message != null) {
                    b.operations {
                        it.index { idx ->
                            idx.index(index).document(
                                ClassifiedError(
                                    entry.vectorStoreXSimilarity.similarity,
                                    type,
                                    message,
                                    entry.originalRequest.timestamp
                                )
                            )
                        }
                    }
                }
            }
            b
        }

        bulkResponse.takeIf { it.errors() }?.let {
            log.error("Bulk sending error items: {}", it.items())
            error("Bulk send error")
        }
    }
}
