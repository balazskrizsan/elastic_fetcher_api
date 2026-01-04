package com.kbalazsworks.elastic_fetcher_api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ImportRuntimeHints
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
@ImportRuntimeHints(AppRuntimeHintsRegistrar::class)
class ElasticFetcherApiApplication

fun main(args: Array<String>) {
    runApplication<ElasticFetcherApiApplication>(*args)
}
