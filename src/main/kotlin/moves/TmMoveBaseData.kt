package moves

import com.fasterxml.jackson.annotation.JsonProperty
import data.EGamePhase

data class TmMoveBaseData(
    @JsonProperty("name")
    val name: String,
    @JsonProperty("phase")
    val phase: EGamePhase,
)
