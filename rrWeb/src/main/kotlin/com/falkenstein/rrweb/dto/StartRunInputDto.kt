package com.falkenstein.rrweb.dto

data class StartRunInputDto(
    val type: String,
    /**
     * No reason to differentiate starter and random6 - the impact on the rest of the game is exactly the same.
     */
    val selectedNames: List<String>,
)
