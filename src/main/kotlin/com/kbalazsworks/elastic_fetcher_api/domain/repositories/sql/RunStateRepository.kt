package com.kbalazsworks.elastic_fetcher_api.domain.repositories.sql

import com.kbalazsworks.elastic_fetcher_api.domain.db.tables.RunStates
import com.kbalazsworks.elastic_fetcher_api.domain.db.tables.records.RunStatesRecord
import com.kbalazsworks.elastic_fetcher_api.domain.db.tables.references.RUN_STATES
import com.kbalazsworks.elastic_fetcher_api.domain.entities.RunState
import com.kbalazsworks.elastic_fetcher_api.domain.jooq_orm.value_objects.TableMeta
import com.kbalazsworks.elastic_fetcher_api.domain.repositories.AbstractCrudRepository
import com.kbalazsworks.elastic_fetcher_api.domain.services.JooqService
import org.springframework.stereotype.Repository

typealias RunStateGenerics = AbstractCrudRepository<RunState, RunStates, RunStatesRecord>

@Repository
class RunStateRepository(jooqService: JooqService): RunStateGenerics(jooqService) {
    override val tableMeta = TableMeta(RunState::class.java, RUN_STATES)
}
