package com.example

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.newFixedThreadPoolContext
import kotlinx.coroutines.runBlocking

var sharedCounter = 0

@Synchronized fun incrementCounter() {
    sharedCounter++
}

fun main() = runBlocking {

    val scope = CoroutineScope(newFixedThreadPoolContext(4, "MyThreadPool"))
    val any = Any()

    scope.launch {

        val coroutines = (1..1000).map {
            launch {
                for (i in 1..1000) {
                    incrementCounter()
                }
            }
        }
        coroutines.forEach {
            it.join()
        }
    }.join()

    println("SharedCounter = $sharedCounter")


}