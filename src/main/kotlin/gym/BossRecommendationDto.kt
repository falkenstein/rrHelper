package gym

import pokemon.PokemonInstance

data class BossRecommendationDto(
    val myPokemon: PokemonInstance,
    val bossPokemon: PokemonInstance,
    val resultThis: Float,
    val resultAverage: Float,
) {
    override fun toString(): String {
        return "${bossPokemon.species.niceName}: ${myPokemon.species.niceName}(${myPokemon.ability.substringAfter("ABILITY_")}): " +
            "$resultThis ($resultAverage)"
    }
}
