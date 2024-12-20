package com.falkenstein.rrweb.dto

import species.SpeciesDto

data class SelectedTeamDto(
    val runId: Int,
    val team: List<SpeciesDto>,
)
