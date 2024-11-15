package species

/**
 * Unified tags for the species.
 */
enum class ETag {
    /**
     * This species is totally ignored by all the force mechanics (possibly also bans). Usually it's the total trash that just doesn't matter for the game.
     */
    IGNORED,

    /**
     * The species is only available through a raid.
     */
    RAID,
}
