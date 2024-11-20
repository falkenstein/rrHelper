package monotype

import data.EGamePhase
import data.EType
import species.SpeciesDto

data class MonotypeRunDto(
    var currentPhase: EGamePhase,
    val type: EType,
    val starter: SpeciesDto?,
    var randomTeam: List<SpeciesDto>,
)
