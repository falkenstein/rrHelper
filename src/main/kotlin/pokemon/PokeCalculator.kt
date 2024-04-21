package pokemon

import data.EAttribute
import data.EType
import data.TypeChart
import moves.MoveData
import species.FullSpeciesData

class PokeCalculator {
}

/**
 * Calculates a move's damage based on the inputs using the static parts of the GenV damage formula
 * (https://bulbapedia.bulbagarden.net/wiki/Damage#Generation_V_onward).
 * TODO: support natures and items.
 * TODO: implement Ice Face
 */
fun calculateMoveDamage(
    moveData: MoveData,
    userSpeciesData: FullSpeciesData,
    targetInstance: PokemonInstance? = null,
    level: Int,
    typeChart: TypeChart,
    userAbility: String? = null,
    weather: String? = null,
    status: String? = null,
    screen: String? = null,
    targetHp: Int? = null,
): Int {
    if (moveData.split == "SPLIT_STATUS") {
        return 0
    }
    if (userAbility == "ABILITY_SOUNDPROOF" && moveData.flags.contains("Punk Rock Affected Moves")) {
        return 0
    }
    val targetDefense: Int = if (targetInstance?.species == null) {
        83 // Default average for both Def and SpDef.
    } else if (moveData.split == "SPLIT_PHYSICAL") {
        targetInstance.species.attributes[EAttribute.DEFENSE]!!
    } else if (moveData.split == "SPLIT_SPECIAL") {
        targetInstance.species.attributes[EAttribute.SPECIAL_DEFENSE]!!
    } else {
        error("Attempted to get a weird defense combination. Move: $moveData. Target: ${targetInstance.species}")
    }
    val userAttack: Int = when (moveData.split) {
        "SPLIT_PHYSICAL" -> {
            userSpeciesData.attributes[EAttribute.ATTACK]!!
        }

        "SPLIT_SPECIAL" -> {
            userSpeciesData.attributes[EAttribute.SPECIAL_ATTACK]!!
        }

        else -> {
            error("Attempted to get a weird offense combination. Move: $moveData. User: $userSpeciesData")
        }
    }
    val typeEffectiveness = targetInstance?.species?.let {
        calculateTypeEffectiveness(moveData.type, defensiveTypes = it.types, typeChart, userAbility, targetInstance.ability, moveData)
    } ?: 1F
    val baseDamage = (((((2 * level / 5F) + 2) * moveData.power * userAttack / targetDefense) / 50 + 2) * typeEffectiveness * 0.92F).toInt()
    if (targetInstance?.ability == "ABILITY_DISGUISE" && targetHp == targetInstance.hp && baseDamage > 0) {
        return targetInstance.hp / 8 // Disguise overrides standard dmg evaluation.
    }
    if (targetInstance?.ability == "ABILITY_MULTISCALE" && targetHp == targetInstance.hp && baseDamage > 0) {
        return baseDamage / 2 // Multiscale halves damage at full HP.
    }
    if (targetInstance?.ability == "ABILITY_PUNKROCK" && moveData.flags.contains("Punk Rock Affected Moves")) {
        return baseDamage / 2
    }
    return baseDamage
}

fun calculateTypeEffectiveness(
    offensiveType: EType,
    defensiveTypes: List<EType>,
    typeChart: TypeChart,
    userAbility: String? = null,
    targetAbility: String? = null,
    usedMove: MoveData? = null,
): Float {
    if (targetAbility == "ABILITY_BULLETPROOF" && usedMove != null && usedMove.flags.contains("Ball Bomb Moves")) {
        return 0F
    }
    val defensiveAbilityModifier: Float = if (offensiveType == EType.FIRE && targetAbility == "ABILITY_DRYSKIN") {
        1.25F
    } else if (offensiveType == EType.WATER && targetAbility == "ABILITY_DRYSKIN") {
        0F
    } else if (targetAbility == "ABILITY_LEVITATE" && offensiveType == EType.GROUND) {
        0F
    } else if (targetAbility == "ABILITY_EARTHEATER" && offensiveType == EType.GROUND) {
        0F
    } else if (targetAbility == "ABILITY_FLASHFIRE" && offensiveType == EType.FIRE) {
        0F
    } else if (targetAbility == "ABILITY_FLUFFY" && offensiveType == EType.FIRE) {
        2F
    } else if (targetAbility == "ABILITY_HEATPROOF" && offensiveType == EType.FIRE) {
        0.5F
    } else if (targetAbility == "ABILITY_MOTORDRIVE" && offensiveType == EType.ELECTRIC) {
        0F
    } else if (targetAbility == "ABILITY_PURIFYINGSALT" && offensiveType == EType.GHOST) {
        0.5F
    } else if (targetAbility == "ABILITY_SAPSIPPER" && offensiveType == EType.GRASS) {
        0F
    } else if (targetAbility == "ABILITY_STORMDRAIN" && offensiveType == EType.WATER) {
        0F
    } else if (targetAbility == "ABILITY_VOLTABSORB" && offensiveType == EType.ELECTRIC) {
        0F
    } else if (targetAbility == "ABILITY_THICKFAT" && offensiveType in listOf(EType.FIRE, EType.ICE)) {
        0.5F
    } else if (targetAbility == "ABILITY_WATERBUBBLE" && offensiveType == EType.FIRE) {
        0.5F
    } else if (targetAbility == "ABILITY_WELLBAKEDBODY" && offensiveType == EType.FIRE) {
        0F
    } else if (targetAbility == "ABILITY_WATERABSORB" && offensiveType == EType.WATER) {
        0F
    } else if (targetAbility == "ABILITY_WINDRIDER" && offensiveType == EType.FLYING) {
        0F
    } else {
        1F
    }
    val offensiveAbilityModifier = if (userAbility == "ABILITY_WATERBUBBLE" && offensiveType == EType.WATER) {
        2F
    } else {
        1F
    }
    val offenseMap = typeChart.types[offensiveType]?.effectiveness ?: error("Type not found in the chart: $offensiveType")
    val baseEffectiveness = defensiveTypes.map { offenseMap[it] ?: 1F }.reduce { a, b -> a * b }
    if (baseEffectiveness > 1 && (targetAbility == "ABILITY_FILTER" || targetAbility == "ABILITY_PRISMARMOR" || targetAbility == "ABILITY_SOLIDROCK")) {
        return baseEffectiveness * 0.75F
    }
    if (targetAbility == "ABILITY_WONDERGUARD" && baseEffectiveness <= 1) {
        return 0F
    }
    return baseEffectiveness * defensiveAbilityModifier * offensiveAbilityModifier
}

/**
 * Calculates base HP of the pokemon species.
 */
fun calculateHp(hpAttribute: Int, level: Int): Int {
    return (((2 * hpAttribute + 100) * level) / 100) + 10
}
