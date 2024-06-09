package org.example

import kotlinx.cinterop.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import platform.posix.*

@Serializable
data class Config(val inputs: List<String>, val outputs: List<OutputConfig>)

@Serializable
data class OutputConfig(val name: String, val pattern: String, val maxLength: Int = 1000, val postProcessors: MutableList<String> = mutableListOf())


class ConfigManager(configFile: String) {
    val config: Config

    init {
        config = Json.decodeFromString<Config>(readFileAsString(configFile));
    }

    @OptIn(ExperimentalForeignApi::class)
    fun readFileAsString(fileName: String): String {
        val file = fopen(fileName, "r") ?: error("Cannot open file: $fileName")
        try {
            fseek(file, 0, SEEK_END)
            val fileSize = ftell(file).toInt()
            fseek(file, 0, SEEK_SET)

            val buffer = ByteArray(fileSize)
            fread(buffer.refTo(0), 1u, fileSize.convert(), file)
            return buffer.toKString()
        } finally {
            fclose(file)
        }
    }
}