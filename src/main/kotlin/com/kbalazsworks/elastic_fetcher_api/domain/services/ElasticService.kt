package com.kbalazsworks.elastic_fetcher_api.domain.services

import co.elastic.clients.elasticsearch._types.FieldValue
import co.elastic.clients.elasticsearch._types.SortOrder
import co.elastic.clients.elasticsearch.core.search.Hit
import co.elastic.clients.json.JsonData
import com.kbalazsworks.elastic_fetcher_api.domain.factories.ElasticClientFactory
import com.kbalazsworks.elastic_fetcher_api.domain.repositories.semantic_log_classifier.ILogApi.VectorStoreXSimilarity
import com.kbalazsworks.elastic_fetcher_api.domain.services.ClassifierService.ClassifiedError
import com.kbalazsworks.elastic_fetcher_api.domain.value_objects.LogEntry
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class ElasticService {
    companion object {
        val client = ElasticClientFactory().create()
        private val log = LoggerFactory.getLogger(this::class.java)
    }

    fun fetch(index: String, batchSize: Int, lastTimestamp: Long? = null, lastDoc: Long? = null): List<Hit<LogEntry>> {
        val response = client.search(
            { s ->
                s.index(index)
                    .size(batchSize)
                    .sort { it.field { f -> f.field("@timestamp").order(SortOrder.Asc) } }
                    .sort { it.field { f -> f.field("_doc").order(SortOrder.Asc) } }
                    .query { q ->
                        q.bool { b ->
                            b.filter {
                                it.range { r -> r.field("level_value").gte(JsonData.of(30000)) }
                            }
                        }
                    }
                    .apply {
                        if (lastTimestamp != null && lastDoc != null) {
                            searchAfter(
                                listOf(
                                    FieldValue.of(lastTimestamp),
                                    FieldValue.of(lastDoc)
                                )
                            )
                        }
                    }
            },
            LogEntry::class.java
        )

        val hits = response.hits().hits()
        log.info("Number of elastic hits: {}", hits.size)

        return hits
    }

    fun sendBulk(index: String, entries: List<VectorStoreXSimilarity>) {
        if (entries.isEmpty()) return

        val bulkRequest = client.bulk { b ->
            entries.forEach { entry ->
                val contextInfoType = entry.vectorStoreX.contextInfo["type"]
                val contextInfoMessage = entry.vectorStoreX.contextInfo["text"]

                if (contextInfoType != null && contextInfoMessage != null) {
                    b.operations { op ->
                        op.index { idx ->
                            idx.index(index)
                                .document(ClassifiedError(entry.similarity, contextInfoType, contextInfoMessage))
                        }
                    }
                }
            }
            b
        }

        if (bulkRequest.errors()) {
            log.error("Bulk sending error items: {}", bulkRequest.items())

            throw Exception("Bulk send error")
        }
    }
}
