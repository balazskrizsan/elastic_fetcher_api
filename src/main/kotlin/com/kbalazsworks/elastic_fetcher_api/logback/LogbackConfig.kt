package com.kbalazsworks.elastic_fetcher_api.logback

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.ConsoleAppender
import com.kbalazsworks.elastic_fetcher_api.domain.services.ApplicationPropertiesService
import jakarta.annotation.PostConstruct
import net.logstash.logback.appender.LogstashTcpSocketAppender
import net.logstash.logback.encoder.LogstashEncoder
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets

@Component
class LogbackConfig(
    private var ap: ApplicationPropertiesService,
    private var logBackState: LogBackState
) {
    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }

    @PostConstruct
    fun setupLogger() {
        val currentEnv = ap.serverEnv
        val currentApp = ap.springApplicationName

        log.info(
            "LogbackConfig setup / logstash enabled: {}, app: {} env: {}, url: {}",
            ap.logbackLogstashEnabled,
            currentApp,
            currentEnv,
            ap.logbackLogstashFullHost
        )

        val context = LoggerFactory.getILoggerFactory() as LoggerContext
        context.apply {
            reset()
            addTurboFilter(LogbackMdcTurboFilter(currentEnv, currentApp, logBackState))
        }

        with(context.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME)) {
            detachAndStopAllAppenders()
            level = Level.INFO
            if (ap.logbackLogstashEnabled) {
                addAppender(getLogstashTcpSocketAppender(context))
            }
            addAppender(getLoggingEventConsoleAppender(context))
        }

        log.info("LogbackConfig setup")
    }

    private fun getLoggingEventConsoleAppender(context: LoggerContext) = ConsoleAppender<ILoggingEvent?>().apply {
        log.info("LogbackConfig console created")

        this.context = context

        encoder = PatternLayoutEncoder().apply {
            this.context = context
            pattern = "%highlight(%d [%thread]) %highlight(%-5level) %cyan(%logger{35}) - %msg%n"
            charset = StandardCharsets.UTF_8
            start()
        }

        start()
    }

    private fun getLogstashTcpSocketAppender(context: LoggerContext) = LogstashTcpSocketAppender().apply {
        log.info("LogbackConfig logstash created")

        this.context = context
        addDestination(ap.logbackLogstashFullHost)

        encoder = LogstashEncoder()
        start()
    }
}