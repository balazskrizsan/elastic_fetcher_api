package com.kbalazsworks.elastic_fetcher_api.domain.services

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class ApplicationPropertiesService {
    @Value("\${spring.application.name}")
    lateinit var springApplicationName: String

    @Value("\${server.port}")
    lateinit var serverPort: String

    @Value("\${server.env}")
    lateinit var serverEnv: String

    @Value("\${spring.datasource.url}")
    lateinit var springDatasourceUrl: String

    @Value("\${spring.datasource.username}")
    lateinit var springDatasourceUsername: String

    @Value("\${spring.datasource.password}")
    lateinit var springDatasourcePassword: String

    @Value("\${spring.datasource.driver-class-name}")
    lateinit var springDatasourceDriverClassName: String

    @Value("\${logback.logstash.enabled}")
    lateinit var logbackLogstashEnabledString: String
    val logbackLogstashEnabled: Boolean by lazy {
        logbackLogstashEnabledString.toBoolean()
    }

    @Value("\${logback.logstash.full_host}")
    lateinit var logbackLogstashFullHost: String

    @Value("\${native.reflection-configuration-generator.enabled}")
    private lateinit var nativeReflectionConfigurationGeneratorEnabledEnabled: String
    val isNativeReflectionConfigurationGeneratorEnabledEnabled: Boolean by lazy {
        nativeReflectionConfigurationGeneratorEnabledEnabled.toBoolean()
    }

    @Value("\${app.elastic-fetcher-task-enabled}")
    private lateinit var appElasticFetcherTaskEnabled: String
    val isAppElasticFetcherTaskEnabled: Boolean by lazy {
        appElasticFetcherTaskEnabled.toBoolean()
    }

    @Value("\${app.elastic-host}")
    lateinit var appElasticHost: String

    @Value("\${app.elastic-port}")
    private lateinit var appElasticPortString: String
    val appElasticPort: Int by lazy {
        appElasticPortString.toInt()
    }

    @Value("\${app.elastic-scheme}")
    lateinit var appElasticScheme: String
}
