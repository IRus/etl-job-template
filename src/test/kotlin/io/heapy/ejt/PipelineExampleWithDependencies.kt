package io.heapy.ejt

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take

suspend fun main() {
    ApplicationFactory().run()
    // Output:
    // Extracted 2000 records. Transformed 2000 records.
}

open class ApplicationFactory {
    open val dataProducerDependency by lazy {
        DataProducerDependency()
    }

    open val dataProducer by lazy {
        DataProducer(dataProducerDependency)
    }

    open val dataProcessorDependency by lazy {
        DataProcessorDependency()
    }

    open val dataProcessor by lazy {
        DataProcessor(dataProcessorDependency)
    }

    open val dataLoader by lazy {
        DataLoader()
    }

    open suspend fun run(): PipelineReport {
        return pipeline<Int, String>()
            .extract(dataProducer::produceData)
            .transform(dataProcessor::processData)
            .load(dataLoader::loadData)
            .run()
    }
}

// S3Client, HttpClient, etc
class DataProducerDependency {
    val message = "hello!"
}

class DataProducer(
    private val dataProducerDependency: DataProducerDependency
) {
    suspend fun produceData(): Flow<Int> {
        return flow {
            var value = 1
            while (true) {
                if (value % 1000 == 0) println(dataProducerDependency.message)
                emit(++value)
            }
        }
    }
}

// Some library for processing data, or maybe some state store
open class DataProcessorDependency {
    open fun mapper(i: Int) = "_$i"
}

class DataProcessor(
    private val dataProcessorDependency: DataProcessorDependency
) {
    fun processData(flow: Flow<Int>): Flow<String> {
        return flow.map { dataProcessorDependency.mapper(it) }
    }
}

class DataLoader {
    suspend fun loadData(flow: Flow<String>) {
        flow.take(2000).collect { println(it) }
    }
}
