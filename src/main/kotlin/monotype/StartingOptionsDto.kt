package monotype

import species.SpeciesDto

data class StartingOptionsDto(
    val possibleStarters: List<SpeciesDto>,
    val possibleRandomSix: List<SpeciesDto>,
)
