package com.falkenstein.rrweb.monotype

import com.falkenstein.rrweb.dto.SpeciesUsageDto
import data.EGamePhase
import species.SpeciesDto

data class MonotypePhaseDto(
    val phase: EGamePhase,
    val team: List<SpeciesDto>,
    val bossTeams: Map<String, List<SpeciesUsageDto>>,
)
