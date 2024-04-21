package gym

import data.EBoss
import pokemon.PokemonInstance

data class BossResultDto(
    val boss: EBoss,
    val pokemon: PokemonInstance,
    val outcomes: Map<PokemonInstance, Float>,
    val averageResult: Float,
    val bestResult: Float,
    val wins: Int,
    val losses: Int,
) {
    override fun toString(): String {
        return "${pokemon.species.niceName}(${pokemon.ability.substringAfter("ABILITY_")}): ${outcomes.map { "${it.key.species.niceName}: ${it.value}" }}"
    }
}
