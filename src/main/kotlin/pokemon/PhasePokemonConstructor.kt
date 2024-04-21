package pokemon

import data.EAttribute
import data.EGamePhase
import data.EType
import data.TypeChart
import moves.MoveAvailabilityData
import species.FullSpeciesData

class PhasePokemonConstructor {

    /**
     * Shows all possible options for the given game phase.
     */
    fun setupPokemonForPhase(
        type: EType,
        phase: EGamePhase,
        speciesData: List<FullSpeciesData>,
        allMoveData: List<MoveAvailabilityData>,
        typeChart: TypeChart,
    ): List<PokemonInstance> {
        val availableSpecies = speciesData
            .filter { it.types.contains(type) && it.available.levelCap <= phase.levelCap && !it.mega }
            .groupBy { it.evolutionLine.last() }
            .map { evos -> evos.value.maxBy { it.evolutionLine.indexOf(it.name) } }
            .plus(speciesData.filter { it.types.contains(type) && it.available.levelCap <= phase.levelCap && it.mega })
        return availableSpecies.map {
            setupPokemonVariationsForPhase(
                species = it,
                phase = phase,
                allMoveData = allMoveData,
                typeChart = typeChart,
            )
        }.flatten()
    }

    /**
     * Prepares the given species with regard to the given phase.
     */
    private fun setupPokemonVariationsForPhase(
        species: FullSpeciesData,
        phase: EGamePhase,
        allMoveData: List<MoveAvailabilityData>,
        typeChart: TypeChart,
    ): List<PokemonInstance> {
        val possibleMoves = allMoveData.filter { move ->
            val levelUpMove = species.levelUpMoves.find { it.first.equals(move.move.name, ignoreCase = true) }
            val tmMove = species.tmMoves.find { it.equals(move.move.name, ignoreCase = true) }
            (levelUpMove != null && levelUpMove.second <= phase.levelCap) || (move.tmPhase != null && tmMove != null && move.tmPhase.levelCap <= phase.levelCap)
        }.map { it.move }
        return species.abilities.filter { it != "ABILITY_NONE" }.map { ability ->
            val selectedDamageMoves = possibleMoves
                .filter { it.power > 0 }
                .groupBy { it.type }
                .map { typeMoves -> typeMoves.value.maxBy { calculateMoveDamage(
                    moveData = it,
                    userSpeciesData = species,
                    targetInstance = null,
                    level = phase.levelCap,
                    typeChart = typeChart,
                    userAbility = ability,
                ) } }
            PokemonInstance(
                species = species,
                level = phase.levelCap,
                ability = ability,
                hp = calculateHp(species.attributes[EAttribute.HP]!!, phase.levelCap),
                knownMoves = selectedDamageMoves,
            )
        }
    }
}