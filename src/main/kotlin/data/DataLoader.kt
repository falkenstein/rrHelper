package data

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import species.SpeciesDto
import java.io.File

class DataLoader {

    private val mapper = ObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .registerModule(KotlinModule.Builder().build())

    fun readSpeciesData(): List<SpeciesDto> {
        val csvSpecies = object {}.javaClass.getResourceAsStream("/pokedata/pokemon_species.csv")?.bufferedReader()?.readText()
            ?: error("CSV read failed.")
        val csvNames = object {}.javaClass.getResourceAsStream("/pokedata/pokemon_species_names.csv")?.bufferedReader()?.readText()
            ?: error("CSV read failed.")
        val csvTypes = object {}.javaClass.getResourceAsStream("/pokedata/pokemon_types.csv")?.bufferedReader()?.readText()
            ?: error("CSV read failed.")
        val speciesRows = csvSpecies.split("\n").drop(1)
        val namesIds = csvNames.split("\n").drop(1)
            .filter { it.substringAfter(",").substringBefore(",").toIntOrNull() == 9 }
            .associate { it.substringBefore(",").toInt() to it.substringBeforeLast(",").substringAfterLast(",") }
        val typesIds = csvTypes.split("\n").drop(1)
            .map { it.substringBefore(",").toInt() to it.split(",")[1].toInt() }
            .groupBy { it.first }
            .map { it.key to it.value.map { value -> value.second } }.toMap()
        val species = speciesRows.map { row ->
            val id = row.substringBefore(",").toInt()
            SpeciesDto(
                id = id,
                name = namesIds[id]!!,
                types = typesIds[id]!!.map { typeId -> EType.entries.first { type -> type.id == typeId } },
                availability = EGamePhase.MISTY,
                evolvesFromId = row.split(",")[3].toIntOrNull(),
            )
        }
        return species
    }

    fun readFullSpeciesData(): List<SpeciesDto> {
        val jsonFile = File("speciesData.json")
        val text = jsonFile.readText()
        val allSpecies: List<SpeciesDto> = mapper.readValue(text)
        return allSpecies
    }

    /**
     * Provides offensive type chart combinations.
     */
    fun readTypeChart(): TypeChart {
        val json = object {}.javaClass.getResourceAsStream("/pokedata/typeChart.json")?.bufferedReader()?.readText() ?: error("Json read failed.")
        val typeDataMap: Map<String, Map<String, Float>> = mapper.readValue(json)
        return TypeChart(
            types = typeDataMap.map { parsed ->
                EType.valueOf(parsed.key.substringAfter("TYPE_")) to TypeEffectiveness(parsed.value.map { effect ->
                    EType.valueOf(effect.key.substringAfter("TYPE_")) to effect.value
                }.toMap())
            }.toMap()
        )
    }

    /**
     * Loads the sprite image from the src files.
     */
    fun loadSpeciesSprite(species: SpeciesDto): File {
        val spriteName = "${species.id}_" + species.name + (species.form?.let { "_$it" } ?: "") + (species.region?.let { "_$it" } ?: "") +
                ".png"
        val image = File("src/main/kotlin/sprites/$spriteName")
        return image
    }


    /**
     * Determines whether the name is indeed the given species.
     */
    fun comparePokemonByName(rawName: String, species: SpeciesDto): Boolean {
        if (rawName.contains("-") && !species.name.contains("-")) {
            if (species.region == null && species.form == null) {
                return false
            }
            val mappedSuffix = when (val suffix = rawName.substringAfter("-")) {
                "Alola", "A" -> "Alolan"
                "Hisui", "H" -> "Hisuian"
                "Sevii", "S" -> "Seviian"
                "Galar", "G" -> "Galarian"
                else -> suffix
            }
            return (mappedSuffix == species.region || mappedSuffix == species.form)
                    && rawName.substringBefore("-").equals(species.name, ignoreCase = true)
        } else {
            return rawName.equals(species.name, ignoreCase = true)
        }
    }

    /**
     * Prints and stores a json from the species data.
     */
    fun storeSpeciesDataAsJson(species: List<SpeciesDto>) {
        val json = mapper.writeValueAsString(species)
        val file = File("speciesData2.json")
        file.writeText(json)
    }
}
