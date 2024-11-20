package monotype

import data.DataLoader
import species.SpeciesDto

class MonotypeFlow {

    private val loader = DataLoader()
    private val species = loader.readFullSpeciesDataFromJson()

    fun progressRun(runDto: MonotypeRunDto) {

    }

    fun species(id: Int): SpeciesDto {
        return species.first { it.id == id }
    }

    fun species(name: String): SpeciesDto {
        return species.first { it.name.equals(name, ignoreCase = true) }
    }
}