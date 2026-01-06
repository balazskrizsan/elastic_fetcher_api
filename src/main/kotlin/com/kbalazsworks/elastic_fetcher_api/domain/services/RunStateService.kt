package com.kbalazsworks.elastic_fetcher_api.domain.services

import co.elastic.clients.elasticsearch._types.FieldValue
import co.elastic.clients.elasticsearch.core.search.Hit
import com.kbalazsworks.elastic_fetcher_api.domain.entities.RunState
import com.kbalazsworks.elastic_fetcher_api.domain.repositories.sql.RunStateRepository
import com.kbalazsworks.elastic_fetcher_api.domain.value_objects.LogEntry
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class RunStateService(private val repository: RunStateRepository) {
    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }

    fun saveLastHit(index: String, lastHit: Hit<LogEntry>) {
        val lastSort: List<FieldValue> = lastHit.sort()

        val lastTimestamp: Long = lastSort[0].longValue()
        val lastDoc: Long = lastSort[1].longValue()

        log.info("State lock: timestamp: {}, doc: {}", lastTimestamp, lastDoc)

        repository._saveOnDuplicateKeyUpdate(RunState(index, lastTimestamp, lastDoc))
    }

    fun getByIndex(index: String) = repository._getOneById(index)
}
