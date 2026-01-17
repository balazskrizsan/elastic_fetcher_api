package com.kbalazsworks.elastic_fetcher_api

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.Instant

data class TestLogEntry(
    @JsonProperty("app") val app: String?,
    @JsonProperty("level") val level: String?,
    @JsonProperty("level_value") val level_value: Int?,
    @JsonProperty("structured_message") val structured_message: String?,
    @JsonProperty("message") val message: String?,
    @JsonProperty("@timestamp") val timestamp: Instant?,
    @JsonProperty("traceId") val traceId: String?,
)
