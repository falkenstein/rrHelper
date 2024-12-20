package com.falkenstein.rrweb.dto

import species.SpeciesDto

data class PhaseSetupDto(
    val phase: String,
    val type: String,
    val forced: List<SpeciesDto>,
    val banned: List<SpeciesDto>,
    val options: List<SpeciesDto>,
    /**
     * How many species must be selected by the player.
     */
    val selectCount: Int,
)
