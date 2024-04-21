package trainers

import data.EBoss
import data.EGamePhase
import pokemon.PokemonInstance

data class TrainerFullData(
    val boss: EBoss,
    val pokemon: List<PokemonInstance>,
)
