package com.falkenstein.rrweb

import com.falkenstein.rrweb.data.DataLoader
import com.falkenstein.rrweb.dto.BasicRunDataDto
import com.falkenstein.rrweb.dto.PhaseSetupDto
import data.EGamePhase
import data.EType
import com.falkenstein.rrweb.monotype.MonotypeRunDto
import com.falkenstein.rrweb.monotype.SearchResultsDto
import com.falkenstein.rrweb.monotype.StartingOptionsDto
import org.springframework.stereotype.Service
import species.SpeciesDto

@Service
class MonotypeManager {

    private val loader = DataLoader()
    private val allSpecies = loader.readFullSpeciesDataFromJson()
    private val allRuns = loader.loadRuns().toMutableList()

    /**
     * Retrieves species as per type and search. Can't return results from outside of the searched type.
     */
    fun getSearchedSpecies(type: EType, search: String): SearchResultsDto {
        return SearchResultsDto(
            results = allSpecies.filter { it.types.contains(type) && it.name.contains(search, ignoreCase = true) }
        )
    }

    fun getStartingOptions(type: EType): StartingOptionsDto {
        val starters = allSpecies.filter { it.starter && it.evolvesFromId == null && it.types.contains(type) }
        val randomSix = allSpecies.filter { it.types.contains(type) && it.evolvesFromId == null && it.statsTotal < 550 }
        return StartingOptionsDto(starters, randomSix)
    }

    /**
     * Starts a new run based on the input data and returns its ID.
     */
    fun startNewRun(
        selectedNames: List<String>,
        type: EType,
    ): Int {
        val openingSpecies = selectedNames.map { name ->
            allSpecies.first { it.name == name }
        } // Validates the input - makes sure that the input species really exist.
        val assignedId = (allRuns.maxOfOrNull { it.id } ?: 0) + 1
        val runDto = MonotypeRunDto(
            id = assignedId,
            currentPhase = EGamePhase.MISTY,
            type = type,
            openingSpecies = openingSpecies.map { it.name }, // Maps the species back to the name.
            phases = mutableListOf(),
        )
        allRuns.add(runDto)
        loader.saveRuns(allRuns)
        return assignedId
    }

    /**
     * Used to get all data for the runs.
     */
    fun getBasicRunData(): List<BasicRunDataDto> {
        return allRuns.map {
            BasicRunDataDto(
                id = it.id,
                type = it.type.name,
                phase = it.currentPhase.name,
            )
        }
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
        withExtras.addAll(runDto.openingSpecies.map { name -> getHighestEvolutionForPhase(allSpecies.first { it.name == name }, phase, runDto.type) }.flatten())
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

    /**
     * Provides forces and bans for the given run.
     */
    fun setupRunPhase(runId: Int): PhaseSetupDto {
        val run = allRuns.first { it.id == runId }
        val phase = run.currentPhase // We always generate for the current phase - presuming that phase has been moved before.
        val forcedSpecies = generateForcedPokemon(run, phase)
        val bannedSpecies = generateBannedPokemon(run, phase, forcedSpecies)
        val options = getSpeciesAvailableForPhase(phase, run)
        return PhaseSetupDto(
            phase = run.currentPhase.name,
            type = run.type.name,
            forced = forcedSpecies,
            banned = bannedSpecies,
            options = options - bannedSpecies.toSet() - forcedSpecies.toSet(),
            selectCount = 8 - forcedSpecies.size,
        )
    }

    /**
     * Stores the team for the current phase. Fails if the team is already set.
     */
    fun storeTeamForPhase(runId: Int, team: List<SpeciesDto>) {
        val run = allRuns.first { it.id == runId }
        val phase = run.currentPhase

    }

//    fun preparePhaseRecording(runId: Int): PhaseRecordDto {
//        val run = allRuns.first { it.id == runId }
//        val phase = run.currentPhase
//        return PhaseRecordDto(phase, )
//    }

    fun recordRunBoss() {

    }
}
