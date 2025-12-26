package com.kbalazsworks.elastic_fetcher_api.domain.services

import co.elastic.clients.elasticsearch._types.FieldValue
import co.elastic.clients.elasticsearch._types.SortOrder
import co.elastic.clients.json.JsonData
import com.kbalazsworks.elastic_fetcher_api.domain.factories.ElasticClientFactory
import com.kbalazsworks.elastic_fetcher_api.domain.value_objects.LogEntry
import java.time.Instant

class ElkBatchLogReader() {
    fun fetchAllWarnAndAbove(
        index: String,
        from: Instant,
        batchSize: Int = 10,
        consumer: (LogEntry) -> Unit
    ) {
        val client = ElasticClientFactory().create()
        var searchAfter: List<FieldValue>? = null

        while (true) {
            val response = client.search<LogEntry>(
                { s ->
                    s.index(index)
                        .size(batchSize)
                        .sort {
                            it.field { f ->
                                f.field("@timestamp").order(SortOrder.Asc)
                            }
                        }
                        .query { q ->
                            q.bool { b ->
                                b.filter {
                                    it.range { r ->
                                        r.field("@timestamp").gte(JsonData.of(from.toString()))
                                    }
                                }
                                b.filter {
                                    it.range { r ->
                                        r.field("level_value").gte(JsonData.of(30000))
                                    }
                                }
                            }
                        }
                        .apply {
                            searchAfter?.let { sa ->
                                searchAfter(sa)
                            }
                        }
                },
                LogEntry::class.java
            )

            val hits = response.hits().hits()
            if (hits.isEmpty()) break

            hits.forEach { hit ->
                hit.source()?.let(consumer)
            }

            searchAfter = hits.last().sort()
        }
    }
}
