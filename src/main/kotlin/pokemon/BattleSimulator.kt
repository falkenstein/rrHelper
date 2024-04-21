package pokemon

import data.DataLoader
import data.EAttribute

class BattleSimulator {

    private val loader = DataLoader()
    private val typeChart = loader.readTypeChart()

    /**
     * Simulates a fight between two species. They will attempt to use their best moves for the situation. Returns a number based on how much percentage is the
     * pokemonA likely to have. If the value is negative, it means pokemonB is likely to survive with the HP amount.
     */
    fun simulateBattle(pokemonA: PokemonInstance, pokemonB: PokemonInstance): Float {
        val bestMoveA = pokemonA.knownMoves.map {
            it to calculateMoveDamage(
                moveData = it,
                userSpeciesData = pokemonA.species,
                targetInstance = pokemonB,
                level = pokemonA.level,
                typeChart = typeChart,
                userAbility = pokemonA.ability,
                weather = null,
                status = null,
                screen = null,
                targetHp = pokemonB.hp,
            )
        }.maxBy { it.second }
        val bestMoveB = pokemonB.knownMoves.map {
            it to calculateMoveDamage(
                moveData = it,
                userSpeciesData = pokemonB.species,
                targetInstance = pokemonA,
                level = pokemonB.level,
                typeChart = typeChart,
                userAbility = pokemonB.ability,
                weather = null,
                status = null,
                screen = null,
                targetHp = pokemonA.hp,
            )
        }.maxBy { it.second }
        val hitsNeededByA = (pokemonB.hp / bestMoveA.second) + 1
        val hitsNeededByB = (pokemonA.hp / bestMoveB.second) + 1
        return if (pokemonA.species.attributes[EAttribute.SPEED]!! > pokemonB.species.attributes[EAttribute.SPEED]!!) {
            if (hitsNeededByA <= hitsNeededByB) {
                // A is faster and wins
                calculateRemainingHp(hitsNeededByA - 1, pokemonA.hp, bestMoveB.second)
            } else {
                // B wins
                -1 * calculateRemainingHp(hitsNeededByB, pokemonB.hp, bestMoveA.second)
            }
        } else if (pokemonB.species.attributes[EAttribute.SPEED]!! > pokemonA.species.attributes[EAttribute.SPEED]!!) {
            if (hitsNeededByA < hitsNeededByB) {
                // A wins, even though B is faster
                calculateRemainingHp(hitsNeededByA, pokemonA.hp, bestMoveB.second)
            } else {
                // B wins
                -1 * calculateRemainingHp(hitsNeededByB - 1, pokemonB.hp, bestMoveA.second)
            }
        } else { // Same speed.
            if (hitsNeededByA < hitsNeededByB) {
                // A wins anyway
                calculateRemainingHp(hitsNeededByA, pokemonA.hp, bestMoveB.second)
            } else if (hitsNeededByA > hitsNeededByB) {
                // B wins even though the speeds are matched.
                -1 * calculateRemainingHp(hitsNeededByB, pokemonB.hp, bestMoveA.second)
            } else {
                // We have a speed tie and hit tie. The fight is therefore random and can go both ways - we return 0
                0F
            }
        }
    }

    /**
     * Calculate percentage of HP that the pokemon will have remaining.
     */
    private fun calculateRemainingHp(hitsTaken: Int, winnerHp: Int, loserDamage: Int): Float {
        val remainingHp = winnerHp - (hitsTaken * loserDamage)
        return remainingHp.toFloat() / winnerHp
    }
}
