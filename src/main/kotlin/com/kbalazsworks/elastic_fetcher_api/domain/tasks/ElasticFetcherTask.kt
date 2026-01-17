package com.kbalazsworks.elastic_fetcher_api.domain.tasks

import com.kbalazsworks.elastic_fetcher_api.domain.repositories.semantic_log_classifier.ILogApi
import com.kbalazsworks.elastic_fetcher_api.domain.services.ApplicationPropertiesService.Companion.APP__ELASTIC_FETCHER_TASK_ENABLED
import com.kbalazsworks.elastic_fetcher_api.domain.services.ClassifierService
import com.kbalazsworks.elastic_fetcher_api.domain.services.ElasticService
import com.kbalazsworks.elastic_fetcher_api.domain.services.RunStateService
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty(name = [APP__ELASTIC_FETCHER_TASK_ENABLED])
class ElasticFetcherTask(
    private val runStateService: RunStateService,
    private val elasticService: ElasticService,
    private val logApi: ILogApi,
) {
    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
        private const val INDEX_NAME = "logs-dev"
        private const val INDEX_ERROR_NAME = "alerting-logs-dev"
    }

    private lateinit var thread: Thread
    private lateinit var classifierService: ClassifierService

    @PostConstruct
    fun start() {
        classifierService = ClassifierService(runStateService, elasticService, logApi)

        thread = Thread {
            log.info("Classifier thread loop started")

            while (!Thread.currentThread().isInterrupted) {
                try {
                    val hasClassifiedDocs = classifierService.run(INDEX_NAME, INDEX_ERROR_NAME)
                    if (!hasClassifiedDocs) {
                        Thread.sleep(1_000)
                    }
                } catch (_: InterruptedException) {
                    Thread.currentThread().interrupt()

                    log.info("Classifier thread interrupted, stopping")

                    break
                } catch (e: Exception) {
                    log.error("Unexpected error in classifier thread", e)

                    Thread.sleep(5_000)
                }
            }

            log.info("Classifier thread stopped")
        }.apply {
            name = "classifier-thread"
            isDaemon = true
            start()
        }

        log.info("Classifier thread started")
    }

    @PreDestroy
    fun stop() {
        log.info("Stopping classifier thread")

        if (::thread.isInitialized) {
            thread.interrupt()
        }
    }
}
