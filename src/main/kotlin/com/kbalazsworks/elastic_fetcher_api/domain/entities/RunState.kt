package com.kbalazsworks.elastic_fetcher_api.domain.entities

data class RunState (
    var index: String,
    var timestamp: Long,
    var doc: Long,
)
