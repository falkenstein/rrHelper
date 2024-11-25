package com.falkenstein.rrweb

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class RrWebApplication

fun main(args: Array<String>) {
    runApplication<RrWebApplication>(*args)
}
