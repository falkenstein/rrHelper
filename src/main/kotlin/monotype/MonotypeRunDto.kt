package monotype

import data.EType
import species.SpeciesDto

data class MonotypeRunDto(
    val type: EType,
    val starter: SpeciesDto?,
    var randomTeam: List<SpeciesDto>,
)
