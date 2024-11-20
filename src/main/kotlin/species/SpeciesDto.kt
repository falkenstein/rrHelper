package species

import data.EGamePhase
import data.EType

data class SpeciesDto(
    val id: Int,
    val evolvesFromId: Int?,
    val name: String,
    val form: String? = null,
    val region: ERegion? = null,
    val types: List<EType>,
    /**
     * The first phase where the species is available since its start.
     */
    val availability: EGamePhase,
    val starter: Boolean = false,
    val tags: List<ETag>? = emptyList(),
)
