package data

import org.junit.Test

import org.junit.Assert.*

class DataLoaderTest {

    private val loader = DataLoader()

    @Test
    fun readTrainers() {
        loader.readTrainers()
    }
}