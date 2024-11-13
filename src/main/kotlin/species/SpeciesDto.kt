package species

import data.EGamePhase
import data.EType

data class SpeciesDto(
    val id: Int,
    val evolvesFromId: Int?,
    val name: String,
    val form: String? = null,
    val region: String? = null,
    val types: List<EType>,
    /**
     * The first phase where the species is available since its start.
     */
    val availability: EGamePhase,
    val starter: Boolean = false,
    /**
     * Pokemon with this tag can't be forced into the team before Rocket phase (when all stones become routinely available).
     */
    val stone: Boolean = false,
)
