package pokemon

import data.EType
import moves.MoveData
import species.FullSpeciesData

data class PokemonInstance(
    val species: FullSpeciesData,
    val level: Int,
    val ability: String,
    val hp: Int,
    val knownMoves: List<MoveData>,
    val hiddenPowerType: EType? = null,
) {
    override fun toString(): String {
        return "[$level: ${species.niceName}: $ability, HP:$hp, moves: ${knownMoves.joinToString(", ") { it.name }}]"
    }
}
