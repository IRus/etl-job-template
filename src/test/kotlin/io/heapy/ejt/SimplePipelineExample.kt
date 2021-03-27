package io.heapy.ejt

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take

suspend fun main() {
    pipeline<Int, String>()
        .extract(::produceData)
        .transform(::processData)
        .load(::loadData)
        .run()

    // Output:
    // Extracted 2000 records. Transformed 2000 records.
}

suspend fun produceData(): Flow<Int> {
    return flow {
        var value = 0
        while (true) {
            emit(++value)
            if (value % 1000 == 0) println("Emit")
        }
    }
}

fun processData(flow: Flow<Int>): Flow<String> {
    return flow.map { it.toString() }
}

suspend fun loadData(flow: Flow<String>) {
    flow.take(2000).collect { println(it) }
}
