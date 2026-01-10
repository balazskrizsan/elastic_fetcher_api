package com.kbalazsworks.elastic_fetcher_api.domain.repositories.semantic_log_classifier

import com.pgvector.PGvector
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.service.annotation.HttpExchange
import org.springframework.web.service.annotation.PostExchange
import java.time.Instant

@HttpExchange("/v1/log")
interface ILogApi {
    @PostExchange
    fun post(@RequestBody data: Request): String

    @PostExchange("/classification")
    fun postClassification(@RequestBody data: Request): ResponseEntity<ResponseData<Response>>

    data class Request(val level: String, val structuredMessage: String, val message: String, val timestamp: Instant)
    data class Response(val vectorStoreXSimilarity: VectorStoreXSimilarity, val originalRequest: Request)
    data class ResponseData<T>(val data: T?, val success: Boolean, val errorCode: Int)
    data class VectorStoreXSimilarity(val similarity: Float, val vectorStoreX: VectorStoreX)
    data class VectorStoreX(
        val id: Long?,
        val vectorModelId: Long,
        val contextInfo: Map<String, String>,
        val embedding: PGvector
    )
}
