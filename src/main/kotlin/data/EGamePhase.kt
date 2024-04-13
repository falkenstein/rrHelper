package data

enum class EGamePhase(
    val id: Int,
    val availabilityXmlName: String,
    val levelCap: Int,
) {
    START(0, "0_sevii", 1),
    BROCK(1, "1_brock", 15),
    MISTY(2, "2_misty", 27),
    SURGE(3, "3_surge", 34),
    ERIKA(4, "4_erika", 44),
    GIOVANNI_1(5, "5_giovanni_1", 47),
    GIOVANNI_2(6, "6_giovanni_2", 57),
    SABRINA(7, "7_sabrina", 59),
    KOGA(8, "8_koga", 68),
    BLAINE(9, "9_blaine", 76),
    GIOVANNI_3(10, "10_giovanni_3", 79),
    CLAIR(11, "11_clair", 81),
    BRENDAN(12, "12_brendan", 82),
    ELITE_FOUR(13, "13_elite_four", 85),
    POST_GAME(13, "14_all", 100),
}
