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
        
    }
}