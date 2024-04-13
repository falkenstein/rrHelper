package trainers

import data.EGamePhase
import pokemon.PokemonInstance

data class TrainerFullData(
    val name: String,
    val phase: EGamePhase,
    val pokemon: List<PokemonInstance>,
)
