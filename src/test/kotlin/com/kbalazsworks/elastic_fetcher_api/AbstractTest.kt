package com.kbalazsworks.elastic_fetcher_api

import co.elastic.clients.elasticsearch.ElasticsearchClient
import co.elastic.clients.elasticsearch._types.Refresh
import com.kbalazsworks.elastic_fetcher_api.domain.services.ApplicationPropertiesService
import com.kbalazsworks.elastic_fetcher_api.test_factories.ElasticClientFactory
import com.kbalazsworks.elastic_fetcher_api.test_services.ServiceFactory
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.params.provider.Arguments
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import java.util.function.Consumer
import java.util.stream.Stream

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ContextConfiguration(classes = [ElasticFetcherApiApplication::class])
abstract class AbstractTest() {
    companion object {
        @JvmStatic
        protected fun <T> providerMap(vararg dataList: T): Stream<Arguments> = dataList.map { testData ->
            Arguments.of(testData)
        }.stream()
    }

    @Autowired
    private lateinit var serviceFactory: ServiceFactory

    @Autowired
    protected lateinit var ap: ApplicationPropertiesService

    @AfterEach
    fun after() {
        serviceFactory.resetMockContainer()
    }

    val elasticClient: ElasticsearchClient by lazy {
        ElasticClientFactory().create(ap.appElasticHost, ap.appElasticPort, ap.appElasticScheme)
    }

    protected fun <T> createInstance(clazz: Class<T>, mocks: List<Any>): T {
        mocks.forEach(Consumer { m: Any -> setOneTimeMock(clazz, m) })

        return createInstance(clazz)
    }

    protected fun <T> createInstance(clazz: Class<T>, mock: Any): T {
        setOneTimeMock(clazz, mock)

        return createInstance(clazz)
    }

    protected fun <T> createInstance(clazz: Class<T>): T {
        return serviceFactory.createInstance(clazz)
    }

    protected fun setOneTimeMock(newClass: Class<*>, mock: Any) {
        serviceFactory.setOneTimeMock(newClass, mock)
    }

    fun recreateIndex(index: String) {
        val exists = elasticClient.indices().exists { it.index(index) }.value()

        if (exists) {
            elasticClient.indices().delete { d ->
                d.index(index)
            }
        }

        elasticClient.indices().create { c ->
            c.index(index)
            c.mappings { m ->
                m.properties("@timestamp") { p ->
                    p.date { d -> d }
                }
                m.properties("message") { p ->
                    p.text { t -> t }
                }
            }
        }
    }

    fun bulkInsertToElastic(index: String, entries: List<TestLogEntry>) {
        if (entries.isEmpty()) return

        val bulkResponse = elasticClient.bulk { b ->
            entries.forEach { entry ->
                b.operations {
                    it.index { idx ->
                        idx.index(index).document(entry)
                    }
                }
            }
            b.refresh(Refresh.True)
        }

        bulkResponse.takeIf { it.errors() }?.let {
            println("Bulk sending error items: " + it.items())
        }
    }
}
