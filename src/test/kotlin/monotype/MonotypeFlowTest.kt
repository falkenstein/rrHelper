package monotype

import data.DataLoader
import data.EGamePhase
import data.EType
import species.SpeciesDto

class MonotypeFlowTest {

    val flow = MonotypeFlow()

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
                flow.species("NidoranF"),
            )
        )
    }
}