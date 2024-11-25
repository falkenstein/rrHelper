package monotype

import data.EGamePhase
import data.EType
import species.SpeciesDto

data class MonotypeRunDto(
    val id: Int,
    var currentPhase: EGamePhase,
    val type: EType,
    val starter: SpeciesDto?,
    var randomTeam: List<SpeciesDto>,
)
