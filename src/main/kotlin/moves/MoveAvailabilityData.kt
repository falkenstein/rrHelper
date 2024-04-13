package moves

import data.EGamePhase

data class MoveAvailabilityData(
    val move: MoveData,
    val tmPhase: EGamePhase?,
)
