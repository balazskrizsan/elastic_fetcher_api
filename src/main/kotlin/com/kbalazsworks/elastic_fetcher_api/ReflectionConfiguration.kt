package com.kbalazsworks

import org.springframework.aot.hint.MemberCategory
import org.springframework.aot.hint.RuntimeHints
import org.springframework.aot.hint.RuntimeHintsRegistrar
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.ImportRuntimeHints

@Configuration
@RegisterReflectionForBinding(
    org.springframework.http.ResponseEntity::class,
    ch.qos.logback.classic.LoggerContext::class,
    ch.qos.logback.core.spi.ContextAwareBase::class,
    com.kbalazsworks.elastic_fetcher_api.domain.value_objects.LogEntry::class,
)
class AppRuntimeHintsRegistrar : RuntimeHintsRegistrar {
    override fun registerHints(hints: RuntimeHints, classLoader: ClassLoader?) {
        val categories = MemberCategory.entries.toTypedArray()

        hints.reflection().registerType(org.springframework.http.ResponseEntity::class.java, *categories)
        hints.reflection().registerType(ch.qos.logback.classic.LoggerContext::class.java, *categories)
        hints.reflection().registerType(ch.qos.logback.core.spi.ContextAwareBase::class.java, *categories)
        hints.reflection().registerType(com.kbalazsworks.elastic_fetcher_api.domain.value_objects.LogEntry::class.java, *categories)
    }
}
