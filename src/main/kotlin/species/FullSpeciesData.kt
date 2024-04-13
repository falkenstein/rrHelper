package species

import data.EAttribute
import data.EGamePhase
import data.EType

data class FullSpeciesData(
    val name: String,
    val id: Int,
    val attributes: Map<EAttribute, Int>,
    val abilities: List<String>,
    val types: List<EType>,
    val levelUpMoves: List<Pair<String, Int>>,
    val tmMoves: List<String>,
    val eggMoves: List<String>,
    val tutorMoves: List<String>,
    val evolutionLine: List<String>,
    val available: EGamePhase,
    val niceName: String,
    val region: String?,
    val form: String?,
)
