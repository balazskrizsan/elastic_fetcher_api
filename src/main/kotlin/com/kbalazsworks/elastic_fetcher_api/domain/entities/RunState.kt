package com.kbalazsworks.elastic_fetcher_api.domain.entities

import java.time.OffsetDateTime

data class RunState (
    var index: String,
    var timestamp: Long,
    var doc: Long,
    var updatedAt: OffsetDateTime? = null
)
