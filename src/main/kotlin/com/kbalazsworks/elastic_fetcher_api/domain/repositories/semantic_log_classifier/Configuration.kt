package com.kbalazsworks.elastic_fetcher_api.domain.repositories.semantic_log_classifier

import io.netty.resolver.DefaultAddressResolverGroup
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.support.WebClientAdapter
import org.springframework.web.service.invoker.HttpServiceProxyFactory
import reactor.netty.http.client.HttpClient

@Configuration
class ClientConfig {
    @Bean
    fun iLogApi(): ILogApi {
        val httpClient = HttpClient.create().resolver(DefaultAddressResolverGroup.INSTANCE)

        val webClient = WebClient.builder()
            .baseUrl("http://localhost:3080")
            .clientConnector(ReactorClientHttpConnector(httpClient))
            .build()

        val adapter = WebClientAdapter.create(webClient)

        return HttpServiceProxyFactory
            .builder()
            .exchangeAdapter(adapter)
            .build()
            .createClient(ILogApi::class.java)
    }
}
