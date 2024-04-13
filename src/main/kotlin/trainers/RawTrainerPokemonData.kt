package trainers

data class RawTrainerPokemonData(
    val name: String,
    val ability: String,
    val nature: String,
    val moves: List<String>,
)
