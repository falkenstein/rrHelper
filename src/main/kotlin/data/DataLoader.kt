package data

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import gdocs.BossReport
import moves.AllTmMovesData
import moves.MoveAvailabilityData
import moves.MoveData
import pokemon.PokemonInstance
import pokemon.calculateHp
import species.BaseSpeciesData
import species.FullSpeciesData
import species.RawSpeciesData
import trainers.MetaTrainerData
import trainers.RawTrainerData
import trainers.RawTrainerPokemonData
import trainers.TrainerFullData
import java.io.File
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class DataLoader {

    private val mapper = ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    fun readSpeciesData(): List<FullSpeciesData> {
        val json = object {}.javaClass.getResourceAsStream("/pokedata/speciesData.json")?.bufferedReader()?.readText() ?: error("Json read failed.")
        val parsedMap: Map<String, RawSpeciesData> = mapper.readValue(json)
        val rawList = parsedMap.values.toList()
            .filter { !it.name.contains("UNOWN") && !it.name.contains("SILVALLY") && !it.name.contains("MINIOR") }
        val availability = processAvailability()
        val fullList = rawList.mapNotNull { rawData ->
            val baseData = availability.keys.find { compareSpecies(it, rawData) }
            baseData?.let {
                with(rawData) {
                    FullSpeciesData(
                        name = name,
                        id = it.id,
                        attributes = mapOf(
                            EAttribute.HP to baseHp,
                            EAttribute.ATTACK to baseAttack,
                            EAttribute.DEFENSE to baseDefense,
                            EAttribute.SPECIAL_ATTACK to baseSpAttack,
                            EAttribute.SPECIAL_DEFENSE to baseSpDefense,
                            EAttribute.SPEED to baseSpeed,
                        ),
                        abilities = abilities,
                        types = listOf(type1, type2).distinct(),
                        levelUpMoves = levelUpLearnsets.map { rawMove -> Pair(rawMove[0].toString(), rawMove[1].toString().toInt()) },
                        tmMoves = TMHMLearnsets,
                        eggMoves = eggMovesLearnsets,
                        tutorMoves = tutorLearnsets,
                        evolutionLine = evolutionLine,
                        available = availability[baseData]!!,
                        region = baseData.region,
                        form = baseData.form,
                        niceName = baseData.name,
                    )
                }
            }
        }
        return fullList
    }

    /**
     * Prepares all move data.
     */
    fun readMovesData(): List<MoveAvailabilityData> {
        val allMovesJson = object {}.javaClass.getResourceAsStream("/pokedata/moves.json")?.bufferedReader()?.readText() ?: error("Json read failed.")
        val parsedMap: Map<String, MoveData> = mapper.readValue(allMovesJson)
        val tmsJson = object {}.javaClass.getResourceAsStream("/availability/tms.json")?.bufferedReader()?.readText() ?: error("Json read failed.")
        val parsedTms: AllTmMovesData = mapper.readValue(tmsJson)
        return parsedMap.values.map { baseMove ->
            MoveAvailabilityData(
                move = baseMove,
                tmPhase = parsedTms.moves.find { it.name == baseMove.name }?.phase,
            )
        }
    }

    /**
     * Compares whether the two species are the same. We use names, as ID is not reliable.
     */
    private fun compareSpecies(base: BaseSpeciesData, raw: RawSpeciesData): Boolean {
        if (raw.name.contains("MR_")) {
            return if (raw.name.contains("_G")) {
                base.name == "Mr. Mime" && base.region == "Galarian"
            } else {
                base.region == null && base.name.substringAfter("Mr. ").uppercase() == raw.name.substringAfter("MR_")
            }
        }
        if ((base.region == null && base.form == null) || (raw.name == "FRILLISH" || raw.name == "JELLICENT" || raw.name == "TOXTRICITY"
                || base.name == "Meloetta") || base.name == "Pumpkaboo" || base.name == "Aegislash") {
            return if (base.name.contains("-")) {
                base.name.replace("-", "").equals(raw.name.replace("_", ""), ignoreCase = true)
            } else {
                base.name.replace(" ", "").equals(raw.name, ignoreCase = true)
            }
        }
        if (raw.name == "ORICORIO") { // Means the Baile form
            return base.form == "Baile" && base.name.equals(raw.name.substringBefore("_"), ignoreCase = true)
        }
        val suffix = raw.name.substringAfter("_")
        if (suffix == "MEGA") {
            return base.form == "Mega" && base.name.equals(raw.name.substringBeforeLast("_"), ignoreCase = true)
        } else if (base.name == "Rotom" || base.name == "Shellos" || base.name == "Gastrodon" || base.name == "Sawsbuck" || base.name == "Pikachu") {
            return base.form?.replace(" ", "_").equals(suffix, ignoreCase = true) && base.name.equals(raw.name.substringBeforeLast("_"), ignoreCase = true)
        } else if (base.form == "Incarnate") {
            return base.name.equals(raw.name.substringBeforeLast("_"), ignoreCase = true)
        } else if (suffix != raw.name) { // Meaning the suffix actually does exist.
            when (suffix) {
                "F", "FEMALE" -> {
                    return base.form == "Female" && base.name.equals(raw.name.substringBeforeLast("_"), ignoreCase = true)
                }
                "M" -> {
                    return base.form == "Male" && base.name.equals(raw.name.substringBeforeLast("_"), ignoreCase = true)
                }
                "H" -> {
                    return base.region == "Hisuian" && base.name.equals(raw.name.substringBeforeLast("_"), ignoreCase = true)
                }
                "S" -> {
                    return (base.region == "Seviian" && base.name.equals(raw.name.substringBeforeLast("_"), ignoreCase = true))
                        || (base.form == "Sensu" && base.name.equals(raw.name.substringBefore("_"), ignoreCase = true))
                }
                "G" -> {
                    return base.region == "Galarian" && base.name.equals(raw.name.substringBeforeLast("_"), ignoreCase = true)
                }
                "A" -> { // Oricorio forms
                    return base.region == "Alolan" && base.name.equals(raw.name.substringBeforeLast("_"), ignoreCase = true)
                }
                "Y" -> {
                    return base.form == "Pom-Pom" && base.name.equals(raw.name.substringBefore("_"), ignoreCase = true)
                }
                "P" -> {
                    return base.form == "Pa'u" && base.name.equals(raw.name.substringBefore("_"), ignoreCase = true)
                }
            }
        }
        return false
    }

    /**
     * Processes all the availabilities into one simple map.
     */
    private fun processAvailability(): Map<BaseSpeciesData, EGamePhase> {
        val speciesAvailable = mutableMapOf<BaseSpeciesData, EGamePhase>()
        val parsedAvailabilities = EGamePhase.entries.associateWith {
            readAvailability("/availability/" + it.availabilityXmlName + ".xml")
        }
        EGamePhase.entries.forEach { phase ->
            parsedAvailabilities[phase]!!.forEach { species ->
                speciesAvailable.putIfAbsent(species, phase)
            }
        }
        return speciesAvailable
    }

    /**
     * Reads the pseudo-xml file and returns basic data for which pokemon are available in which phase of the game.
     */
    @OptIn(ExperimentalEncodingApi::class)
    fun readAvailability(fileName: String, saveSprites: Boolean = false): List<BaseSpeciesData> {
        val foundSpecies = mutableListOf<BaseSpeciesData>()
        val lines = object {}.javaClass.getResourceAsStream(fileName)?.bufferedReader()?.readLines() ?: return emptyList()
        var id = 0
        var lastSpriteUrl = ""
        for (i in lines.indices) {
            if (lines[i].contains("speciesDexIDWrapper")) {
                id = lines[i].substringBeforeLast("<").substringAfter(">").toInt()
            }
            if (saveSprites && lines[i].contains("data:image")) {
                lastSpriteUrl = lines[i].substringBeforeLast("\">").substringAfter("src=\"")
            }
            if (lines[i].contains("speciesNameName")) {
                val name = lines[i].substringBeforeLast("<").substringAfter(">")
                val form = if (lines[i + 1].contains("speciesNameForm")) {
                    lines[i + 1].substringBeforeLast("<").substringAfter(">")
                } else {
                    null
                }
                val region = if (lines[i - 1].contains("speciesNameRegion")) {
                    lines[i - 1].substringBeforeLast("<").substringAfter(">")
                } else {
                    null
                }
                val species = BaseSpeciesData(id, name, form, region)
                foundSpecies.add(species)
                if (saveSprites) {
                    val imageData = Base64.decode(lastSpriteUrl.substringAfter("data:image/png;base64,"))
                    val nameFormAppendix = if (species.form != null) {
                        "_${species.form}"
                    } else {
                        ""
                    }
                    val nameRegionAppendix = if (species.region != null) {
                        "_${species.region}"
                    } else {
                        ""
                    }
                    val file = File("${species.id}_${species.name}$nameFormAppendix$nameRegionAppendix.png")
                    file.writeBytes(imageData)
                }
            }
        }
        return foundSpecies
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
     * Only holds information about the boss trainers.
     */
    fun readTrainers(
        allSpeciesData: List<FullSpeciesData> = readSpeciesData(),
        moves: List<MoveAvailabilityData> = readMovesData(),
    ): List<TrainerFullData> {
        val rawText = object {}.javaClass.getResourceAsStream("/trainers/trainers.txt")?.bufferedReader()?.readText() ?: error("Txt read failed.")
        val individualRawTrainers = rawText.split("\n\n")
        return individualRawTrainers.map { trainerBlock ->
            val lines = trainerBlock.split("\n")
            val boss = EBoss.valueOf(lines.first().trim().replace(" ", "_"))
            val pokemon = lines.drop(1).filter { it.startsWith("-") }.mapNotNull { line ->
                val parts = line.substringAfter("-").trim().split(":")
                trainerDataToInstance(
                    name = parts[0],
                    moves = parts[2].substringBefore("]").substringAfter("[").split(","),
                    ability = parts[1],
                    allSpeciesData = allSpeciesData,
                    allMoves = moves,
                    level = boss.phase.levelCap,
                )
            }
            TrainerFullData(
                boss = boss,
                pokemon = pokemon,
            )
        }
    }

    private fun trainerDataToInstance(
        name: String,
        moves: List<String>,
        ability: String,
        allSpeciesData: List<FullSpeciesData>,
        allMoves: List<MoveAvailabilityData>,
        level: Int,
    ): PokemonInstance? {
        try {
            val species = allSpeciesData.first { comparePokemonByName(name, it) }
            var hiddenPowerType: EType? = null
            val knownMoves = moves.mapNotNull { trainerMove ->
                val found = allMoves.find { trainerMove.equals(it.move.ingameName, ignoreCase = true) }
                val result = found
                    ?: if (trainerMove.contains("Draining Kiss")) {
                        allMoves.first { it.move.name == "MOVE_DRAININGKISS" }
                    } else if (trainerMove.contains("Hidden Power")) {
                        hiddenPowerType = EType.valueOf(trainerMove.substringAfterLast(" ").uppercase())
                        allMoves.first { it.move.name == "MOVE_HIDDENPOWER" }
                    } else {
                        null
                    }
                result
            }
            return PokemonInstance(
                species = species,
                level = level,
                ability = ability,
                hp = calculateHp(species.attributes[EAttribute.HP]!!, level),
                knownMoves = knownMoves.map { it.move },
                hiddenPowerType = hiddenPowerType,
            )
        } catch (e: Exception) {
            println("Issues for $name")
            return null
        }
    }

    /**
     * Determines whether the name is indeed the given species.
     */
    fun comparePokemonByName(rawName: String, species: FullSpeciesData): Boolean {
        if (rawName.contains("-") && !species.niceName.contains("-")) {
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
                && rawName.substringBefore("-").equals(species.niceName, ignoreCase = true)
        } else {
            return rawName.equals(species.niceName, ignoreCase = true)
        }
    }

    /**
     * Loads the sprite image from the src files.
     */
    fun loadSpeciesSprite(fullSpecies: FullSpeciesData): File {
        val spriteName = "${fullSpecies.id}_" + fullSpecies.niceName + (fullSpecies.form?.let { "_$it" } ?: "") + (fullSpecies.region?.let { "_$it" } ?: "") +
            ".png"
        val image = File("src/main/kotlin/sprites/$spriteName")
        return image
    }
}
