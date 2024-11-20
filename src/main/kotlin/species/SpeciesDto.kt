package species

import data.EGamePhase
import data.EType

data class SpeciesDto(
    val id: Int,
    val evolvesFromId: Int?,
    val name: String,
    val form: String? = null,
    val region: ERegion? = null,
    val types: Set<EType>,
    /**
     * The first phase where the species is available since its start.
     */
    val availability: EGamePhase,
    val starter: Boolean = false,
    val tags: List<ETag>? = emptyList(),
    val statsTotal: Int,
    val tier: Int,
) {
    override fun toString(): String {
        return name + (region ?: "")
    }
}
