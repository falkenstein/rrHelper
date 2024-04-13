package gdocs

import data.EBoss
import data.EType
import species.FullSpeciesData

data class BossReport(
    val boss: EBoss,
    val type: EType,
    val team: List<FullSpeciesData>,
    val difficulty: Int,
) {
    override fun toString(): String {
        return "$type|$boss|$difficulty:[${team.joinToString(",") { nicePrint(it) }}]"
    }

    private fun nicePrint(species: FullSpeciesData): String {
        val suffix = species.form ?: species.region
        return species.niceName + (suffix?.let { "-$it" } ?: "")
    }
}
