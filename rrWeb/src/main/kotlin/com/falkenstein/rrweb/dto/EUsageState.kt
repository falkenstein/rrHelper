package com.falkenstein.rrweb.dto

enum class EUsageState {
    /**
     * Was among the inactive slots.
     */
    BENCH,

    /**
     * Was on the active team, but didn't see usage in the boss battle.
     */
    UNUSED,

    /**
     * Didn't make it through the fight.
     */
    FAINTED,

    /**
     * Made it through the fight.
     */
    SURVIVED,
}
