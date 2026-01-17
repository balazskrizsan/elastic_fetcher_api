package com.kbalazsworks.elastic_fetcher_api.test_services

import co.elastic.clients.elasticsearch.ElasticsearchClient
import com.kbalazsworks.elastic_fetcher_api.domain.factories.ElasticClientFactory
import com.kbalazsworks.elastic_fetcher_api.domain.services.ApplicationPropertiesService
import com.kbalazsworks.elastic_fetcher_api.domain.services.ElasticService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Modifier
import java.util.*

@Service
class ServiceFactory(
    private val applicationPropertiesService: ApplicationPropertiesService,
    private val elasticService: ElasticService,
    private val elasticClientFactory: ElasticClientFactory,
    private val elasticsearchClient: ElasticsearchClient,
) {
    companion object {
        private val logger = LoggerFactory.getLogger(ServiceFactory::class.java)
    }

    val oneTimeMocks = mutableMapOf<String, MutableMap<String, Any>>()

    fun setOneTimeMock(newClass: Class<*>, mock: Any) {
        val className = newClass.name
        // Remove $ ending from mock class name
        val mockClassName = mock::class.java.name.substringBefore('$')

        val mocksForClass = oneTimeMocks.getOrPut(className) { hashMapOf() }
        mocksForClass[mockClassName] = mock
    }

    fun resetMockContainer() {
        oneTimeMocks.clear()
    }

    fun <T> createInstance(clazz: Class<T>): T {
        return try {
            logger.info("Creating object from: {}", clazz.name)

            createInstanceLogic(clazz)
        } catch (e: InvocationTargetException) {
            throw ServiceFactoryException("Service instance creation error: ${e.message}", e)
        } catch (e: InstantiationException) {
            throw ServiceFactoryException("Service instance creation error: ${e.message}", e)
        } catch (e: IllegalAccessException) {
            throw ServiceFactoryException("Service instance creation error: ${e.message}", e)
        }
    }

    private fun <T> createInstanceLogic(clazz: Class<T>): T {
        val diFields = getPrivateFinalNotStaticFields(clazz)
        diFields.map { f -> logger.info("Object DI fields: {}", f) }

        val constructor: Constructor<T>?
        try {
            constructor = getDefaultDiConstructor(clazz, diFields)
        } catch (e: NoSuchElementException) {
            throw ServiceFactoryException("Service factory instance creation error: ${clazz.name}", e)
        }

        val diObjects: MutableList<Any> = ArrayList()
        for (f in diFields) {
            try {
                val dependency = getDiObject(clazz, f, getField(f))
                diObjects.add(dependency)
            } catch (e: NoSuchFieldException) {
                val dependency = getDependency(clazz, f.type, createInstance(f.type))
                diObjects.add(getDependency(clazz, f.type, dependency))
            }
        }

        return constructor.newInstance(*diObjects.toTypedArray())
    }

    private fun <T> getDiObject(clazz: Class<T>, f: Field, fieldObject: Field): Any {
        try {
            return getDependency(clazz, f.type, fieldObject[this])
        } catch (e: IllegalAccessException) {
            throw RuntimeException(e)
        }
    }

    private fun getField(f: Field): Field {
        return javaClass.getDeclaredField(f.name)
    }

    protected fun <T> getDependency(newClass: Class<*>, dependencyClass: Class<*>, diObject: T): T {
        val newClassFound = oneTimeMocks.getOrDefault(newClass.name, null)
        if (newClassFound != null && newClassFound.isNotEmpty()) {
            val dependencyClassFound = newClassFound.getOrDefault(dependencyClass.name, null) as T?
            if (dependencyClassFound != null) {
                logger.info("DI Loaded from OneTimeMock: {}", dependencyClassFound.javaClass.name)

                newClassFound.remove(dependencyClass.name)

                return dependencyClassFound
            }
        }

        return diObject
    }

    private fun <T> getPrivateFinalNotStaticFields(clazz: Class<T>): List<Field> {
        return Arrays.stream(clazz.declaredFields).filter { f: Field ->
            val modifiers = f.modifiers
            Modifier.isPrivate(modifiers) && Modifier.isFinal(modifiers) && !Modifier.isStatic(modifiers)
        }.toList()
    }

    fun <T> getDefaultDiConstructor(clazz: Class<T>, diFields: List<Field>): Constructor<T> {
        val diFieldFqns = diFields.map { it.type.name }

        return clazz.constructors.filterIsInstance<Constructor<T>>().first { constructor ->
            val constructorParamFqns = constructor.parameterTypes.map { it.name }

            constructorParamFqns == diFieldFqns
        }
    }
}