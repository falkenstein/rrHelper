package com.falkenstein.rrweb.monotype

import data.EGamePhase
import species.SpeciesDto

data class MonotypePhase(
    val phase: EGamePhase,
    val forcedSpecies: List<SpeciesDto>,
    val bannedSpecies: List<SpeciesDto>,
    val teamForPhase: List<SpeciesDto>,
)
