package species

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import data.EType
import data.TypeDeserializer

data class RawSpeciesData(
    @JsonProperty("name")
    val name: String,
    @JsonProperty("ID")
    val id: Int,
    @JsonProperty("baseHP")
    val baseHp: Int,
    @JsonProperty("baseAttack")
    val baseAttack: Int,
    @JsonProperty("baseDefense")
    val baseDefense: Int,
    @JsonProperty("baseSpAttack")
    val baseSpAttack: Int,
    @JsonProperty("baseSpDefense")
    val baseSpDefense: Int,
    @JsonProperty("baseSpeed")
    val baseSpeed: Int,
    @JsonProperty("abilities")
    val abilities: List<String>,
    @JsonProperty("type1")
    @JsonDeserialize(using = TypeDeserializer::class)
    val type1: EType,
    @JsonProperty("type2")
    @JsonDeserialize(using = TypeDeserializer::class)
    val type2: EType,
    @JsonProperty("levelUpLearnsets")
    val levelUpLearnsets: List<List<Any>>,
    @JsonProperty("TMHMLearnsets")
    val TMHMLearnsets: List<String>,
    @JsonProperty("eggMovesLearnsets")
    val eggMovesLearnsets: List<String>,
    @JsonProperty("tutorLearnsets")
    val tutorLearnsets: List<String>,
    @JsonProperty("evolutionLine")
    val evolutionLine: List<String>,
)
