package com.falkenstein.rrweb.gdocs

import data.EType

data class MonotypeReport(
    val type: EType,
    val bosses: List<BossReport>,
)
