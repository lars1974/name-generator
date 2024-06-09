package org.example

import kotlin.math.min

class Processor(val config: Config) {
    fun process(inputMap: MutableMap<String, String>): MutableMap<String, String>{
        val outputMap = mutableMapOf<String, String>()

        config.outputs.forEach { output ->  outputMap[output.name] = postProcess(generateName(inputMap.toMutableMap(), output), output) }

        return outputMap
    }

    fun postProcess(value: String, output: OutputConfig): String {
       var result = value
       output.postProcessors.forEach { postProcessor ->
           when(postProcessor.lowercase()){
               "uppercase" -> result = result.uppercase()
               "lowercase" -> result = result.lowercase()
               "universal" -> result = result.replace( "[^a-zA-Z0-9]".toRegex(), "-")
           }
       }
        return result
    }

    companion object {
        fun generateName(inputMap: MutableMap<String, String>, output: OutputConfig): String {
            val lengthMap = createLengthMap(output.pattern)

            // Trim tokens
            lengthMap.forEach { (k, v) ->
                inputMap[k]?.let {
                    inputMap[k] = it.substring(0, min(v, it.length))
                }
            }

            inputMap["hash"] = generateHash(inputMap.values.joinToString("-"), lengthMap["hash"] ?: 8)

            val result = fillTemplate(output.pattern, inputMap)

            return if(result.length < output.maxLength) result else { // if the result is longer than the maxLength then we need to trim it
                val longestToken = inputMap.entries.maxBy { it.value.length }
                val allowedTokenLength = longestToken.value.length - (result.length - output.maxLength)
                inputMap[longestToken.key] = longestToken.value.substring(0, allowedTokenLength)
                fillTemplate(output.pattern, inputMap)
            }
        }

        @OptIn(ExperimentalStdlibApi::class)
        fun generateHash(input: String, length: Int): String {
            val hash = input.hashCode().toHexString()

            return hash.substring(0, min(length, hash.length))
        }

        fun fillTemplate(template: String, valuesMap: Map<String, String>): String {
            return "\\{(\\w+)(?::\\d+)?}".toRegex().replace(template) { matchResult ->
                val key = matchResult.groupValues[1]
                valuesMap[key] ?: matchResult.value
            }
        }

        // We have templates like {branch:50}-{hash:5} and we need to extract length values
        fun createLengthMap(input: String): Map<String, Int> {
            val matches = "\\{(\\w+)(?::(\\d+))?}".toRegex().findAll(input)
            val resultMap = mutableMapOf<String, Int>()

            matches.forEach { match ->
                val (key, value) = match.destructured
                resultMap[key] = value.toIntOrNull() ?: 250
            }
            return resultMap
        }
    }
}