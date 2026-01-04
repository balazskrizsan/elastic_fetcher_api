package com.kbalazsworks.elastic_fetcher_api.domain.jooq_orm.value_objects

data class TableMeta<ENTITY, TABLE_TYPE>(
    val entityClass: Class<ENTITY>,
    val table: TABLE_TYPE,
)
