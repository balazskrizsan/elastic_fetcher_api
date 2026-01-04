package com.kbalazsworks.elastic_fetcher_api.domain.value_objects

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

data class LogEntry(
    @JsonProperty("app") val app: String?,
    @JsonProperty("level") val level: String?,
    @JsonProperty("level_value") val levelValue: Int?,
    @JsonProperty("structured_message") val structuredMessage: String?,
    @JsonProperty("message") val message: String?,
    @JsonProperty("timestamp") val timestamp: Long?,
    @JsonProperty("traceId") val traceId: String?,
)
