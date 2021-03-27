package io.heapy.ejt

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import kotlin.properties.Delegates
import kotlin.reflect.KType

interface Pipeline<ET : Any, TT : Any> {
    /**
     * Extract data from datasource as is.
     *  - For REST API it will be just ByteArray or String.
     *      Each request mapped to single element in flow
     *  - For some storage like S3 it will be ByteArray
     *      with metadata (filename, modification time, etc)
     */
    fun extract(
        extractor: suspend () -> Flow<ET>,
    ): Pipeline<ET, TT>

    // map(String -> T) and validate
    // validator with different strategies for each field:
    //  on validation error:
    //    fail - fail pipeline
    //    warn - warn message
    //    warn_drop - warn message and drop element
    // calculate metrics

    fun transform(
        transformer: suspend (Flow<ET>) -> Flow<TT>,
    ): Pipeline<ET, TT>

    fun load(
        loader: suspend (Flow<TT>) -> Unit,
    ): Pipeline<ET, TT>

    suspend fun run(): PipelineReport
}

// Statistics by step
// Realtime
class PipelineReport

/**
 * This pipeline serializes results of each step to S3 storage.
 * So it's possible to run each step in single process, or rerun some steps.
 */
private class SerializingPipeline<ET : Any, TT : Any>(
    private val extractType: KType, // typeOf<ET>()
    private val transformType: KType, // typeOf<TT>()
) : Pipeline<ET, TT> {
    override fun extract(extractor: suspend () -> Flow<ET>): Pipeline<ET, TT> {
        TODO("Not yet implemented")
    }

    override fun transform(transformer: suspend (Flow<ET>) -> Flow<TT>): Pipeline<ET, TT> {
        TODO("Not yet implemented")
    }

    override fun load(loader: suspend (Flow<TT>) -> Unit): Pipeline<ET, TT> {
        TODO("Not yet implemented")
    }

    override suspend fun run(): PipelineReport {
        TODO("Not yet implemented")
    }
}

/**
 * Simple pipeline that just run all steps.
 */
private class SimplePipeline<ET : Any, TT : Any> : Pipeline<ET, TT> {
    private var _extractor: suspend () -> Flow<ET> by Delegates.notNull()
    private var _transformer: suspend (Flow<ET>) -> Flow<TT> by Delegates.notNull()
    private var _loader: suspend (Flow<TT>) -> Unit by Delegates.notNull()

    override fun extract(extractor: suspend () -> Flow<ET>): Pipeline<ET, TT> {
        _extractor = extractor
        return this
    }

    override fun transform(transformer: suspend (Flow<ET>) -> Flow<TT>): Pipeline<ET, TT> {
        _transformer = transformer
        return this
    }

    override fun load(loader: suspend (Flow<TT>) -> Unit): Pipeline<ET, TT> {
        _loader = loader
        return this
    }

    override suspend fun run(): PipelineReport {
        var extractedCount = 0
        val extracted = _extractor().onEach { extractedCount++ }
        var transformedCount = 0
        val transformed = _transformer(extracted).onEach { transformedCount++ }
        _loader(transformed)
        println("Extracted $extractedCount records. Transformed $transformedCount records.")
        return PipelineReport()
    }
}

fun <ET : Any, TT : Any> pipeline(): Pipeline<ET, TT> {
    return SimplePipeline()
}

