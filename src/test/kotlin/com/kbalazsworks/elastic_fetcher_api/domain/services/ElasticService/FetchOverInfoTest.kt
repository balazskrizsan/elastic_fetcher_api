package com.kbalazsworks.elastic_fetcher_api.domain.services.ElasticService

import com.kbalazsworks.elastic_fetcher_api.AbstractTest
import com.kbalazsworks.elastic_fetcher_api.TestLogEntry
import com.kbalazsworks.elastic_fetcher_api.domain.services.ElasticService
import com.kbalazsworks.elastic_fetcher_api.domain.value_objects.LogEntry
import com.kbalazsworks.elastic_fetcher_api.fakeBuilders.InstantFakeBuilder
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class FetchOverInfoTest : AbstractTest() {
    @Test
    fun insertedLogs_returningLogsOver30001() {
        // Arrange
        val now = InstantFakeBuilder().build()
        val testIndex = "test-1"
        recreateIndex(testIndex)

        bulkInsertToElastic(
            testIndex,
            listOf(
                TestLogEntry("App_name", "ERROR", 40000, "struct_message_4", "message_4", now, "trace_id"),
                TestLogEntry("App_name", "INFO", 30000, "struct_message_3", "message_3", now, "trace_id"),
                TestLogEntry("App_name", "DEBUG", 20000, "struct_message_2", "message_2", now, "trace_id"),
                TestLogEntry("App_name", "BETWEEN_INFO_AND_ERROR", 35000, "struct_message_35", "message", now, "trace_id"),
                TestLogEntry("App_name", "ERROR", 40000, "struct_message_4", "message_4", now, "trace_id"),
                TestLogEntry("App_name", "ERROR", 40000, "struct_message_5", "message_5", now, "trace_id"),
                TestLogEntry("App_name", "ERROR", 40000, "struct_message_6", "message_6", now, "trace_id"),
                TestLogEntry("App_name", "ERROR", 40000, "struct_message_7", "message_7", now, "trace_id"),
                TestLogEntry("App_name", "ERROR", 40000, "struct_message_8", "message_8", now, "trace_id"),
            )
        )
        val expected = listOf(
            LogEntry("App_name", "ERROR", 40000, "struct_message_4", "message_4", now, "trace_id"),
            LogEntry("App_name", "BETWEEN_INFO_AND_ERROR", 35000, "struct_message_35", "message", now, "trace_id"),
            LogEntry("App_name", "ERROR", 40000, "struct_message_4", "message_4", now, "trace_id"),
            LogEntry("App_name", "ERROR", 40000, "struct_message_5", "message_5", now, "trace_id"),
            LogEntry("App_name", "ERROR", 40000, "struct_message_6", "message_6", now, "trace_id"),
        )

        // Act
        val actual = createInstance(ElasticService::class.java).fetchOverInfo(testIndex, 5)

        // Assert
        assertThat(actual.mapNotNull { it.source() }).isEqualTo(expected)
    }
}
