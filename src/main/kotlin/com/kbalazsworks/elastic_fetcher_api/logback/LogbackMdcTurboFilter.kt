package com.kbalazsworks.elastic_fetcher_api.logback

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.turbo.TurboFilter
import ch.qos.logback.core.spi.FilterReply
import org.slf4j.MDC
import org.slf4j.Marker

class LogbackMdcTurboFilter(
    private val envName: String,
    private val appName: String,
    private var logBackState: LogBackState
) : TurboFilter() {
    companion object {
        const val LONG_TERM_MDC_NAME: String = "long_term"
        const val MDC_ENV_NAME: String = "env"
        const val MDC_APP_NAME: String = "app"
    }

    override fun decide(
        marker: Marker?,
        logger: Logger?,
        level: Level?,
        s: String?,
        objects: Array<out Any?>?,
        throwable: Throwable?
    ): FilterReply {
        if (MDC.get(MDC_ENV_NAME) == null) {
            MDC.put(MDC_ENV_NAME, envName)
        }

        if (MDC.get(MDC_APP_NAME) == null) {
            MDC.put(MDC_APP_NAME, appName)
        }

        MDC.put(LONG_TERM_MDC_NAME, logBackState.threadLocalLongTermLogState.get())

        return FilterReply.NEUTRAL
    }
}
