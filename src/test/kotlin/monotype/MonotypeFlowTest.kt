package monotype

import data.DataLoader
import data.EGamePhase
import data.EType
import org.junit.Test
import species.SpeciesDto
import kotlin.test.assertEquals

class MonotypeFlowTest {

    val flow = MonotypeFlow()

    @Test
    fun poisonMonotype() {
        val monotype = MonotypeRunDto(
            currentPhase = EGamePhase.MISTY,
            type = EType.POISON,
            starter = flow.species(1), // Bulbasaur
            randomTeam = listOf(
                flow.species("Gulpin"),
                flow.species("Grimer"),
                flow.species("Slowpoke"), // Galarian version TODO implement
                flow.species("Ekans"),
                flow.species("Skorupi"),
                flow.species("Nidoran♀"),
            )
        )
        val phase = EGamePhase.SURGE
        val forcedTeam = flow.generateForcedPokemon(monotype, phase)
        println("Forced: " + forcedTeam)
        val bannedTeam = flow.generateBannedPokemon(monotype, phase, forcedTeam)
        println("Banned: " + bannedTeam)
    }

    @Test
    fun testFindHighestEvolutionForPhase() {
        val species = flow.species("Nidoran♀")
        val type = EType.POISON
        assertEquals(1, flow.getHighestEvolutionForPhase(species, EGamePhase.MISTY, type).size)
        assertEquals(1, flow.getHighestEvolutionForPhase(species, EGamePhase.ROCKET, type).size)
        assertEquals("Nidorina", flow.getHighestEvolutionForPhase(species, EGamePhase.MISTY, type).first().name)
        assertEquals("Nidoqueen", flow.getHighestEvolutionForPhase(species, EGamePhase.ROCKET, type).first().name)
    }
}