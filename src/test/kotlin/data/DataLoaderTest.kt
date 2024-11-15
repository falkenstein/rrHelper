package data

import org.junit.Test

import org.junit.Assert.*

class DataLoaderTest {

    private val loader = DataLoader()

    @Test
    fun testSpeciesLoad() {
        val species = loader.readSpeciesData()
//        loader.storeSpeciesDataAsJson(species)
    }

    @Test
    fun testFullSpeciesLoad() {
        val species = loader.readFullSpeciesData()
        val fourGens = species.filter { it.id <= 494 }
        val firstPhaseFrequency = EType.entries.associateWith { type ->
            fourGens.count { it.types.contains(type) && it.availability == EGamePhase.MISTY }
        }
        println(firstPhaseFrequency)
    }
}