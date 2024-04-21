package gym

import data.DataLoader
import data.EBoss
import data.EType
import pokemon.BattleSimulator
import pokemon.PhasePokemonConstructor

class GymLeaderBattleCalculator {

    private val simulator = BattleSimulator()
    private val phaseConstructor = PhasePokemonConstructor()
    private val loader = DataLoader()

    fun calculateBestTeamForGymLeader(boss: EBoss, type: EType): List<BossRecommendationDto> {
        val moves = loader.readMovesData()
        val species = loader.readSpeciesData()
        val typeChart = loader.readTypeChart()
        val possiblePokemon = phaseConstructor.setupPokemonForPhase(type, boss.phase, species, moves, typeChart)
        val bossTeam = loader.readTrainers(species, moves).first { it.boss == boss }
        println(bossTeam.pokemon.filter { it.knownMoves.size < 4 })
        val battleOptions = possiblePokemon.map { myPokemon ->
            val outcomes = bossTeam.pokemon.associateWith { bossPokemon ->
                simulator.simulateBattle(myPokemon, bossPokemon)
            }
            BossResultDto(
                boss = boss,
                pokemon = myPokemon,
                outcomes = outcomes,
                averageResult = outcomes.values.average().toFloat(),
                bestResult = outcomes.values.max(),
                wins = outcomes.values.count { it > 0 },
                losses = outcomes.values.count { it < 0 },
            )
        }.toMutableList()
        return bossTeam.pokemon.map { bossPokemon ->
            bossPokemon to battleOptions.mapNotNull { it.outcomes[bossPokemon] }.average() // Determine the hardest opponent.
        }
            .sortedBy { it.second }
            .map { bossPokemon ->
                val bestResult = battleOptions.maxBy { it.outcomes[bossPokemon.first]!! }
                val recommendation = BossRecommendationDto(
                    myPokemon = bestResult.pokemon,
                    bossPokemon = bossPokemon.first,
                    resultThis = bestResult.outcomes[bossPokemon.first]!!,
                    resultAverage = bestResult.averageResult,
                )
                battleOptions.removeAll { it.pokemon.species == bestResult.pokemon.species }
                if (bestResult.pokemon.species.mega) {
                    battleOptions.removeAll { it.pokemon.species.mega } // Only one mega on the team.
                }
                recommendation
            }
    }

}