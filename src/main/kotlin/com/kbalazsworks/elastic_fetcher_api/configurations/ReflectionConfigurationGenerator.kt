package com.kbalazsworks.elastic_fetcher_api.configurations

import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.core.spi.ContextAwareBase
import com.kbalazsworks.common.io_module.services.FileService
import com.kbalazsworks.common.native_build_module.services.RuntimeHintsReflectionGenerator
import com.kbalazsworks.common.templating_module.services.MustacheService
import com.kbalazsworks.elastic_fetcher_api.domain.services.ApplicationPropertiesService
import com.kbalazsworks.elastic_fetcher_api.domain.value_objects.LogEntry
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import org.springframework.http.ResponseEntity

@Configuration
class ReflectionConfigurationGenerator(private val ap: ApplicationPropertiesService) {
    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }

    @PostConstruct
    fun generate() {
        val isNativeReflectionConfigurationGeneratorEnabled: Boolean = ap
            .isNativeReflectionConfigurationGeneratorEnabledEnabled

        log.info("ReflectionConfigurationGenerator status: {}", isNativeReflectionConfigurationGeneratorEnabled)

        if (!isNativeReflectionConfigurationGeneratorEnabled) {
            return
        }

        RuntimeHintsReflectionGenerator(MustacheService(), FileService())
            .generate(
                "src/main/kotlin/com/kbalazsworks/elastic_fetcher_api/ReflectionConfiguration.kt",
                listOf(
                ),
                listOf(
                    ResponseEntity::class.java,
                    LoggerContext::class.java,
                    ContextAwareBase::class.java,
                    LogEntry::class.java,
                )
            )
    }
}