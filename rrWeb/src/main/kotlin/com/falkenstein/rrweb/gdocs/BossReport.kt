package com.falkenstein.rrweb.gdocs

import data.EBoss
import data.EType
import species.SpeciesDto

data class BossReport(
    val boss: EBoss,
    val type: EType,
    val team: List<SpeciesDto>,
    val difficulty: Int,
) {
    override fun toString(): String {
        return "$type|$boss|$difficulty:[${team.joinToString(",") { nicePrint(it) }}]"
    }

    private fun nicePrint(species: SpeciesDto): String {
        val suffix = species.form ?: species.region
        return species.name + (suffix?.let { "-$it" } ?: "")
    }
}
