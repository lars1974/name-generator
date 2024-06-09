package org.example

object Outputter {
    fun output(outputs: Map<String, String>, singleOutput: String?){
        if(singleOutput != null){
            println(outputs[singleOutput])
            return
        }
        outputs.forEach { (k, v) -> println("$k = $v") }
    }
}