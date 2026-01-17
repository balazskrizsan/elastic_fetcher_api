package com.kbalazsworks.elastic_fetcher_api.fakeBuilders

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

class InstantFakeBuilder {
    private var year: Int = 2024
    private var month: Int = 1
    private var day: Int = 1
    private var hour: Int = 12
    private var minute: Int = 0
    private var second: Int = 0
    private var zone: ZoneId = ZoneOffset.UTC

    fun year(value: Int) = apply { year = value }
    fun month(value: Int) = apply { month = value }
    fun day(value: Int) = apply { day = value }
    fun hour(value: Int) = apply { hour = value }
    fun minute(value: Int) = apply { minute = value }
    fun second(value: Int) = apply { second = value }
    fun zone(value: ZoneId) = apply { zone = value }

    fun build(): Instant = LocalDateTime
            .of(year, month, day, hour, minute, second)
            .atZone(zone)
            .toInstant()
}
