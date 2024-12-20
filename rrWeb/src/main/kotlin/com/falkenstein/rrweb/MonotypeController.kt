package com.falkenstein.rrweb

import com.falkenstein.rrweb.dto.BasicRunResponseDto
import com.falkenstein.rrweb.dto.PhaseSetupDto
import com.falkenstein.rrweb.dto.SelectedTeamDto
import com.falkenstein.rrweb.dto.StartRunInputDto
import data.EType
import com.falkenstein.rrweb.monotype.SearchResultsDto
import com.falkenstein.rrweb.monotype.StartingOptionsDto
import org.springframework.web.bind.annotation.*
import species.SpeciesDto

@RestController
class MonotypeController(
    private val monotypeManager: MonotypeManager,
) {

    @GetMapping("/run/load")
    fun loadAllRuns(): BasicRunResponseDto {
        return BasicRunResponseDto(monotypeManager.getBasicRunData().toSet())
    }

    @GetMapping("/run/start")
    fun getStartingOptions(@RequestParam type: String): StartingOptionsDto {
        println("Request for type: $type")
        val eType = EType.valueOf(type.uppercase())
        return monotypeManager.getStartingOptions(eType)
    }

    @PostMapping("/run/start")
    fun startNewRun(
        @RequestBody input: StartRunInputDto,
    ): Int {
        println("POST for start run. Type: ${input.type}, selectedNames: ${input.selectedNames}")
        return monotypeManager.startNewRun(input.selectedNames, EType.valueOf(input.type.uppercase()))
    }

    @GetMapping("/run/search")
    fun getSearchOptions(@RequestParam type: String, @RequestParam search: String): SearchResultsDto {
        println("Search for type: $type & text: $search")
        val eType = EType.valueOf(type.uppercase())
        return monotypeManager.getSearchedSpecies(eType, search)
    }

    @GetMapping("/run/setup")
    fun getSetupPhaseOptions(
        @RequestParam runId: Int,
    ): PhaseSetupDto {
        return monotypeManager.setupRunPhase(runId)
    }

    @PostMapping("/run/setup")
    fun setupPhaseTeam(
        @RequestBody dto: SelectedTeamDto,
    ) {
        monotypeManager.storeTeamForPhase(dto.runId, dto.team)
    }
}
