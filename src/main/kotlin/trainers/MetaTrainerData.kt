package trainers

import com.fasterxml.jackson.annotation.JsonProperty
import data.EGamePhase

data class MetaTrainerData(
    @JsonProperty("name")
    val name: String,
    @JsonProperty("id")
    val id: String,
    @JsonProperty("phase")
    val phase: EGamePhase,
)
