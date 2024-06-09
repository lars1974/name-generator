import org.example.ConfigManager
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.options.varargValues
import org.example.Outputter
import org.example.Processor

class Main : CliktCommand() {

    val inputsArgs by option("-i", "--input").varargValues().required()

    val configArg by option("-c", "--config").default("config.json")

    val singleOutput by option("-s", "--singleOutput")


    lateinit var configManager: ConfigManager

    override fun run() {
        configManager = ConfigManager(configArg)

        val inputMap = mutableMapOf<String, String>()

        inputsArgs.forEach { parseInput(it).also { input -> inputMap[input.first] = input.second } }

        val outputs = Processor(configManager.config).process(inputMap)

        Outputter.output(outputs, singleOutput)
    }

    fun parseInput(input: String): Pair<String, String> {
        input.split("=").also { return it[0] to it[1] }
    }
}

fun main(args: Array<String>) = Main().main(args)

