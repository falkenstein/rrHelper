package data

enum class EGamePhase(
    val levelCap: Int,
) {
    MISTY(27),
    SURGE(34),
    ROCKET(44),
    WEATHER(59),
    KOGA(68),
    BLAINE(76),
    CLAIR(81),
    VICTORY(82),
    ELITE_FOUR(85),
    POST_GAME(100),
}
