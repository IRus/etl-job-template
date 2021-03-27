package io.heapy.ejt

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

class PipelineExampleWithDependenciesTest {

    @Test
    fun test(): Unit = runBlocking {
        // Example of overriding dependency for test
        class TestFactory : ApplicationFactory() {
            override val dataProcessorDependency by lazy {
                object : DataProcessorDependency() {
                    override fun mapper(i: Int): String {
                        return "test_$i"
                    }
                }
            }
        }

        TestFactory().run()
    }
}
