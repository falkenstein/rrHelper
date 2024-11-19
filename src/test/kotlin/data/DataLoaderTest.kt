package data

import org.junit.Test

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
        val alreadyProcessed = species.filter { it.id <= 852 }
        val firstPhaseFrequency = EType.entries.associateWith { type ->
            alreadyProcessed.count { it.types.contains(type) && it.availability == EGamePhase.MISTY }
        }
        println(firstPhaseFrequency)
    }
}