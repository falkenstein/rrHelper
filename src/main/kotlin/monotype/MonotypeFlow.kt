package monotype

import data.DataLoader
import data.EGamePhase
import data.EType
import species.SpeciesDto

class MonotypeFlow {

    private val loader = DataLoader()
    private val allSpecies = loader.readFullSpeciesDataFromJson()

    fun progressRun(runDto: MonotypeRunDto) {

    }

    fun species(id: Int): SpeciesDto {
        return allSpecies.first { it.id == id }
    }

    fun species(name: String): SpeciesDto {
        return allSpecies.firstOrNull() { it.name.equals(name, ignoreCase = true) } ?: error("Species $name not found!")
    }

    /**
     * Lists species that can be forced or banned in the given phase.
     */
    fun getSpeciesAvailableForPhase(phase: EGamePhase, runDto: MonotypeRunDto, ignoreTiers: Boolean = false): List<SpeciesDto> {
        val potentialSpecies = allSpecies.filter {
            it.types.contains(runDto.type) && it.availability.levelCap <= phase.levelCap
                    && (!it.starter || phase.ordinal >= EGamePhase.ROCKET.ordinal) // Starters only become available from the Rocket phase
        }
        val withExtras = mutableListOf<SpeciesDto>()
        withExtras.addAll(potentialSpecies)
        withExtras.addAll(runDto.randomTeam.map { getHighestEvolutionForPhase(it, phase, runDto.type) }.flatten())
        runDto.starter?.let { withExtras.addAll(getHighestEvolutionForPhase(it, phase, runDto.type)) }
        return withExtras.distinct()
            .filter { it.tier >= phase.minTier || ignoreTiers } // Tiers can be ignored - used by the bans selection.
            .filter { spec -> potentialSpecies.none { it.evolvesFromId == spec.id } } // TODO: What about Scyther?
    }

    /**
     * Retrieves the highest possible evolution for the given species, phase and type - with regards to forced/banned availability.
     */
    fun getHighestEvolutionForPhase(spec: SpeciesDto, phase: EGamePhase, type: EType): List<SpeciesDto> {
        val baseList = mutableListOf(spec)
        allSpecies.forEach { searched ->
            if (searched.availability.levelCap <= phase.levelCap && searched.types.contains(type) && baseList.any { searched.evolvesFromId == it.id }) {
                baseList.add(searched)
            }
        }
        allSpecies.forEach { searched -> // Intentionally duplicated, makes sure we find even evolution that are not put in correct order.
            if (searched.availability.levelCap <= phase.levelCap && searched.types.contains(type) && baseList.any { searched.evolvesFromId == it.id }) {
                baseList.add(searched)
            }
        }
        return baseList.distinct().filter { sp -> baseList.none { it.evolvesFromId == sp.id } }
    }

    /**
     * Returns list of pokemon that must be used on the team for the given phase.
     */
    fun generateForcedPokemon(runDto: MonotypeRunDto, phase: EGamePhase): List<SpeciesDto> {
        val options = getSpeciesAvailableForPhase(phase, runDto).toMutableList()
        val forcedTeam = mutableListOf<SpeciesDto>()
        for (i in 1..4) {
            val picked = options.random()
            forcedTeam.add(picked)
            options.remove(picked)
            options.removeIf { it.types == picked.types }
        }
        return forcedTeam
    }

    /**
     * Returns list of pokemon that must NOT be used on the team for the given phase.
     */
    fun generateBannedPokemon(
        runDto: MonotypeRunDto,
        phase: EGamePhase,
        /**
         * Pokemon that already are forced onto the team - banning them would make no sense at all.
         */
        alreadyForced: List<SpeciesDto>,
    ): List<SpeciesDto> {
        val allOptions = getSpeciesAvailableForPhase(phase, runDto, true)
        val tieredOptions = getSpeciesAvailableForPhase(phase, runDto, false).toMutableList() - alreadyForced
        val maxBanCount = minOf(allOptions.size / 3, phase.ordinal + 4)
        return tieredOptions.shuffled().take(maxBanCount)
    }
}