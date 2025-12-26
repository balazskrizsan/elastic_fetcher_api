package com.kbalazsworks.elastic_fetcher_api.domain.services

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import kotlin.text.toBoolean

@Service
class ApplicationPropertiesService {
    @Value("\${spring.application.name}")
    lateinit var springApplicationName: String

    @Value("\${server.port}")
    lateinit var serverPort: String

    @Value("\${server.env}")
    lateinit var serverEnv: String

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
}
