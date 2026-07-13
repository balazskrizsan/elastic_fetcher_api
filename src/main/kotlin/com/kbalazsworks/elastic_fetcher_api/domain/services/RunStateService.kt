package com.kbalazsworks.elastic_fetcher_api.domain.services

import co.elastic.clients.elasticsearch._types.FieldValue
import co.elastic.clients.elasticsearch.core.search.Hit
import com.kbalazsworks.elastic_fetcher_api.domain.entities.RunState
import com.kbalazsworks.elastic_fetcher_api.domain.jooq_orm.exceptions.OrmException
import com.kbalazsworks.elastic_fetcher_api.domain.repositories.sql.RunStateRepository
import com.kbalazsworks.elastic_fetcher_api.domain.value_objects.LogEntry
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.temporal.ChronoUnit

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

    fun getOrDefaultByIndex(index: String): RunState {
        return try {
            repository._getOneById(index)
        } catch (e: OrmException) {
            log.warn("No run state found for index={}, using default (previous day)", index)
            val previousDayTimestamp = Instant.now().minus(1, ChronoUnit.DAYS).toEpochMilli()
            val defaultState = RunState(index, previousDayTimestamp, 0L)
            repository._saveOnDuplicateKeyUpdate(defaultState)
            defaultState
        }
    }
}
