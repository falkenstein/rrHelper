package data

enum class EGamePhase(
    val levelCap: Int,
    /**
     * The lowest tier that is permitted for the given phase.
     */
    val minTier: Int = 4,
) {
    MISTY(27, 1),
    SURGE(34, 2),
    ROCKET(44, 3),
    WEATHER(59),
    KOGA(68),
    BLAINE(76),
    CLAIR(81),
    VICTORY(82),
    ELITE_FOUR(85),
    POST_GAME(100),
}
