package com.kbalazsworks.elastic_fetcher_api.domain.repositories

import com.kbalazsworks.elastic_fetcher_api.domain.services.JooqService
import org.springframework.stereotype.Repository

@Repository
abstract class AbstractRepository(private val jooqService: JooqService) {
    fun getCtx() = jooqService.getDslContext()
}
