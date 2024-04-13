package trainers

data class RawTrainerData(
    val header: String,
    val pokemon: List<RawTrainerPokemonData>,
) {
    override fun toString(): String {
        return "[$header: ${pokemon.joinToString(", ") { it.name }}]"
    }
}
