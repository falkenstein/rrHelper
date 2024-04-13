package moves

import com.fasterxml.jackson.annotation.JsonProperty

data class AllTmMovesData(
    @JsonProperty("moves")
    val moves: List<TmMoveBaseData>,
)
