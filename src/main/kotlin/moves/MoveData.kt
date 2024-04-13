package moves

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import data.EType
import data.TypeDeserializer

data class MoveData(
    @JsonProperty("name")
    val name: String,
    @JsonProperty("type")
    @JsonDeserialize(using = TypeDeserializer::class)
    val type: EType,
    @JsonProperty("ingameName")
    val ingameName: String,
    @JsonProperty("effect")
    val effect: String,
    @JsonProperty("power")
    val power: Int,
    @JsonProperty("target")
    val target: String,
    @JsonProperty("flags")
    val flags: List<String>,
    @JsonProperty("split")
    val split: String,
)
