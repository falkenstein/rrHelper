package com.falkenstein.rrweb

import data.EGamePhase
import data.EType
import com.falkenstein.rrweb.monotype.MonotypeRunDto
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class MonotypeManagerTest{

    val manager = MonotypeManager()
    val run = MonotypeRunDto(
        id = 0,
        currentPhase = EGamePhase.MISTY,
        type = EType.POISON,
        openingSpecies = emptyList(),
    )

    @Test
    fun testOptionsForPhase() {
        val poisonMisty = manager.getSpeciesAvailableForPhase(EGamePhase.MISTY, run)
        assertTrue(poisonMisty.isNotEmpty())
    }
}
