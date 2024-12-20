package data

import org.junit.Test

class DataLoaderTest {

//    private val loader = DataLoader()

    @Test
    fun testSpeciesLoad() {
//        val species = loader.readSpeciesData()
//        loader.storeSpeciesDataAsJson(species)
    }

//    @Test
//    fun fillFullSpeciesDataWithStats() {
//        val withStats = loader.readSpeciesData()
//        val newData = loader.readFullSpeciesDataFromJson()
//        val updatedWithStats = newData.map { spec ->
//            val total = withStats.first { it.id == spec.id }.statsTotal ?: error("No stats found for ${spec.id}")
//            spec.copy(
//                statsTotal = total,
//            )
//        }
//        loader.storeSpeciesDataAsJson(updatedWithStats)
//    }

    @Test
    fun fillTierProperty() {
//        val species = loader.readFullSpeciesDataFromJson()
//        val speciesWithTier = species.map { spec ->
//            spec.copy(
//                tier = when {
//                    spec.statsTotal < 350 -> 0
//                    spec.statsTotal <= 400 -> 1
//                    spec.statsTotal <= 450 -> 2
//                    spec.statsTotal <= 500 -> 3
//                    else -> 4
//                }
//            )
//        }
//        loader.storeSpeciesDataAsJson(speciesWithTier)
    }

    @Test
    fun testFullSpeciesLoad() {
//        val species = loader.readFullSpeciesDataFromJson()
//        val alreadyProcessed = species.filter { it.id <= 852 }
//        val firstPhaseFrequency = EType.entries.associateWith { type ->
//            alreadyProcessed.count { it.types.contains(type) && it.availability == EGamePhase.MISTY }
//        }
//        println(firstPhaseFrequency)
    }
}