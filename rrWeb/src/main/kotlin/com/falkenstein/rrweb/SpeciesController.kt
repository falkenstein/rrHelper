package com.falkenstein.rrweb

import data.EType
import monotype.MonotypeFlow
import monotype.StartingOptionsDto
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class SpeciesController {
    private val monotypeFlow = MonotypeFlow()

    @GetMapping("/run/start")
    fun getStartingOptions(@RequestParam type: String): StartingOptionsDto {
        println("Request for type: $type")
        val eType = EType.valueOf(type.uppercase())
        return monotypeFlow.getStartingOptions(eType)
    }
}
