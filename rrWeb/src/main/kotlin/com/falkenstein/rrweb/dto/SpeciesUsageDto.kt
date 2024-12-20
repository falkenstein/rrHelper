package com.falkenstein.rrweb.dto

import species.SpeciesDto

data class SpeciesUsageDto(
    val species: SpeciesDto,
    val state: EUsageState,
    /**
     * 1 through 5 to mark how useful the team member was in the fight.
     */
    val usefulness: Int,
    /**
     * True if the pokemon was in the opening slot. In double and back-to-back battles there will be two openers.
     */
    val opener: Boolean,
)
