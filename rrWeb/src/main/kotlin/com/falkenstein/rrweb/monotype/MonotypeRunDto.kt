package com.falkenstein.rrweb.monotype

import data.EGamePhase
import data.EType

data class MonotypeRunDto(
    val id: Int,
    var currentPhase: EGamePhase,
    val type: EType,
    /**
     * The six random species from the random6 code plus potential starter. Only lists names - better than the whole species dto.
     */
    var openingSpecies: List<String>,
    val phases: MutableList<MonotypePhaseDto>,
)
