import org.example.Config
import org.example.OutputConfig
import org.example.Processor
import kotlin.test.Test

class ProccessorTest {
    @Test
    fun test() {
        val config = Config(mutableListOf(), mutableListOf())

        val processor = Processor(config)


    }

    @Test
    fun test1(){
        val m = mutableMapOf("test" to "test10", "branch" to "hello-little-branch", "hash" to "12345")
        println(Processor.generateName(m,OutputConfig("out", "{test}-{hash:5}-sdf{branch:20}", 30, mutableListOf())))
    }



}