package com.kbalazsworks.elastic_fetcher_api.domain.beans

import com.kbalazsworks.elastic_fetcher_api.domain.factories.ElasticClientFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ElasticClientBean(private val elasticClientFactory: ElasticClientFactory) {
    @Bean
    fun elasticsearchClient() = elasticClientFactory.create()
}
