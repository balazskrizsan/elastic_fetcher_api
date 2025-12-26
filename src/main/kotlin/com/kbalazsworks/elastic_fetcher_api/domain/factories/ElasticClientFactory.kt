package com.kbalazsworks.elastic_fetcher_api.domain.factories

import co.elastic.clients.elasticsearch.ElasticsearchClient
import co.elastic.clients.json.jackson.JacksonJsonpMapper
import co.elastic.clients.transport.rest_client.RestClientTransport
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.apache.http.HttpHost
import org.elasticsearch.client.RestClient

class ElasticClientFactory() {
    fun create() = ElasticsearchClient(
        RestClientTransport(
            RestClient.builder(HttpHost("localhost", 9200, "http")).build(),
            JacksonJsonpMapper(elasticJsonpMapper())
        )
    )

    init {
        println(elasticJsonpMapper().registeredModuleIds)
    }

    fun elasticJsonpMapper(): JsonMapper = JsonMapper.builder()
        .addModule(JavaTimeModule())
        .addModule(Jdk8Module())
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        .build()
}
