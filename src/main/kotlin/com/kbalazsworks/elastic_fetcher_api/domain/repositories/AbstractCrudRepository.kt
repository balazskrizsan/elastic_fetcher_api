package com.kbalazsworks.elastic_fetcher_api.domain.repositories

import com.kbalazsworks.elastic_fetcher_api.domain.db.tables.references.RUN_STATES
import com.kbalazsworks.elastic_fetcher_api.domain.jooq_orm.exceptions.OrmException
import com.kbalazsworks.elastic_fetcher_api.domain.jooq_orm.value_objects.TableMeta
import com.kbalazsworks.elastic_fetcher_api.domain.services.JooqService
import org.jooq.TableField
import org.jooq.UpdatableRecord
import org.jooq.impl.DSL
import org.jooq.impl.TableImpl
import org.springframework.stereotype.Repository
import java.time.OffsetDateTime

@Repository
@Suppress("FunctionName")
abstract class AbstractCrudRepository<ENTITY, TABLE_TYPE : TableImpl<RECORD_TYPE>, RECORD_TYPE : UpdatableRecord<*>>
    (jooqService: JooqService) : AbstractRepository(jooqService) {
    abstract val tableMeta: TableMeta<ENTITY, TABLE_TYPE>

    fun _save(entity: ENTITY): ENTITY = getCtx()
        .newRecord(tableMeta.table, entity).also { it.store() }
        .into(tableMeta.entityClass)

    fun _saveOnDuplicateKeyUpdate(entity: ENTITY): ENTITY {
        val record = getCtx().newRecord(tableMeta.table, entity)

        val result = getCtx().insertInto(tableMeta.table)
            .set(record)
            .onConflict(getIdField())
            .doUpdate()
            .set(record)
            .returning()
            .fetchOne()!!

        return result.into(tableMeta.entityClass)
    }

    fun _fetchOne(): ENTITY = getCtx()
        .selectFrom(tableMeta.table)
        .limit(1)
        .fetchOneInto(tableMeta.entityClass)
        ?: throwNotFound()

    fun _getOneById(id: Long): ENTITY = getCtx()
        .selectFrom(tableMeta.table)
        .where(getIdField().eq(id))
        .fetchOneInto(tableMeta.entityClass)
        ?: throwNotFound(tableMeta.table.name, id)

    fun _getByIdsKeepOrder(ids: List<Long>): MutableList<ENTITY> {
        val unnestedIds = DSL
            .unnest(DSL.array(ids.toTypedArray()))
            .withOrdinality()
            .asTable("id_list", "id", "ordering")

        return getCtx()
            .select(tableMeta.table.asterisk())
            .from(unnestedIds)
            .join(tableMeta.table)
            .on(getIdField().eq(unnestedIds.field("id", Long::class.java)))
            .orderBy(unnestedIds.field("ordering", Int::class.java))
            .fetchInto(tableMeta.entityClass)
    }

    fun _delete(id: Long) = getCtx().deleteFrom(tableMeta.table).where(getIdField().eq(id)).execute()

    private fun getIdField() = tableMeta.table.primaryKey?.fields?.first() as TableField<RECORD_TYPE, Any?>

    private fun throwNotFound(tableName: String = "undefined", id: Long? = null): Nothing =
        throw OrmException("Record not found; table: $tableName; id: $id")
}
